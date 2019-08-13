package com.pinyougou.page.service;

public interface ItemPageService {
	
	/**根据商品id生成商品详情页（使用goodsId查询goods表、goodsDesc表信息）
	 * 并填入模板
	 * @param goodsId 商品id
	 * @return
	 */
	public boolean genItemHtml(Long goodsId);
}
