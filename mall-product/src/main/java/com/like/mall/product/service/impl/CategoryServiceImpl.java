package com.like.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.product.dao.CategoryDao;
import com.like.mall.product.entity.CategoryEntity;
import com.like.mall.product.service.CategoryBrandRelationService;
import com.like.mall.product.service.CategoryService;
import com.like.mall.product.vo.Catelog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>());

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1.查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        // 2.组装成父子结构
        return entities
                .stream()
                .filter(c -> c.getParentCid() == 0)   // 查询到所有的一级分类，parent id = 0
                .peek(c -> {
                    c.setChildren(getChildren(c, entities)); // 查找当前标签的子标签
                })
                .sorted(Comparator.comparingInt(c -> (c.getSort() == null ? 0 : c.getSort()))) // 排序
                .collect(Collectors.toList()); // 收集

    }

    @Override
    public void removeMenu(List<Long> asList) {
        // TODO: 2020/10/26 1.删除标签：检查是否被引用
        // 现在使用逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = findParentPaths(catelogId, new ArrayList<>());
        // 孙/子/父 -> 反转
        Collections.reverse(paths);
        return paths.toArray(new Long[paths.size()]);
    }


    /**
     * 级联更新所有关联的数据
     *
     * @param category
     */
    @Override
    @Transactional
    public void updateDetail(CategoryEntity category) {
        // 1.先更新自己
        updateById(category);
        // 2.更新和品牌相关的内容
        if (StringUtils.isNotBlank(category.getName())) {
            categoryBrandRelationService.updateCategory(category);
        }
    }

    @Override
    public List<CategoryEntity> getLevelFirstCategory() {
        return baseMapper.getLevelFirstCategory();
    }

    /**
     * 使用缓存
     *
     * @return {@link Map<String, List<Catelog2Vo>>}
     */
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        /**
         * 解决高并发的缓存失效
         * 1.空结果缓存，缓存穿透
         * 2.设置随机过期时间,缓存雪崩
         * 3.加锁，缓存击穿
         */
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        // 缓存中有数据
        if (StringUtils.isNotBlank(catalogJson)) {
            log.info("使用缓存");
            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
        }
        // 1. 缓存中没有数据：从数据库中查
        return getCatalogJsonFromDbWithRedisLock();
    }

    /**
     * 使用分布式锁-redis的setnx
     * 从数据库中查询商品分类的信息
     *
     * @return {@link Map<String, List<Catelog2Vo>>}
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        String value = UUID.randomUUID().toString();
        // 1.分布式锁，去redis中占坑并设置过期时间 setnxex
        Boolean hasLock = redisTemplate.opsForValue().setIfAbsent("catalogJsonLock", value, 300, TimeUnit.SECONDS);
        // 2.判断是否加锁成功
        if (hasLock) {
            // 2.1.1 成功 抢到锁,读取数据库，写入redis
            try {
                return getDataFromDb();
            } finally {
                // 2.1.2 解锁 如果是当前线程放入的就删除 获取值对比+对比成功删除 = 原子操作  使用lua脚本解锁
                String script =
                        "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" + "return redis.call(\"del\",KEYS[1])\n" + "else\n" + "return 0\n" + "end";
                redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList("catalogJsonLock", value));
            }
        } else {
            // 2.2.1失败 自旋 在次从redis中读取catalogJson
            try {
                TimeUnit.SECONDS.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDbWithRedisLock();
        }
    }

    /**
     * 使用本地锁
     * 从数据库中查询商品分类的信息
     *
     * @return {@link Map<String, List<Catelog2Vo>>}
     */
    public synchronized Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithLocalLock() {
        // 再次判断缓存中是否有该json
        return getDataFromDb();
    }

    public Map<String, List<Catelog2Vo>> getDataFromDb() {
        // 判断缓存中是否有该json
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isNotBlank(catalogJson)) {
            log.info("使用缓存");
            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
        }

        // 查询数据库
        log.error("缓存读取数据库");
        List<CategoryEntity> allCategory = baseMapper.selectList(null);
        // 1.找出所有的一级分类
        List<CategoryEntity> level1 = getParent_cid(allCategory, 0L);

        // 2.封装数据
        Map<String, List<Catelog2Vo>> data = level1.stream()
                .collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                    // 3.查找当前一级分类下的二级分类
                    List<CategoryEntity> level2 = getParent_cid(allCategory, v.getCatId());

                    // 4.封装二级分类vo
                    List<Catelog2Vo> vo2s = new ArrayList<>();
                    if (level2 != null) {
                        vo2s = level2.stream().map(i2 -> {
                            Catelog2Vo Vo2 = new Catelog2Vo();
                            Vo2.setCatalogId(String.valueOf(v.getCatId()));
                            Vo2.setId(i2.getCatId().toString());
                            Vo2.setName(i2.getName());

                            // 5.寻找三级分类
                            List<CategoryEntity> level3 = getParent_cid(allCategory, i2.getCatId());

                            // 6.封装三级级分类vo
                            List<Catelog2Vo.Catelog3Vo> vo3s = null;
                            if (level3 != null) {
                                vo3s = level3.stream().map(i3 -> {
                                    Catelog2Vo.Catelog3Vo Vo3 = new Catelog2Vo.Catelog3Vo();
                                    Vo3.setCatalog2Id(i3.getParentCid().toString());
                                    Vo3.setId(i3.getCatId().toString());
                                    Vo3.setName(i3.getName());
                                    return Vo3;
                                }).collect(Collectors.toList());
                            }
                            Vo2.setCatalog3List(vo3s);
                            return Vo2;
                        }).collect(Collectors.toList());
                    }
                    return vo2s;
                }));

        catalogJson = JSON.toJSONString(data);
        // 2.放入redis缓存中
        redisTemplate.opsForValue().set("catalogJson", catalogJson, 1000 + new Random().nextInt(100), TimeUnit.SECONDS);

        return data;
    }

    public List<CategoryEntity> getParent_cid(List<CategoryEntity> allCategory, Long pCid) {
        return allCategory.stream().filter(i -> i.getParentCid().equals(pCid)).collect(Collectors.toList());
    }

    // 递归收集父节点
    private List<Long> findParentPaths(Long catelogId, List<Long> paths) {
        // 1.收集当前节点id
        paths.add(catelogId);
        // 2.寻找是否有父亲
        CategoryEntity category = getById(catelogId);
        if (category.getParentCid() != 0) {
            findParentPaths(category.getParentCid(), paths); // 递归
        }
        return paths;
    }

    /**
     * 查找当前标签的子标签
     *
     * @param root 需要查找的标签
     * @param all  所有标签
     * @return 返回root的子标签集合
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        return all
                .stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == root.getCatId())  // 条件：当all中的标签的父id是root的id
                .map(c -> {
                    c.setChildren(getChildren(c, all)); // 递归查找，当前标签的子标签，比如说当前是2级标签，就查找他的子标签->3级标签
                    return c;
                })
                .sorted((c1, c2) -> (c1.getSort() == null ? 0 : c1.getSort()) - (c2.getSort() == null ? 0 : c2.getSort())) // 排序
                .collect(Collectors.toList()); // 收集
    }
}