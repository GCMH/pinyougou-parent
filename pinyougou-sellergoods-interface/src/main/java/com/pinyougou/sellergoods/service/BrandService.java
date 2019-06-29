package com.pinyougou.sellergoods.service;

import java.util.List;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

/**  
* 创建时间：2019年5月4日 下午10:55:58  
* 项目名称：pinyougou-sellergoods-interface  
* @author hcf  
* @version 1.0   
* @since JDK 1.8.0_201  
* 文件名称：BrandService.java  
* 说       明：  品牌接口
*/
public interface BrandService {
	
	/**查询所有品牌
	 * @return:所有品牌信息列表
	 */
	public List<TbBrand> findAll();
	
	/**品牌分页
	 * @param pageNum:当前所在页
	 * @param pageSize:当前页显示数据数目
	 * @return:页面结果封装类
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	/**添加品牌
	 * @param tbBrand:将tbBrand添加到数据库
	 */
	public void add(TbBrand tbBrand);
	
	/**根据id查询品牌
	 * @param id
	 * @return 品牌信息类
	 */
	public TbBrand findOne(long id);
	
	/**更改品牌信息
	 * @param tbBrand:替换原有数据
	 */
	public void update(TbBrand tbBrand);
	
	/**删除品牌
	 * @param ids:删除品牌id
	 */
	public void delete(long[] ids);
	
	/** 条件查询
	 * @param tbBrand:条件查询封装对象
	 * @param pageNum：当前页码
	 * @param pageSize：当前页显示结果数量
	 * @return
	 */
	public PageResult findPage(TbBrand tbBrand,int pageNum, int pageSize);
}
