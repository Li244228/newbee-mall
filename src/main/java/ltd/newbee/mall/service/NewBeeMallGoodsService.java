/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.service;

import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.entity.UserCheckedHistory;
import ltd.newbee.mall.entity.Answer;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public interface NewBeeMallGoodsService {
	List<Answer> getAnswerById(List<Long> answerId);
	int deleteAnswerById(List<Long> answerId);
	void updateAnswerById(Map<Object, String> updateAnswer);
	PageResult getAnswerPage(PageQueryUtil pageUtil);
	void setUserCheckedHistory(UserCheckedHistory userCheckedHistory);
	PageResult getCommentPage(PageQueryUtil pageUtil, long goodsId);
	void setCommentLike(Map<Object, Long> commentLike);
	void deleteCommentLike(Map<Object, Long> commentLikeDelete);
	void setCommentSubmit(Map<String, Object> commentSubmit);
	void setGoodsReview(Map<String, Object> goodsReviewList, MultipartFile[] file);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getNewBeeMallGoodsPage(PageQueryUtil pageUtil);

    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    String saveNewBeeMallGoods(NewBeeMallGoods goods);

    /**
     * 批量新增商品数据
     *
     * @param newBeeMallGoodsList
     * @return
     */
    void batchSaveNewBeeMallGoods(List<NewBeeMallGoods> newBeeMallGoodsList);

    /**
     * 修改商品信息
     *
     * @param goods
     * @return
     */
    String updateNewBeeMallGoods(NewBeeMallGoods goods);

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    NewBeeMallGoods getNewBeeMallGoodsById(Long id);

    /**
     * 批量修改销售状态(上架下架)
     *
     * @param ids
     * @return
     */
    Boolean batchUpdateSellStatus(Long[] ids,int sellStatus);

    /**
     * 商品搜索
     *
     * @param pageUtil
     * @return
     */
    PageResult searchNewBeeMallGoods(PageQueryUtil pageUtil);
    PageResult showAllNewBeeMallGoods(PageQueryUtil pageUtil);
}
