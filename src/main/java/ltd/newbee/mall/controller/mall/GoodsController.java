/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.controller.mall;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.NewBeeMallException;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallGoodsDetailVO;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.controller.vo.SearchPageCategoryVO;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.entity.SkuColumnMangementEntity;
import ltd.newbee.mall.entity.SkuEntity;
import ltd.newbee.mall.entity.UserCheckedHistory;
import ltd.newbee.mall.service.NewBeeMallCategoryService;
import ltd.newbee.mall.service.NewBeeMallGoodsService;
import ltd.newbee.mall.service.SkuColumnMangementService;
import ltd.newbee.mall.service.SkuService;
import ltd.newbee.mall.util.BeanUtil;
import org.apache.commons.beanutils.PropertyUtils;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
//import main.java.ltd.newbee.mall.util.Result;

@Controller
public class GoodsController {

    @Resource
    private NewBeeMallGoodsService newBeeMallGoodsService;
    @Resource
    private NewBeeMallCategoryService newBeeMallCategoryService;
    @Resource
    private SkuColumnMangementService skuColumnMangementService;
    @Resource
    private SkuService skuService;

    @GetMapping({"/search", "/search.html"})
    public String searchPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        //封装分类数据
        if (params.containsKey("goodsCategoryId") && StringUtils.hasText(params.get("goodsCategoryId") + "")) {
            Long categoryId = Long.valueOf(params.get("goodsCategoryId") + "");
            SearchPageCategoryVO searchPageCategoryVO = newBeeMallCategoryService.getCategoriesForSearch(categoryId);
            if (searchPageCategoryVO != null) {
                request.setAttribute("goodsCategoryId", categoryId);
                request.setAttribute("searchPageCategoryVO", searchPageCategoryVO);
            }
        }
        //封装参数供前端回显
        if (params.containsKey("orderBy") && StringUtils.hasText(params.get("orderBy") + "")) {
            request.setAttribute("orderBy", params.get("orderBy") + "");
        }
        String keyword = "";
        //对keyword做过滤 去掉空格
        if (params.containsKey("keyword") && StringUtils.hasText((params.get("keyword") + "").trim())) {
            keyword = params.get("keyword") + "";
        }
        request.setAttribute("keyword", keyword);
        params.put("keyword", keyword);
        //搜索上架状态下的商品
        params.put("goodsSellStatus", Constants.SELL_STATUS_UP);
        //封装商品数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("pageResult", newBeeMallGoodsService.searchNewBeeMallGoods(pageUtil));
        return "mall/search";
    }

    @GetMapping("/goods/detail/{goodsId}")
    public String detailPage(@PathVariable("goodsId") Long goodsId, HttpServletRequest request, HttpSession httpSession) {
        if (goodsId < 1) {
            NewBeeMallException.fail("参数异常");
        }
        NewBeeMallGoods goods = newBeeMallGoodsService.getNewBeeMallGoodsById(goodsId);
        if (Constants.SELL_STATUS_UP != goods.getGoodsSellStatus()) {
            NewBeeMallException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        NewBeeMallGoodsDetailVO goodsDetailVO = new NewBeeMallGoodsDetailVO();
        BeanUtil.copyProperties(goods, goodsDetailVO);
        goodsDetailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
        request.setAttribute("goodsDetail", goodsDetailVO);
        NewBeeMallUserVO user = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        if (user != null) { //想判断是否是登陆状态
        	UserCheckedHistory userCheckedHistory=new UserCheckedHistory();
        	userCheckedHistory.setGoodsId(goodsId);
        	userCheckedHistory.setUserId(user.getUserId());
        	userCheckedHistory.setCheckTime(new Date());
        	newBeeMallGoodsService.setUserCheckedHistory(userCheckedHistory);
        }
        
        return "mall/detail";
    }
    @RequestMapping(value = "/answers", method = RequestMethod.GET)
    @ResponseBody
    public Result answers(@RequestBody List<Long> answerId) {
    	return ResultGenerator.genSuccessResult(newBeeMallGoodsService.getAnswerById(answerId)); 

    }
    

    @RequestMapping(value = "/answers", method = RequestMethod.DELETE)
    @ResponseBody
    public Result answersDelete(@RequestBody List<Long> answerId) {
    	return ResultGenerator.genSuccessResult(newBeeMallGoodsService.deleteAnswerById(answerId)); 

    }
    
    @RequestMapping(value = "/answers", method = RequestMethod.PUT)
    @ResponseBody
    public void answersUpdate(@RequestBody Map<Object, String> updateAnswer) {
    	newBeeMallGoodsService.updateAnswerById(updateAnswer);
    }
    
    @RequestMapping(value = "/answers/list", method = RequestMethod.GET)
    @ResponseBody
    public Result answerList(@RequestParam Map<String, Object> params) {
        if (ObjectUtils.isEmpty(params.get("page")) || ObjectUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(newBeeMallGoodsService.getAnswerPage(pageUtil));
    }
    @RequestMapping(value = "/goods/sku", method = RequestMethod.GET)
    @ResponseBody
    public Result goodsSku(@RequestParam Map<String, String> paramList) {
        List<SkuColumnMangementEntity> skuCmeList = skuColumnMangementService.selectSkuColumnMangement(paramList);
        List<SkuEntity> skuList = skuService.selectSku(paramList);
        
        int i=0, j=0;
        for(SkuEntity sku : skuList) {
        	for(SkuColumnMangementEntity skuCme : skuCmeList) {
        		String col = sku.getColumn1();
        		for(String key : paramList.keySet()) {
        			if(key == skuCme.getColumn1()) {
        				if(col == paramList.get(key)) {
        					j++;
        				}
        			}
        		}
        	}
        	if(j == skuCmeList.size()) {
        		break;
        	}
        	i++;
        }
        System.out.print("**********************");
        System.out.print(skuList);
        System.out.print(skuCmeList);
        return ResultGenerator.genSuccessResult(skuList.get(0));
    }
    
    @RequestMapping(value = "/goods/sku/settled", method = RequestMethod.GET)
    @ResponseBody
    public Result goodsSkuSettled(@RequestParam Map<String, String> paramList) {
        SkuEntity sku = skuService.selectSku(paramList);
        return ResultGenerator.genSuccessResult(sku);
    }
    
    @RequestMapping(value = "/goods/comment", method = RequestMethod.GET)
    @ResponseBody
    public Result goodsComment(@RequestParam Map<String, Object> params) {
    	if (ObjectUtils.isEmpty(params.get("page")) || ObjectUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        long goodsId = Long.valueOf(params.get("goodsId").toString());
        return ResultGenerator.genSuccessResult(newBeeMallGoodsService.getCommentPage(pageUtil, goodsId));
    }
    
    @RequestMapping(value = "/comment/like", method = RequestMethod.PUT)
    @ResponseBody
    public void commentLike(@RequestBody Map<Object, Long> commentLike) {
    	newBeeMallGoodsService.setCommentLike(commentLike);
    }
    
    @RequestMapping(value = "/comment/like/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public void commentLikeDelete(@RequestBody Map<Object, Long> commentLikeDelete) {
    	newBeeMallGoodsService.deleteCommentLike(commentLikeDelete); 
    }
    
    @RequestMapping(value = "/comment/submit", method = RequestMethod.PUT)
    @ResponseBody
    public void commentSubmit(@RequestBody Map<String, Object> commentSubmit) {
    	newBeeMallGoodsService.setCommentSubmit(commentSubmit);
    }
    
    @PostMapping(value = "/goods/review", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result goodsReview(@RequestParam Map<String, Object> goodsReviewList, @RequestPart("file") MultipartFile[] file) {
    	if (file.length > 4) {
    		return ResultGenerator.genFailResult("最多上传五张图片"); 
    	}
    	newBeeMallGoodsService.setGoodsReview(goodsReviewList, file);
    	return ResultGenerator.genSuccessResult();
    }
}
