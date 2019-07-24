package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private SellerMapper sellerMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private BrandMapper brandMapper;


    @Override
    public PageInfo<TbGoods> search(Integer pageNum, Integer pageSize, TbGoods goods) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);
        //创建查询对象
        Example example = new Example(TbGoods.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();
        //查询非删除状态的数据
        criteria.andNotEqualTo("isDelete",1);

        //查询商家
        if (StringUtils.isNotBlank(goods.getSellerId())) {
            criteria.andEqualTo("sellerId", goods.getSellerId());
        }
        //查询商品状态
        if (StringUtils.isNotBlank(goods.getAuditStatus())) {
            criteria.andEqualTo("auditStatus", goods.getAuditStatus());
        }
        //模糊查询商品名称
        if (StringUtils.isNotBlank(goods.getGoodsName())) {
            criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
        }

        List<TbGoods> list = goodsMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    @Override
    public void addGoods(Goods goods) {
        //保存商品基本信息
        add(goods.getGoods());
        //保存商品描述信息
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());

        //保存商品sku列表信息
        saveItemList(goods);

    }

    /**
     * 保存商品sku列表信息
     *
     * @param goods
     */
    private void saveItemList(Goods goods) {
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            //启用规格
            if (goods.getItemList() != null && goods.getItemList().size() > 0) {
                for (TbItem tbItem : goods.getItemList()) {
                    //标题
                    String title = goods.getGoods().getGoodsName();
                    //获取规格选项
                    Map<String, String> specMap = JSON.parseObject(tbItem.getSpec(), Map.class);
                    for (Map.Entry<String, String> entry : specMap.entrySet()) {
                        title += " " + entry.getValue();
                    }
                    tbItem.setTitle(title);

                    setItemValue(goods, tbItem);

                    //保存到数据库
                    itemMapper.insertSelective(tbItem);
                }
            }
        } else {
            //不启用规格
            TbItem tbItem = new TbItem();
            //价格
            tbItem.setPrice(goods.getGoods().getPrice());
            //库存
            tbItem.setNum(9999);
            //是否启用
            tbItem.setStatus("0");
            //是否默认
            tbItem.setIsDefault("1");
            //规格
            tbItem.setSpec("{}");
            //标题
            tbItem.setTitle(goods.getGoods().getGoodsName());
            //设置商品sku的数据
            setItemValue(goods, tbItem);

            itemMapper.insertSelective(tbItem);

        }
    }


    private void setItemValue(Goods goods, TbItem tbItem) {
        //图片，获取spu描述中第一张图片
        if (StringUtils.isNotBlank(goods.getGoodsDesc().getItemImages())) {
            List<Map> images = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
            if (images != null && images.size() > 0) {
                tbItem.setImage(images.get(0).get("url") + "");
            }
        }
        //卖家
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        tbItem.setSellerId(seller.getSellerId());
        tbItem.setSeller(seller.getName());

        //商品分类 sup第三级别分类id
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        tbItem.setCategoryid(itemCat.getId());
        tbItem.setCategory(itemCat.getName());

        //品牌
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        tbItem.setBrand(brand.getName());

        tbItem.setGoodsId(goods.getGoods().getId());

        tbItem.setCreateTime(new Date());
        tbItem.setUpdateTime(tbItem.getCreateTime());

    }


    @Override
    public Goods findGoodsById(Long id) {
        return findGoodsByIdAndStatus(id,null);
    }

    /**
     * 修改
     *
     * @param goods
     */
    @Override
    public void updateGoods(Goods goods) {

        //更新描述信息
        goods.getGoods().setAuditStatus("0");
        //更新基本信息
        update(goods.getGoods());
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());

        //更新sku列表(先删除，再保存)
        TbItem item = new TbItem();
        item.setGoodsId(goods.getGoods().getId());
        itemMapper.delete(item);

        saveItemList(goods);
    }

    @Override
    public void updateStatus(String status, Long[] ids) {
        //要跟新的数据
        TbGoods tbGoods = new TbGoods();
        tbGoods.setAuditStatus(status);

        //查询条件
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(tbGoods, example);

        TbItem item = new TbItem();
        if ("2".equals(status)) {
            //审核通过
            item.setStatus("1");
        } else {
            //审核不通过
            item.setStatus("0");
        }
        Example itemExample = new Example(TbItem.class);
        itemExample.createCriteria().andIn("goodsId", Arrays.asList(ids));
        itemMapper.updateByExampleSelective(item, itemExample);
    }

    @Override
    public void deleteGoods(Long[] ids) {
        //格局商品spu id数组更新商品spu的isDelete属性值
        TbGoods tbGoods = new TbGoods();
        tbGoods.setIsDelete("1");
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(tbGoods,example);
    }

    /**根据商品spu id数组获取对应已启动的sku商品列表
     *
     * @param ids spu id 数组
     * @return
     */
    @Override
    public List<TbItem> findItemListByGoodsIds(Long[] ids) {
        //查询条件
        Example example = new Example(TbItem.class);
        example.createCriteria().andEqualTo("status","1").andIn("goodsId",Arrays.asList(ids));

       return itemMapper.selectByExample(example);

    }

    @Override
    public Goods findGoodsByIdAndStatus(Long goodsId, String itemStatus) {

        Goods goods = new Goods();
        //根据spu id查询剧本信息
        goods.setGoods(findOne(goodsId));
        //根据spu id查询描述信息
        goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(goodsId));
        //根据spu id查询sku列表
        Example example = new Example(TbItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("goodsId",goodsId);
        if (itemStatus!=null){
        criteria.andEqualTo("status",itemStatus);
        }
        example.orderBy("isDefault").desc();
        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);

        return goods;


    }
}
