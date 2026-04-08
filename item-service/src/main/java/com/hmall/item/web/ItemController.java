package com.hmall.item.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.common.dto.PageDTO;
import com.hmall.item.pojo.Item;
import com.hmall.item.service.IItemService;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("item")
public class ItemController {

    @Autowired
    private IItemService itemService;

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return {@link PageDTO}<{@link Item}>
     */
    @GetMapping("/list")
    public PageDTO<Item> queryItemByPage(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        //分页查询
        Page<Item> itemPage = itemService.page(new Page<>(page, size));

        //封装并返回
        return new PageDTO<>(itemPage.getTotal(), itemPage.getRecords());
    }


    /**
     * 根据id查询商品详情
     *
     * @return {@link Item}
     */
    @GetMapping("/{id}")
    public Item queryItemById(@PathVariable("id") Long id) {
        return itemService.getById(id);
    }

    /**
     * 增加商品
     */
    @PostMapping
    public void saveItem(@RequestBody Item item) {
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        item.setStatus(2);
        itemService.save(item);
    }

    /**
     * 商品上下架
     *
     * @param id
     * @param status
     */
    @PutMapping("/status/{id}/{status}")
    public void updateStatusById(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        itemService.updateStatus(id, status);
    }

    /**
     * 更新商品信息
     *
     * @param item
     */
    @PutMapping
    public void update(@RequestBody Item item) {
        item.setUpdateTime(new Date());
        //将status设置为null,更新时就会忽略该字段
        item.setStatus(null);
        itemService.updateById(item);
    }

    /**
     * 根据id删除商品
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        itemService.removeById(id);
    }

    /**扣减库存
     * @param itemId
     * @param num
     */
    @PutMapping("/stock/{itemId}/{num}")
    public void updateStock(@PathVariable("itemId") Long itemId, @PathVariable("num") Integer num) {
        itemService.updateStock(itemId, num);
    }
}
