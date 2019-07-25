package com.pinyougou.sellergoods.service;
import java.util.List;
import com.pinyougou.pojo.TbItemCat;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface ItemCatService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbItemCat> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbItemCat itemCat);
	
	
	/**
	 * 修改
	 */
	public void update(TbItemCat itemCat);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbItemCat findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 * @throws Exception 
	 */
	public void delete(Long [] ids) throws Exception;

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbItemCat itemCat, int pageNum,int pageSize);
	
	
	
	/**
	 * 根据商品级别ID查询商品
	 * @param parentId：商品级别id
	 * @return:返回查询到的商品列表
	 */
	public List<TbItemCat> findByPraentId(Long parentId); 
	
}
