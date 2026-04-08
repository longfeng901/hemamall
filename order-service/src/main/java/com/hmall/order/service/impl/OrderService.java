package com.hmall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.common.client.ItemClient;
import com.hmall.common.client.UserClient;
import com.hmall.common.dto.Address;
import com.hmall.common.dto.Item;
import com.hmall.order.mapper.OrderMapper;
import com.hmall.order.pojo.Order;
import com.hmall.order.pojo.OrderDetail;
import com.hmall.order.pojo.OrderLogistics;
import com.hmall.order.pojo.RequestParams;
import com.hmall.order.service.IOrderDetailService;
import com.hmall.order.service.IOrderService;
import com.hmall.order.utils.UserHolder;

import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private IOrderDetailService detailService;
    @Autowired
    private UserClient userClient;
    @Autowired
    private OrderLogisticService logisticService;

    /**
     * 创建订单
     *
     * @param requestParams
     * @return {@link Long}
     */
    @Transactional
    @Override
    public Long createOrder(RequestParams requestParams) {
        Order order = new Order();
        //   - 1）查询商品
        Item item = itemClient.queryItemById(requestParams.getItemId());
        //   - 2）基于商品价格、购买数量计算商品总价：totalFee
        long totalFee = item.getPrice() * requestParams.getNum();
        order.setTotalFee(totalFee);
        order.setPaymentType(requestParams.getPaymentType());
        order.setUserId(UserHolder.getUser());
        order.setStatus(1);
        //    - 3）将Order写入数据库tb_order表中
        this.save(order);
        //    - 4）将商品信息、orderId信息封装为OrderDetail对象，写入tb_order_detail表
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setName(item.getName());
        orderDetail.setOrderId(item.getId());
        orderDetail.setItemId(item.getId());
        orderDetail.setNum(requestParams.getNum());
        orderDetail.setPrice(item.getPrice());
        orderDetail.setSpec(item.getSpec());
        orderDetail.setImage(item.getImage());
        detailService.save(orderDetail);

        Long addressId = requestParams.getAddressId();
        //  - 5）根据addressId查询user-service服务，获取地址信息
        Address address = userClient.findAddressById(addressId);
        //   - 6）将地址封装为OrderLogistics对象，写入tb_order_logistics表
        OrderLogistics orderLogistics = new OrderLogistics();
        BeanUtils.copyProperties(address, orderLogistics);
        orderLogistics.setOrderId(order.getId());
        logisticService.save(orderLogistics);
        // -7) 扣减库存
        try {
            itemClient.updateStock(requestParams.getItemId(), requestParams.getNum());
        } catch (Exception e) {
            throw new RuntimeException("库存不足!");
        }
        return order.getId();
    }
}
