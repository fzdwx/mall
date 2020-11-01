package com.like.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.product.dao.CategoryDao;
import com.like.mall.product.entity.CategoryEntity;
import com.like.mall.product.service.CategoryBrandRelationService;
import com.like.mall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

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
                .map(c -> {
                    c.setChildren(getChildren(c, entities)); // 查找当前标签的子标签
                    return c;
                })
                .sorted((c1, c2) -> (c1.getSort() == null ? 0 : c1.getSort()) - (c2.getSort() == null ? 0 : c2.getSort())) // 排序
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

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 级联更新所有关联的数据
     *
     * @param category
     */
    @Override
    public void updateDetail(CategoryEntity category) {
        // 1.先更新自己
        updateById(category);
        // 2.更新和品牌相关的内容
        categoryBrandRelationService.updateCategory(category);
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