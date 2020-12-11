package com.like.mall.search.controller;

import com.like.mall.search.service.MallSearchService;
import com.like.mall.search.vo.SearchParam;
import com.like.mall.search.vo.SearchResData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author like
 * @email 980650920@qq.com
 * @since 2020-12-10 11:32
 */
@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    /**
     * 在es中搜索已经上架商品的信息
     *
     * @param param 参数
     * @param model 模型
     * @return {@link String}* @throws IOException ioexception
     */
    @GetMapping("/list.html")
    public String list(SearchParam param, Model model, HttpServletRequest httpRequest) throws IOException {
        param.set_queryString(httpRequest.getQueryString());
        SearchResData data = mallSearchService.search(param);
        model.addAttribute("result", data);
        return "list";
    }

    @GetMapping("/getData")
    @ResponseBody
    public SearchResData getData(SearchParam param, Model model) throws IOException {
        return mallSearchService.search(param);
    }
}
