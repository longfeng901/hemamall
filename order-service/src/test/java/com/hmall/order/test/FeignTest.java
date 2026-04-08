package com.hmall.order.test;

import com.hmall.common.client.UserClient;
import com.hmall.common.dto.Address;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @projectName: Hmall
 * @package: com.hmall.order.test
 * @className: FeignTest
 * @author: xuxiang
 * @description: TODO
 * @date: 2023/10/31 15:10
 * @version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FeignTest {
    @Autowired
    private UserClient userClient;

    @Test
    public void testFindAddress() {
        Address address = userClient.findAddressById(59L);
        System.out.println("address =" + address);
    }
}
