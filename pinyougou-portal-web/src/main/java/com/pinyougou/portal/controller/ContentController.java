package com.pinyougou.portal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;

@RequestMapping("/content")
@RestController
public class ContentController {
	@Reference
	ContentService contentService;
	
	@RequestMapping("/findByCategroyId")
	public List<TbContent> findByCategroyId(Long categroyId){
		return contentService.findByCategroyId(categroyId);
	}
	
}
