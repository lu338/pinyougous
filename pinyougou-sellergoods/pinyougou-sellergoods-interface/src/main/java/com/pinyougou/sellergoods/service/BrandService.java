package com.pinyougou.sellergoods.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BaseService;


import java.util.List;
import java.util.Map;

public interface BrandService extends BaseService<TbBrand> {
    List<TbBrand> queryAll();

    /**
     * 分页查询品牌列表
     * @param pageNum 页号
     * @param pageSize 页大小
     * @return 品牌列表
     */
    List<TbBrand> testPage(Integer pageNum, Integer pageSize);

    PageInfo<TbBrand> search(Integer pageNum, Integer pageSize, TbBrand brand);

    List<Map<String, Object>> selectOptionList();
}
