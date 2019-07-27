package com.pinyougou.search.activemq.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

public class ItemImportMessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    private ItemSearchService itemSearchService;


    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        if (message instanceof TextMessage){
            //1.接收消息并转换列表
            TextMessage textMessage= (TextMessage) message;
            List<TbItem> itemList = JSON.parseArray(textMessage.getText(), TbItem.class);

            //2.将列表中的每个sku的spec转换为map并设置到specMap
            for (TbItem tbItem : itemList) {
                Map specMap = JSON.parseObject(tbItem.getSpec(), Map.class);
                tbItem.setSpecMap(specMap);
            }
            //保存到es
            itemSearchService.importItemList(itemList);
        }
    }
}
