package com.hmall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.order.mapper.OrderLogisticsMapper;
import com.hmall.order.mapper.OrderMapper;
import com.hmall.order.pojo.Order;
import com.hmall.order.pojo.OrderLogistics;
import com.hmall.order.service.IOrderLogisticService;
import com.hmall.order.service.IOrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderLogisticService extends ServiceImpl<OrderLogisticsMapper, OrderLogistics> implements IOrderLogisticService {
}
