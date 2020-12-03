package com.like.mall.product.controller.web;

import com.like.mall.product.entity.CategoryEntity;
import com.like.mall.product.service.CategoryService;
import com.like.mall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author like
 * @since 2020-11-19 14:49
 * web 路径跳转
 */
@Controller
public class IndexController {
    @Resource
    CategoryService categoryService;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        // 1.查询所有的一级分类
        List<CategoryEntity> ens = categoryService.getLevelFirstCategory();
        model.addAttribute("data", ens);
        return "index";
    }

    /**
     * 获取所有二级和三级分类的json数据
     *
     * @return {@link Map<String, List<Catelog2Vo>>}
     */
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        return categoryService.getCatalogJson();
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        // 1.获取锁
        RLock lock = redisson.getLock("hello");

        // 2.上锁
        /**
         * a、锁会自动续期，默认续期到30s，不用担心业务时间过长，导致锁过期被删除
         * b、加锁的业务只要运行完成，就不会续期，当完成后就会在30s内删除
         */
        lock.lock();
        try {
            System.out.println("加锁成功,执行业务"+Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            System.out.println("解锁"+Thread.currentThread().getName());
            // 解锁
            lock.unlock();
        }

        return "hello";
    }

    @Autowired
    RedissonClient redisson;
}
