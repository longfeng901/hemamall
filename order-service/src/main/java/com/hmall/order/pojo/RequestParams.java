package com.hmall.order.pojo;

import lombok.Data;

/**
 * @projectName: Hmall
 * @package: com.hmall.order.pojo
 * @className: RequestParams
 * @author: xuxiang
 * @description: TODO
 * @date: 2023/10/31 19:42
 * @version: 1.0
 */
@Data
public class RequestParams {
    private Integer num;
    private Long itemId;
    private Long addressId;
    private Integer paymentType;
}
