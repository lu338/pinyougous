package com.pinyougou.es;

import com.alibaba.fastjson.JSONObject;
import com.pinyougou.es.dao.ItemElasticSearchDao;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import
        org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import
        org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
// 要扫描到 pinyougou-dao 工程的配置文件所以 classpath 之后加 *
@ContextConfiguration(locations = "classpath*:spring/*.xml")
   public class ItemImport2ElasticSearch {
    @Autowired
    private ItemElasticSearchDao itemElasticSearchDao;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ItemMapper itemMapper;

    /**
     * 创建索引和映射
     */
    @Test
    public void createIndexAndMapping() {
// 创建索引
        elasticsearchTemplate.createIndex(TbItem.class);
// 创建映射
        elasticsearchTemplate.putMapping(TbItem.class);
    }

    @Test
    public void test() {
//1 、查询所有启用的 sku 商品数据
        TbItem param = new TbItem();
        param.setStatus("1");
        List<TbItem> itemList = itemMapper.select(param);
//2 、转换商品的规格 json 数据到 map
        for (TbItem tbItem : itemList) {
            if (StringUtils.isNotBlank(tbItem.getSpec())) {
                Map specMap =
                        JSONObject.parseObject(tbItem.getSpec(), Map.class);
                tbItem.setSpecMap(specMap);
            }
        }
//3 、导入 es
        itemElasticSearchDao.saveAll(itemList);

    }
}