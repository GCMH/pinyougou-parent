package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;

@Service
public class CartServiceImpl implements CartService{

	@Autowired
	private TbItemMapper itemMapper;
	
	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
		
		//1、根据SKUID查询商品明细信息
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		if(item == null || !"1".equals(item.getStatus())) {
			throw new RuntimeException("该商品不存在或者商品状态错误！");
		}
		
		
		
		
		//2、根据SKU对象得到商家ID信息
		String sellerId = item.getSellerId();
		
		//3.根据商家ID查询购物车中商家购物车对象
		Cart cart = searchCartFromCartListBySellerId(cartList,sellerId);
		if(cart == null) {//4、如果没有改商
			//4.1新建一个该商家购物车
			cart = new Cart();
			cart.setSellerId(sellerId);
			cart.setSellerName(item.getSeller());	
			//4.2 新建商品明细对象
			TbOrderItem orderItem = createOrderItem(item,num);
			
			//4.3将商品明细添加到指定购物车
			//4.3.1创建该商家对应的购物车列表
			cart.setOrderItemList(new ArrayList<TbOrderItem>());
			cart.getOrderItemList().add(orderItem);
			
			//将商家购物车添加到购物车
			cartList.add(cart);
		}else {//5.如果存在该商家
			TbOrderItem orderItem = searchOrderItemFromOrderItemListByItemId(cart.getOrderItemList(),itemId);
			if(orderItem == null) {//5.1如果该商品不存在，新建商品对象并添加
				orderItem = createOrderItem(item, num);
				cart.getOrderItemList().add(orderItem);
			}else{//5.2如果该商品存在 ，数量+1
				orderItem.setNum(orderItem.getNum() + num);//更新商品数量
				//更新商品价格
				orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
				
				if(orderItem.getNum().equals(0)) {//如果商品中数量为0
					cart.getOrderItemList().remove(orderItem);//删除商品
				}
				if(cart.getOrderItemList().size() == 0) {//如果商家购物车中的商品列表为空
					cartList.remove(cart);//删除该商家购物车
				}
			}
		}

		return cartList;
	}
	
	
	/**创建商品明细对象
	 * @param item 商品（SKU）
	 * @param num 选择（购买）商品数量
	 * @return 商品明细对象
	 */
	private TbOrderItem createOrderItem(TbItem item, Integer num) {
		if(num<=0){
			throw new RuntimeException("数量非法");
		}
		
		//将商品相关信息添加到商品明细对象中，并返回
		TbOrderItem orderItem=new TbOrderItem();
		orderItem.setGoodsId(item.getGoodsId());
		orderItem.setItemId(item.getId());
		orderItem.setNum(num);
		orderItem.setPicPath(item.getImage());
		orderItem.setPrice(item.getPrice());
		orderItem.setSellerId(item.getSellerId());
		orderItem.setTitle(item.getTitle());
		orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
		return orderItem;
	}

	/**
	 * 从购物车列表中查找对应商家购物车
	 * @param cartList 购物车列表（包含所有选中物品，购物车列表由多个商家购物车组成）
	 * @param sellerId 商家id
	 * @return 如果购物车列表中有对应商家购物车则返回对应商家购物车，反之返回null
	 */
	private Cart searchCartFromCartListBySellerId(List<Cart> cartList, String sellerId) {
		for(Cart cart : cartList) {
			if(cart.getSellerId().equals(sellerId)) {
				return cart;
			}
		}
		return null;
	}
	
	
	/**查询具体商品是否在对应商家购物车列表中
	 * @param orderItemList 商家购物车列表
	 * @param itemId 查询item
	 * @return 如果存在返回对应商品，反之返回null
	 */
	private TbOrderItem searchOrderItemFromOrderItemListByItemId(List<TbOrderItem> orderItemList, Long itemId) {
		for(TbOrderItem orderItem : orderItemList) {
			if(orderItem.getItemId().equals(itemId)) {
				return orderItem;
			}
		}
		return null;
	}

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	public List<Cart> findCartListFromRedis(String username) {
		System.out.println("从redis中提取购物车数据....."+username);
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
		if(cartList==null){
			cartList=new ArrayList<Cart>();
		}
		return cartList;

	}


	@Override
	public void saveCartListToRedis(String username, List<Cart> cartList) {
		System.out.println("向redis存入购物车数据....."+username);
		redisTemplate.boundHashOps("cartList").put(username, cartList);
	}


	@Override
	public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
		for(Cart cart : cartList2) {
			for(TbOrderItem orderItem : cart.getOrderItemList()) {
				cartList1 = addGoodsToCartList(cartList1, orderItem.getItemId(), orderItem.getNum());
			}
		}
		return cartList1;
	}

	
}
