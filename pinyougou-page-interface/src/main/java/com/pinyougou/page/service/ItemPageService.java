package com.pinyougou.page.service;

public interface ItemPageService {
	
	/**根据商品id生成商品详情页（使用goodsId查询goods表、goodsDesc表信息）
	 * 并填入模板
	 * @param goodsId 商品id
	 * @return
	 */
	public boolean genItemHtml(Long goodsId);
	
	
	/** 根据被删除商品id集合，批量删除商品详情页。
	 * @param goodsIds 被删除商品的id集合
	 * @return 删除成功返回true反之返回false
	 */
	public boolean deleteItemHtml(Long[] goodsIds);
}
