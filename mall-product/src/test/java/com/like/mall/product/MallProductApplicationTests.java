package com.like.mall.product;

import com.like.mall.product.entity.BrandEntity;
import com.like.mall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MallProductApplicationTests {
    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {
        BrandEntity ent = new BrandEntity();
        ent.setDescript("hello");
        ent.setName("like");
        boolean save = brandService.save(ent);
        System.out.println(save);
    }

    @Test
    void hello() {
        System.out.println("hello");
    }
}
