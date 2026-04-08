package com.hmall.search.service;

import com.hmall.common.dto.PageDTO;
import com.hmall.search.pojo.ItemDoc;
import com.hmall.search.pojo.RequestParams;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @projectName: Hmall
 * @package: com.hmall.search.service
 * @className: SearchServiceImpl
 * @author: xuxiang
 * @description: TODO
 * @date: 2023/10/24 20:53
 * @version: 1.0
 */

public interface ISearchServiceI {
    /**
     * 自动补全
     *
     * @param prefix
     * @return {@link List}<{@link String}>
     */
    List<String> getSuggestion(String prefix);

    /**
     * 聚合
     *
     * @param params
     * @return {@link Map}<{@link String}, {@link List}<{@link String}>>
     */
    Map<String, List<String>> getFilters(RequestParams params);

    PageDTO<ItemDoc> search(RequestParams params);

    void saveItemById(Long id);

    void deleteItemById(Long id);
}
