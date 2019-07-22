package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 根据查询条件查询es中的商品数据
     * @param searchMap 查询条件
     * @return 返回数据
     */
    Map<String, Object> search(Map<String, Object> searchMap);

    /**保存sku商品数据
     *
     * @param itemList
     */
    void importItemList(List<TbItem> itemList);

    /**
     * 根据商品spu ids数组删除在es中的sku
     * @param goodsIds 商品spu id数组
     *
     */
    void deleteItemByIds(Long[] goodsIds);
}
