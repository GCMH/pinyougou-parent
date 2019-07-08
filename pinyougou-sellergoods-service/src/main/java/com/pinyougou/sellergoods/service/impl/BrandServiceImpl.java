package com.pinyougou.sellergoods.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;

/**  
* 创建时间：2019年5月8日 下午9:32:51  
* 项目名称：pinyougou-sellergoods-service  
* @author hcf  
* @version 1.0   
* @since JDK 1.8.0_201  
* 文件名称：BrandServiceImpl.java  
* 说       明：  
*/
@Service
public class BrandServiceImpl implements BrandService{
	
	@Autowired
	private TbBrandMapper brandMapper;
	
	@Override
	public List<TbBrand> findAll() {
		// TODO Auto-generated method stub
		System.out.println("------------into service----------------" + new Date(System.currentTimeMillis()));
		
		return brandMapper.selectByExample(null);
	}
	
	
	/*
	 * 
	 * @param pageNum 当前页页码
	 * @param pageSize 当前页数据量
	 * 
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);//设置PageHelper分类工具类
		Page<TbBrand> page = (Page<TbBrand>)brandMapper.selectByExample(null);//将查询结果集合强转为分页类型
		return new PageResult(page.getTotal(), page.getResult());
	}
	
	//添加品牌
	@Override
	public void add(TbBrand tbBrand) {
		// TODO Auto-generated method stub
		brandMapper.insert(tbBrand);
	}
	
	//修改数据，查询数据回显内容
	@Override
	public TbBrand findOne(long id) {
//		System.out.println(brandMapper.selectByPrimaryKey(id).getName());//测试用
		return brandMapper.selectByPrimaryKey(id);
	}
	//修改数据
	@Override
	public void update(TbBrand brand) {
		brandMapper.updateByPrimaryKey(brand);
	}

	//删除品牌
	@Override
	public void delete(long[] ids) {
		// TODO Auto-generated method stub
		for(int i = 0; i < ids.length; i++) {
			brandMapper.deleteByPrimaryKey(new Long(ids[i]));
		}
	}

	//条件查询，返回结果对象
	@Override
	public PageResult findPage(TbBrand tbBrand, int pageNum, int pageSize) {
		TbBrandExample example = new TbBrandExample();
		Criteria criteria = example.createCriteria();
		if(tbBrand != null) {
			if(tbBrand.getFirstChar() != null && !tbBrand.getFirstChar().equals("")) {
				criteria.andFirstCharLike("%" + tbBrand.getFirstChar() + "%");
			}
			if(tbBrand.getName() != null && !tbBrand.getName().equals("")) {
				criteria.andNameLike("%" + tbBrand.getName() + "%");
			}
		}
		PageHelper.startPage(pageNum, pageSize);//设置PageHelper分类工具类
		Page<TbBrand> page = (Page<TbBrand>)brandMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}


	@Override
	public List<Map> selectOptionList() {
		// TODO Auto-generated method stub
		return brandMapper.selectOptionList();
	}
}
