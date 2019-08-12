package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
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
		
		//1.查询列表
		map.putAll(searchList(searchMap));
		//2.分组查询,查询商品分类列表用于前端显示分类（如手机、电脑）
		List<String> categoryList = searchCategoryList(searchMap);
		map.put("categoryList", categoryList);//将根据category分组后的数据返回前端
		
		//3查询品牌和服务列表
		String category = (String) searchMap.get("category");
		if(!"".equals(category)) {//如果指定了品牌则按指定品牌查询
			Map brandAndSpecMap = searchBrandAndSpecList(category);//多个分类以第一个为准
			map.putAll(brandAndSpecMap);
		}else {//放在则按默认品牌查询
			if(categoryList.size() > 0) {//默认情况下按分类列表第一个分类进行查询
				//根据分类列表查询对应品牌及规格（例如根据手机查询出品牌：华为三星、苹果，规格：内存、网络制式）
				Map brandAndSpecMap = searchBrandAndSpecList(categoryList.get(0));//多个分类以第一个为准
				map.putAll(brandAndSpecMap);
			}
		}
		
		
		return map;
	}
	
	/**根据关键字查询复制域，附加高亮显示
	 * @param searchMap 查询参数 keywords:searchParameter
	 * @return 
	 */
	private Map searchList(Map searchMap) {
		Map map = new HashMap();
		
		//空格处理
		String keywords = (String) searchMap.get("keywords");
		//放入去除空格后的关键字搜索
		searchMap.put("keywords", keywords.replace(" ", ""));
		
		
		
		//高亮查询
		HighlightQuery query = new SimpleHighlightQuery();
		//高亮选项,指定在item_title上添加高亮
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
		highlightOptions.setSimplePrefix("<em style='color:red'>");//设置前缀
		highlightOptions.setSimplePostfix("</em>");
		//设置高亮查询选项
		query.setHighlightOptions(highlightOptions );
		
		//1.1关键字查询
		//根据复制域搜索,设置搜索域，及搜索内容
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria );
		
		//1.2根据searchMap中的category进行过滤查询
		if(!"".equals(searchMap.get("category"))) {
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
			filterQuery.addCriteria(filterCriteria );
			query.addFilterQuery(filterQuery);
		}
		
		//1.3根据searchMap中的brand进行过滤查询
		if(!"".equals(searchMap.get("brand"))) {
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
			filterQuery.addCriteria(filterCriteria );
			query.addFilterQuery(filterQuery);
		}
		
		//1.4根据searchMap中的spec进行过滤查询
		if(null != searchMap.get("spec")) {
			Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
			for(String key : specMap.keySet()) {
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
				filterQuery.addCriteria(filterCriteria );
				query.addFilterQuery(filterQuery);
			}
		}
		
		//1.5根据价格区间查询
		if(!"".equals(searchMap.get("price"))) {
			String[] price = ((String)searchMap.get("price")).split("-");
			if(!"0".equals(price[0])) {//如果下界不等于0，指定查询访问为大于下界，如果下界等于0，不指定下界，即只搜索小于上界的结果。
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_price").greaterThan(price[0]);
				filterQuery.addCriteria(filterCriteria );
				query.addFilterQuery(filterQuery);
			}
			if(!"*".equals(price[1])) {//如果上界不等于*，指定查询范围为小于上界，如果上界等于*，不指定上界，即只搜索大于下界的结果。
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_price").lessThan(price[1]);
				filterQuery.addCriteria(filterCriteria );
				query.addFilterQuery(filterQuery);
			}
			
		}
		
		
		//1.6分页
		Integer pageNo = (Integer)searchMap.get("pageNo");//页码
		if(pageNo == null) {
			pageNo = 1;
		}
		Integer pageSize = (Integer)searchMap.get("pageSize");//每页记录数
		if(pageSize == null) {
			pageSize = 20;
		}
		//设置偏移起始位置
		query.setOffset((pageNo - 1) * pageSize);
		query.setRows(pageSize);
		
		//1.7字段排序
		String sortValue = (String)searchMap.get("sort");//排序规格，升序降序
		String sortField = (String)searchMap.get("sortField");//排序字段
		if(sortValue != null && !sortValue.equals("")) {
			if(sortValue.equals("ASC")) {
				Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
				query.addSort(sort );
			}
			
			if(sortValue.equals("DESC")) {
				Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
				query.addSort(sort );	
			}
			
		}
		
		
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
		
		//设置分页相关信息
		map.put("totalPages", page.getTotalPages());//总页数
		map.put("total", page.getTotalElements());//总记录数
		
		map.put("rows", page.getContent());
		return map;	
	}
	
	
	/**根据item_category进行分组查询
	 * @param searchMap 查询条件
	 * @return
	 */
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
	
	@Autowired
	private RedisTemplate redisTemplate;
	/**通过商品分类名查询对应模板ID
	 * 再通过模板ID在模板表中查询对应品牌列表和规格列表（规格列表还需通过规格ID查询规格选项列表）。
	 * @param category 商品分类名（item_cat表中name属性）
	 * @return 返回改商品分类对应的品牌列表和规格列表
	 */
	private Map searchBrandAndSpecList(String category) {
		Map map = new HashMap();
		
		//1.根据商品分类获取模板ID，该数据在sellerGoodsService->ItemCatServiceImpl中写入
		Long typeTemplateId = (Long)redisTemplate.boundHashOps("itemCat").get(category);
		if(typeTemplateId != null) {
			//品牌列表、规格列表在sellerGoodsService->saveToRedis()中写入
			//2.根据模板ID获取品牌列表
			List brandList = (List)redisTemplate.boundHashOps("brandList").get(typeTemplateId);
			map.put("brandList", brandList);
			
			//3.根据模板ID获取规格列表
			List specList = (List)redisTemplate.boundHashOps("specList").get(typeTemplateId);
			map.put("specList", specList);
			
			//System.out.println(brandList.size() + " " + specList);
		}

		return map;
	}

	@Override
	public void importList(List list) {
		// TODO Auto-generated method stub
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}

	@Override
	public void deleteByGoodsIds(List goodsIds) {
		Query query = new SimpleQuery("*:*");
		
		//指定列为item_goodsid
		Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
		query.addCriteria(criteria );
		//删除所有goodsIds
		solrTemplate.delete(query);
		solrTemplate.commit();
	}

}
