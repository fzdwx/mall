package com.like.mall.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author like
 * @email 980650920@qq.com
 * @since 2020-12-10 11:32
 */
@Controller
public class SearchController {

    @GetMapping("/list.html")
    public String list() {
        return "list";
    }
}
