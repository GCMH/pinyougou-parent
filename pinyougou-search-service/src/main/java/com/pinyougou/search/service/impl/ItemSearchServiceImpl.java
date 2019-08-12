package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service
public class ItemSearchServiceImpl implements ItemSearchService{
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	@Override
	public Map search(Map searchMap) {
		Map map = new HashedMap();
//		Query query = new SimpleQuery("*:*");
//		
//		//根据复制域搜索
//		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
//		query.addCriteria(criteria );
//		
//		ScoredPage<TbItem> page = solrTemplate.queryForPage(query , TbItem.class);
//		
//		//将结果放入map并返回
//		map.put("rows", page.getContent());
//		return map;	
		
		//查询列表
		map.putAll(searchList(searchMap));
		//分组查询
		map.put("categoryList", searchCategoryList(searchMap));
		return map;
	}
	
	/**根据关键字查询复制域，附加高亮显示
	 * @param searchMap 查询参数 keywords:searchParameter
	 * @return 
	 */
	private Map searchList(Map searchMap) {
		Map map = new HashMap();
		
		//高亮查询
		HighlightQuery query = new SimpleHighlightQuery();
		//高亮选项,指定在item_title上添加高亮
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
		highlightOptions.setSimplePrefix("<em style='color:red'>");//设置前缀
		highlightOptions.setSimplePostfix("</em>");
		//设置高亮查询选项
		query.setHighlightOptions(highlightOptions );
		
		//根据复制域搜索,设置搜索域，及搜索内容
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria );
		
		//获取高亮结果
		//获取高亮查询结果
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		//获取高亮入口列表，包含每条记录，及其高亮入口
		List<HighlightEntry<TbItem>> highlightList = page.getHighlighted();
		//每一个个查询结果的高亮入口
		for(HighlightEntry<TbItem> highlightEntry :highlightList) {//highlightEntry：每一个结果+高亮的集合
			//获取高亮入口中的高亮集合,（包含所有高亮域）
			List<Highlight> highlights = highlightEntry.getHighlights();
			//遍历单个结果中的高亮集合，遍历设置的高亮域
//					for(Highlight highlight : highlights) {
//						List<String> snipplets = highlight.getSnipplets();//每个域可能存在多值
//						for(String snipptle : snipplets) {
//							System.out.println(snipptle);
//						}
//					}
			
			TbItem item = highlightEntry.getEntity();
			
			//由于只设置了一个高亮域，可直接highlights.get(0)获取高亮域，
			if(highlights.size() > 0 && highlights.get(0).getSnipplets().size() > 0)
			item.setTitle(highlights.get(0).getSnipplets().get(0));
			//System.out.println(highlights.get(0).getSnipplets().get(0));
		}
		
		map.put("rows", page.getContent());
		return map;	
	}
	
	private List<String> searchCategoryList(Map searchMap){
		List<String> list = new ArrayList<String>();
		
		Query query = new SimpleQuery("*:*");
		
		//设置分组选项
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions );
		
		//根据复制域搜索,设置搜索域，及搜索内容
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria );
		
		//获取分组页
		GroupPage<TbItem> queryForGroupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
		//根据指定分组获取分组结果
		GroupResult<TbItem> groupResult = queryForGroupPage.getGroupResult("item_category");
		//获取分页结果入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		//根据分页结果入口页获取内容集合
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		//迭代
		for(GroupEntry<TbItem> item : content) {
			list.add(item.getGroupValue());
		}
		
		return list;
		
	}

}
