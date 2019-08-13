package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;

@Component
public class PageListener implements MessageListener{
	
	@Autowired
	private ItemPageService itemPageService;
	
	@Override
	public void onMessage(Message message) {
		System.out.println("接收到生成静态页消息");
		TextMessage textMessage = (TextMessage)message;
		try {
			String id = textMessage.getText();
			itemPageService.genItemHtml(Long.parseLong(id));
			System.out.println("生成静态页");
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
