package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemCatExample;
import com.pinyougou.pojo.TbItemCatExample.Criteria;
import com.pinyougou.sellergoods.service.ItemCatService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

		@Autowired
		private TbItemCatMapper itemCatMapper;
		
		@Autowired
		private RedisTemplate redisTemplate;
		
		/**
		 * 查询全部
		 */
		@Override
		public List<TbItemCat> findAll() {
			return itemCatMapper.selectByExample(null);
		}
	
		/**
		 * 按分页查询
		 */
		@Override
		public PageResult findPage(int pageNum, int pageSize) {
			PageHelper.startPage(pageNum, pageSize);		
			Page<TbItemCat> page=   (Page<TbItemCat>) itemCatMapper.selectByExample(null);
			return new PageResult(page.getTotal(), page.getResult());
		}
	
		/**
		 * 增加
		 */
		@Override
		public void add(TbItemCat itemCat) {
			itemCatMapper.insert(itemCat);		
		}
	
		
		/**
		 * 修改
		 */
		@Override
		public void update(TbItemCat itemCat){
			itemCatMapper.updateByPrimaryKey(itemCat);
		}	
		
		/**
		 * 根据ID获取实体
		 * @param id
		 * @return
		 */
		@Override
		public TbItemCat findOne(Long id){
			return itemCatMapper.selectByPrimaryKey(id);
		}

		/**
		 * 批量删除
		 * @throws Exception 
		 */
		@Override
		public void delete(Long[] ids) throws Exception {
			boolean isDelete = true;//表示是否可以删除
			//要么全部删除，要么全部不删除
			for(Long id:ids){
				if(itemCatMapper.countGoods(id) > 0) {
					isDelete = false;//只要有子级商品，直接返回
					break;
				}
			}
			if(isDelete) {
				for(Long id:ids){
					itemCatMapper.deleteByPrimaryKey(id);
				}
			}else {
				throw new Exception("存在子级商品，无法删除");
			}
		}
	
	
		@Override
		public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
			PageHelper.startPage(pageNum, pageSize);
			
			TbItemCatExample example=new TbItemCatExample();
			Criteria criteria = example.createCriteria();
			
			if(itemCat!=null){			
							if(itemCat.getName()!=null && itemCat.getName().length()>0){
					criteria.andNameLike("%"+itemCat.getName()+"%");
				}
		
			}
			
			Page<TbItemCat> page= (Page<TbItemCat>)itemCatMapper.selectByExample(example);		
			return new PageResult(page.getTotal(), page.getResult());
		}
		
		
		
		@Override
		public List<TbItemCat> findByPraentId(Long parentId) {
			// TODO Auto-generated method stub
			TbItemCatExample example = new TbItemCatExample();
			Criteria criteria = example.createCriteria();
			criteria.andParentIdEqualTo(parentId);
			
			//将模板ID放入缓存
			//key为商品分类名称，value为该商品分类对应模板ID
			List<TbItemCat> itemCatList = findAll();
			for(TbItemCat itemCat : itemCatList) {
				redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
			}
			//System.out.println("缓存分类列表");
			return itemCatMapper.selectByExample(example);
		}
	
}
