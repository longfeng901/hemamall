package com.hmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.order.pojo.Order;
import com.hmall.order.pojo.RequestParams;

public interface IOrderService extends IService<Order> {
    /**创建订单
     * @param requestParams
     * @return {@link Long}
     */
    Long createOrder(RequestParams requestParams);

}
