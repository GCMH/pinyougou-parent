package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
	
	/**搜索方法
	 * @param searchMap 传递过来的搜索条件（Map）
	 * @return 返回Map结果
	 */
	public Map search(Map searchMap);
	
	
	/** 将list导入solr
	 * @param list 将要导入solr的数据
	 */
	public void importList(List list);
	
	
	/**根据goodsids删除solr中数据 delete in(goodsids)
	 * @param goodsIds 商品id（SPU的id）
	 */
	public void deleteByGoodsIds(List goodsIds);
}
