package com.hmall.search.mq;

import com.hmall.search.service.ISearchServiceI;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @projectName: Hmall
 * @package: com.hmall.search.mq
 * @className: ItemListener
 * @author: xuxiang
 * @description: TODO
 * @date: 2023/10/30 19:29
 * @version: 1.0
 */
@Component
public class ItemListener {
    @Autowired
    private ISearchServiceI searchServiceI;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "up.queue"),
            exchange = @Exchange(name = "item.topic", type = ExchangeTypes.TOPIC),
            key = "item.up"
    ))
    public void listenerItemUp(Long id) {
        searchServiceI.saveItemById(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "down.queue"),
            exchange = @Exchange(name = "item.topic", type = ExchangeTypes.TOPIC),
            key = "item.down"
    ))
    public void listenerItemDoen(Long id) {
        searchServiceI.deleteItemById(id);
    }
}
