package com.hmall.order.web;

import com.hmall.order.pojo.Order;
import com.hmall.order.pojo.RequestParams;
import com.hmall.order.service.IOrderService;
import com.hmall.order.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @GetMapping("{id}")
    public Order queryOrderById(@PathVariable("id") Long orderId) {
        Long userId = UserHolder.getUser();
        System.out.println(userId);
        return orderService.getById(orderId);
    }

    /**
     * 创建订单
     *
     * @param requestParams
     * @return {@link Long}
     */
    @PostMapping
    public Long creatOrder(@RequestBody RequestParams requestParams) {
        return orderService.createOrder(requestParams);
    }
}
