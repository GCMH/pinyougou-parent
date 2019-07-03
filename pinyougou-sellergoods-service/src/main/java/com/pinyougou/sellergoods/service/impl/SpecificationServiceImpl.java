package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojogroup.Specification;
import com.pinyougou.sellergoods.service.SpecificationService;
import com.sun.tools.javac.resources.compiler;

import entity.PageResult;

/**
 * 服务实现层
 * @author hcf
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 说明：增加规格名称和规格选项
	 * 
	 */
	@Override
	public void add(Specification specification) {
		//获取规格实体：规格名+具体规格列表
		TbSpecification tbSpecification = specification.getSpecification();
		//插入规格名
		specificationMapper.insert(tbSpecification);
		//获取具体规格列表并插入
		List<TbSpecificationOption> specificationList = specification.getSpecificationOptionList();
		for(TbSpecificationOption option : specificationList) {
			//System.out.println(tbSpecification.getId());
			option.setSpecId(tbSpecification.getId());
			specificationOptionMapper.insert(option);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		//获取规格实体：规格名+具体规格列表
		TbSpecification tbSpecification = specification.getSpecification();
		
		//更新规格
		specificationMapper.updateByPrimaryKey(tbSpecification);
		
		//删除原有规格列表
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		com.pinyougou.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(tbSpecification.getId());
		specificationOptionMapper.deleteByExample(example);
		
		
		//获取更新后具体规格列表并插入
		List<TbSpecificationOption> specificationList = specification.getSpecificationOptionList();
		for(TbSpecificationOption option : specificationList) {
			//System.out.println(tbSpecification.getId());
			option.setSpecId(tbSpecification.getId());
			specificationOptionMapper.insert(option);
		}
		
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		//返回的规格实体
		Specification specification = new Specification();
		//查询并设置规格名
		specification.setSpecification(specificationMapper.selectByPrimaryKey(id));
		
		//设置example
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		com.pinyougou.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(id);
		
		//查询并设置规格列表
		List<TbSpecificationOption> specificationsList = specificationOptionMapper.selectByExample(example);
		specification.setSpecificationOptionList(specificationsList);
		
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			com.pinyougou.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(example);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
