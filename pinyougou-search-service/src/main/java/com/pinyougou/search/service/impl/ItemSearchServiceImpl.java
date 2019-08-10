package com.pinyougou.search.service.impl;

import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialArray;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

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
		Query query = new SimpleQuery("*:*");
		
		//根据复制域搜索
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria );
		
		ScoredPage<TbItem> page = solrTemplate.queryForPage(query , TbItem.class);
		
		//将结果放入map并返回
		map.put("rows", page.getContent());
		return map;
	}

}
