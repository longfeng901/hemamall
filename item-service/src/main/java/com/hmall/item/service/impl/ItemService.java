package com.hmall.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.item.mapper.ItemMapper;
import com.hmall.item.pojo.Item;
import com.hmall.item.service.IItemService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService extends ServiceImpl<ItemMapper, Item> implements IItemService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    @Override
    public void updateStatus(Long id, Integer status) {
        this.update().set("status", status).eq("id", id).update();

        //根据上下架判断routingKey
        String routingKey = status == 1 ? "item.up" : "item.down";
        //发送消息
        rabbitTemplate.convertAndSend("item.topic", routingKey, id);


    }

    /**
     * 扣减库存
     *
     * @param itemId
     * @param num
     */
    @Transactional
    @Override
    public void updateStock(Long itemId, Integer num) {
        try {
            update().setSql("stock = stock - " + num).eq("id", itemId).update();
        } catch (Exception e) {
            throw new RuntimeException("库存不足!");
        }
    }


}
