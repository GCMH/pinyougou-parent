package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import com.pinyougou.sellergoods.service.TypeTemplateService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);		
		
		saveToRedis();
		return new PageResult(page.getTotal(), page.getResult());
	}
	
	@Autowired	
	private RedisTemplate redisTemplate;
	
	/**
	 * 将模板表中每一个模板对应的品牌列表，规格选项列表缓存。
	 */
	private void saveToRedis() {
		List<TbTypeTemplate> typeTemplateList = findAll();
		for(TbTypeTemplate typeTemplate : typeTemplateList) {
			//缓存品牌列表
			List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
			//key为模板ID，value为对应模板ID的品牌列表
			redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandList);
			
			//缓存规格列表
			List<Map> specList = findSpecList(typeTemplate.getId());
			redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specList);
		}
		//System.out.println("缓存模板对应的品牌列表，规格选项列表");
	}	
		
	//返回{id:1,text:xxx}格式用于select2查询显示
	@Override
	public List<Map> selectOptionList() { 
		// TODO Auto-generated method stub
		return typeTemplateMapper.selectOptionList();
	}

	@Override
	public List<Map> findSpecList(Long id) {
		//String
		TbTypeTemplate tbTypeTemplate =typeTemplateMapper.selectByPrimaryKey(id);
		//specIds=[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
		//一个map [{id:27,text:网络}]
		List<Map> specList = JSON.parseArray(tbTypeTemplate.getSpecIds(),Map.class); 
		//System.out.println("TypeTemplateServiceImpl->findSpecList->toJsonString:" + JSON.toJSON(specList));
		//查询每一个规格的规格选项
		for(Map map:specList) {
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			com.pinyougou.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(new Long((Integer)map.get("id")));
			//[{id:99,optionName:4G,specId:27},{id:100,optionName:3G,specId:27}...]
			List<TbSpecificationOption> specOptionList =  specificationOptionMapper.selectByExample(example );
			// [{id:27,text:网络,options:[{id:99,optionName:4G,specId:27},{id:100,optionName:3G,specId:27}...]}]
			map.put("options", specOptionList);
			//System.out.println("for-TypeTemplateServiceImpl->findSpecList->toJsonString:" + JSON.toJSON(map));
			//System.out.println("TypeTemplateServiceImpl->findSpecList->toJsonString:" + JSON.toJSON(specList));
		}
		
		// [{id:27,text:网络,options[{id:99,optionName:4G,specId:27},{id:100,optionName:3G,specId:27}...]}]
		return specList;
	}
	
}
