package com.pinyougou.cart.service;

import java.util.List;

import com.pinyougou.pojogroup.Cart;

public interface CartService {
	
	/**将商品添加到购物车
	 * @param list 购物车对象列表
	 * @param itemId 商品id（SKU ID）
	 * @param num 商品数量
	 * @return
	 */
	public List<Cart> addGoodsToCartList(List<Cart> list, Long itemId, Integer num);
	
	/**从redis中查询购物车
	 * @param username 用户名
	 * @return 如果有购物车则返回对应购物车列表，如果没有则返回空list
	 */
	public List<Cart> findCartListFromRedis(String username);
	
	/**将购物车保存到redis
	 * @param username 用户名
	 * @param cartList 购物车列表
	 */
	public void saveCartListToRedis(String username,List<Cart> cartList);
	
	/**
	 * 合并购物车
	 * @param cartList1
	 * @param cartList2
	 * @return 返回合并后的购物车
	 */
	public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);

}
