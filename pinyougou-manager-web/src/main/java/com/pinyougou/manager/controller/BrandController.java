package com.pinyougou.manager.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;
/**  
* 创建时间：2019年5月16日 下午7:30:14  
* 项目名称：pinyougou-manager-web  
* @author hcf  
* @version 1.0   
* @since JDK 1.8.0_201  
* 文件名称：BrandController.java  
* 说       明： 品牌控制类
*/

@RestController
@RequestMapping("/brand")
public class BrandController {
	@Reference
	private BrandService brandService;
	
	/**查询所有品牌信息
	 * @return:所有品牌信息列表
	 */
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
	//	System.out.println("------------into managerweb----------------" + new Date(System.currentTimeMillis()));
		List<TbBrand> tbBarndList = brandService.findAll();
//		if(tbBarndList == null || tbBarndList.size() == 0) {
//			System.out.println("manager result is null！");
//		}
//		for(TbBrand tbBrand : tbBarndList) {
//			System.out.println("manager:" + tbBrand.getFirstChar() + tbBrand.getName());
//		}
//		System.out.println("------------managerweb--end----------------" + new Date(System.currentTimeMillis()));
		return tbBarndList;
	}
	
	
	@RequestMapping("/findPage")
	public PageResult findPage(int pageNum, int pageSize) {
		return brandService.findPage(pageNum, pageSize);
	}
	
	
	/** 添加品牌信息
	 * @param tbBrand：添加数据
	 * @return:成功或失败的结果Result
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbBrand tbBrand) {
		System.out.println("----------" + tbBrand.getFirstChar() + ":" + tbBrand.getName());
		try {
			brandService.add(tbBrand);
			return new Result(true,"添加成功");
		}catch(Exception e) {
			return new Result(false,"添加失败");
		}
	}
	
	/**
	 * @param id:查询参数
	 * @return:查询参数对应的单条品牌信息
	 */
	@RequestMapping("/findOne")
	public TbBrand findOne(long id) {
		return brandService.findOne(id);
	}
	
	/**
	 * @param tbBrand:更新数据
	 * @return:操作结果类
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbBrand tbBrand) {
		try {
			brandService.update(tbBrand);
			//System.out.println("---- into update----");//测试用
			return new Result(true,"修改成功！");
		}catch(Exception e) {
			e.printStackTrace();
			return new Result(false,"修改失败！");
		}
	}
	
	/**
	 * @param ids:删除品牌id
	 * @return:操作结果类
	 */
	@RequestMapping("/delete")
	public Result delete(long ids[]) {
		System.out.println("BrandController:into delete");
		try {
			brandService.delete(ids);
			//System.out.println("---- into update----");//测试用
			return new Result(true,"删除成功！");
		}catch(Exception e) {
			e.printStackTrace();
			return new Result(false,"删除失败！");
		}
	}
	
	//
	@RequestMapping("/query")
	public PageResult query(@RequestBody TbBrand tbBrand,int pageNum, int pageSize) {
//		System.out.println("BrandController:into select");
//		System.out.println("BrandController:\nname:" + tbBrand.getName() + " firstCahr:" + tbBrand.getFirstChar());
		PageResult result = brandService.findPage(tbBrand, pageNum, pageSize);
//		for(int i = 0; i < result.getRows().size(); i++) {
//			System.out.println(((TbBrand)(result.getRows().get(i))).getName());
//		}
		return result;
	}
	
	@RequestMapping("/selectOptionList")
	public List<Map> selectOptionList(){
		return brandService.selectOptionList();
	}
}
