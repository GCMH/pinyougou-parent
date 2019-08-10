package com.pinyougou.solrutil;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

@Component
public class SolrUtil {
	@Autowired
	private SolrTemplate solrTemplate;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	public void importItemData() {
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");//只查询状态为1的商品信息
		List<TbItem> itemList = itemMapper.selectByExample(example );
		System.out.println("-----商品列表开始");
		for(TbItem item : itemList) {
			System.out.println(item.getId() + "-" + item.getTitle() + "-" + item.getPrice());
			Map map = JSON.parseObject(item.getSpec(), Map.class);//解析规格字符串，
			item.setMap(map);//将其设置为TbItem中的map，用于动态生成规格spec_*(*:网络，内存..)
		}
		System.out.println("-----商品列表结束");
		solrTemplate.saveBeans(itemList);
		solrTemplate.commit();
		
	}
	
	
	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		//标识了bean但未配置bean名称，默认为首字母小写，其余字母不变
		SolrUtil solrUtil = (SolrUtil)applicationContext.getBean("solrUtil");
		solrUtil.importItemData();
	}
}
