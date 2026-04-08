package com.hmall.search.pojo;

import lombok.Data;

/**
 * @projectName: Hmall
 * @package: com.hmall.search.pojo
 * @className: RequestParams
 * @author: xuxiang
 * @description: TODO
 * @date: 2023/10/29 15:19
 * @version: 1.0
 */
@Data
public class RequestParams {
    private String key;
    private Integer page = 1;
    private Integer size = 5;
    private String sortBy;
    private String brand;
    private String category;
    private Long minPrice;
    private Long maxPrice;
}
