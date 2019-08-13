package com.pinyougou.manager.controller;
import java.util.Arrays;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
//import com.pinyougou.page.service.ItemPageService;//后续使用消息队列解耦
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
//import com.pinyougou.search.service.ItemSearchService; //后续使用消息队列解耦
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * jms模板
	 */
	@Autowired
	private JmsTemplate jmsTemplate;
	
	/**
	 * 存放删除solr消息的消息队列
	 */
	@Autowired
	private Destination queueSolrDeleteDestination;
	
	/**
	 * 存放向solr中添加数据的消息
	 */
	@Autowired
	private Destination queueSolrDestination;
	
	/**
	 * 存放生成静态页消息
	 */
	@Autowired
	private Destination topicPageDestination;
	
	/**
	 * 存放删除静态页消息
	 */
	@Autowired
	private Destination topicPageDeleteDestination;
	
//	@Reference(timeout=100000) //后续使用消息队列解耦
//	private ItemSearchService itemSearchService;
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	
//manager 没有商品添加功能，可注释	
//	@RequestMapping("/add")
//	public Result add(@RequestBody TbGoods goods){
//		try {
//			goodsService.add(goods);
//			return new Result(true, "增加成功");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new Result(false, "增加失败");
//		}
//	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);
			//删除solr中数据
			//itemSearchService.deleteByGoodsIds(Arrays.asList(ids));//后续使用消息队列解耦
			//向消息队列发送删除solr数据的消息
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					// TODO Auto-generated method stub
					return session.createObjectMessage(ids);
				}
			});
			
			
			//消息队列发送删除静态页的消息
			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					// TODO Auto-generated method stub
					return session.createObjectMessage(ids);
				}
			});
			
			
			
			
			
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}
	
	
	
//	@Reference
//	private ItemPageService itemPageService; //通过消息队列解耦
	/**
	 * 批量修改商品状态
	 * @param ids 修改商品id
	 * @param status 修改后状态
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids, String status) {
		try {
			goodsService.updateStatus(ids, status);
			
			if("1".contentEquals(status)) {
				//1.将审核通过的记录添加到solr
				List<TbItem> itemList = goodsService.findItemListByGoodsIdListAndStatus(ids, status);
				//System.out.println("managerweb->goodsControlelr->itemList:" + itemList.size());
				//itemSearchService.importList(itemList);//调用searchService中方法，将审核通过的列表导入solr中//后续使用消息队列解耦
				
				//1.向消息队列发送更新solr的消息
				//1.1将List<TbItem>转换为json字符串
				final String jsonString = JSON.toJSONString(itemList);
				jmsTemplate.send(queueSolrDestination, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						// TODO Auto-generated method stub
						return session.createTextMessage(jsonString);
					}
				});
				
				
				//2.将审核通过的记录生成静态页。，通过消息队列解耦
				for(final Long goodsId : ids) {
					//itemPageService.genItemHtml(goodsId);
					jmsTemplate.send(topicPageDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							// TODO Auto-generated method stub
							return session.createTextMessage(goodsId+"");
						}
					});
				}
			}
			return new Result(true, "修改成功！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "修改失败！");
		}
	}
	
	/**测试生成静态页面 
	 * @param goodsId
	 */
//	@RequestMapping("/genHtml")
//	public void genHtml(Long goodsId) {
//		itemPageService.genItemHtml(goodsId);
//	}
	
}
