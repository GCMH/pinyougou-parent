package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;

import entity.Result;

@RestController
@RequestMapping("/cart")
public class CartController {
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;
	
	@Reference
	private CartService cartService;
	
	/**取出cookie中购物车信息，将其转换为购物车列表
	 * @return 购物车列表
	 */
	@RequestMapping("/findCartList")
	public List<Cart> findCartList(){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		
		String cartListString = util.CookieUtil.getCookieValue(request, "cartList","UTF-8");
		if(cartListString==null || cartListString.equals("")){
			cartListString="[]";
		}
		//将cookie存储的购物车信息转换为列表
		List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
		
		if("anonymousUser".equals(username)) {//未登录，向cookie中获取购物车数据
			System.out.println("从cookie中取出购物车数据");
			return cartList_cookie;	
		}else {//已登录，向reids中获取购物车数据
			
			List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
			System.out.println("从redis中取出购物车数据");
			if(cartList_cookie.size() > 0) {//如果本地cookie购物车有数据才进行合并存储清除操作
				//合并购物车
				cartList_redis = cartService.mergeCartList(cartList_cookie, cartList_redis);
				System.out.println("合并购物车数据");
				//清除本地cookie存储的购物车
				util.CookieUtil.deleteCookie(request, response, "cartList");
				//将合并购物车存储在reids中
				cartService.saveCartListToRedis(username, cartList_redis);
			}
			return cartList_redis;
		}
	}
	
	@RequestMapping("/addGoodsToCartList")//addGoodsToCartList
	@CrossOrigin(origins="http://localhost:9105",allowCredentials="true")//等价下列1,2设置允许跨域请求
	public Result addGoodsToCartList(Long itemId,Integer num){
		//设置允许http://localhost:9105跨域请求9107
		//response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105"); //1
		//response.setHeader("Access-Control-Allow-Credentials", "true");//如果对cookie进行操作需加上 //2
		
		
		try {		
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			System.out.println("username:" + username);
			List<Cart> cartList =findCartList();//获取购物车列表,登录状态下从redis中获取，未登录状态下从cookie中获取
			//将商品放入购物车，返回更新后购物车对象
			cartList = cartService.addGoodsToCartList(cartList, itemId, num);
			System.out.println("向cookie中添加购物车数据");
			if("anonymousUser".equals(username)) {//未登录，向cookie中存入购物车数据
				//将更新后购物车对象转换为JSON字符串，保存在cookie中
				util.CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList),3600*24,"UTF-8");
			}else {//已登录向 redis中存入购物车数据
				cartService.saveCartListToRedis(username, cartList);
			}

			return new Result(true, "添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "添加失败");
		}
	}	
}
