package com.pinyougou.search.activemq.listener;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.Serializable;

public class ItemDeleteMessageListener extends AbstractAdaptableMessageListener {
    @Autowired
    private ItemSearchService itemSearchService;


    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        if (message instanceof ObjectMessage){
            //1.接收消息
            ObjectMessage objectMessage = (ObjectMessage) message;
            Long[] goodsIds = (Long[]) objectMessage.getObject();
            //2.删除es中的商品数据
            itemSearchService.deleteItemByIds(goodsIds);
        }
    }
}
