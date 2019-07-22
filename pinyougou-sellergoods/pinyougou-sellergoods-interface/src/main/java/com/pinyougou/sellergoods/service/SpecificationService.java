package com.pinyougou.sellergoods.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbSpecification;

import com.pinyougou.service.BaseService;
import com.pinyougou.vo.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService extends BaseService<TbSpecification> {
    /**
     * 根据条件搜索
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param specification 搜索条件
     * @return 分页信息
     */
    PageInfo<TbSpecification> search(Integer pageNum, Integer pageSize, TbSpecification specification);

    /**
     * 新增规格及选项
     * @param specification  规格及选项
     */
    void addSpecification(Specification specification);

    Specification findSpecification(Long id);

    void updateSpecification(Specification specification);

    void deleteSpecificationByIds(Long[] ids);

    List<Map<String, Object>> selectOptionList();
}
