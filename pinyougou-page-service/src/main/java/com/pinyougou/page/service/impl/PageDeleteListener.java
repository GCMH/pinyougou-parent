package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import com.pinyougou.page.service.ItemPageService;

@Component
public class PageDeleteListener implements MessageListener {
	
	@Autowired
	private ItemPageService itemPageService;
	
	//接收删除消息，并删除对应静态页
	@Override
	public void onMessage(Message message) {
		System.out.println("接收到删除静态页消息");
		ObjectMessage objectMessage = (ObjectMessage)message;
		try {
			Long[] goodsIds = (Long[]) objectMessage.getObject();
			boolean isDeleteSuccess = itemPageService.deleteItemHtml(goodsIds);
			System.out.println("删除静态页:" + isDeleteSuccess);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
