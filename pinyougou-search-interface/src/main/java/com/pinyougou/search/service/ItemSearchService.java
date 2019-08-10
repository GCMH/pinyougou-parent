package com.pinyougou.search.service;

import java.util.Map;

public interface ItemSearchService {
	
	/**搜索方法
	 * @param searchMap 传递过来的搜索条件（Map）
	 * @return 返回Map结果
	 */
	public Map search(Map searchMap);
}
