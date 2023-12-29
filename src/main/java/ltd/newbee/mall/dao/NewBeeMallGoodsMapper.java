/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.entity.SkuColumnMangementEntity;
import ltd.newbee.mall.entity.SkuEntity;
import ltd.newbee.mall.entity.Answer;
import ltd.newbee.mall.entity.Comment;
import ltd.newbee.mall.entity.GoodsReview;
import ltd.newbee.mall.entity.StockNumDTO;
import ltd.newbee.mall.entity.UserCheckedHistory;
import ltd.newbee.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface NewBeeMallGoodsMapper {
	List<Answer> getAnswerById(List<Long> answerId);
	int deleteAnswerById(@Param("answerId") List<Long> answerId);
	void updateAnswerById(Map<Object, String> updateAnswer);
	List<Answer> findAnswerList(PageQueryUtil pageUtil);
	int getTotalAnswer(PageQueryUtil pageUtil);
	void setUserCheckedHistory(UserCheckedHistory userCheckedHistory);
	List<Comment> findCommentList(PageQueryUtil pageUtil);
	int getTotalComment(PageQueryUtil pageUtil);
	List<Long> findLikeUserId(Long commentId);
	List<Long> selectCommentId(PageQueryUtil pageUtil);
	int getLikesCount(Long commentId);
	void setCommentLike(Map<Object, Long> commentLike);
	void deleteCommentLike(Map<Object, Long> commentLikeDelete);
	void setCommentSbumit(Comment newComment);
	void setGoodsReview(GoodsReview goodsReview);

	
    int deleteByPrimaryKey(Long goodsId);

    int insert(NewBeeMallGoods record);

    int insertSelective(NewBeeMallGoods record);

    NewBeeMallGoods selectByPrimaryKey(Long goodsId);
    NewBeeMallGoods selectBySkuId(Long skuId);

    NewBeeMallGoods selectByCategoryIdAndName(@Param("goodsName") String goodsName, @Param("goodsCategoryId") Long goodsCategoryId);

    int updateByPrimaryKeySelective(NewBeeMallGoods record);

    int updateByPrimaryKeyWithBLOBs(NewBeeMallGoods record);

    int updateByPrimaryKey(NewBeeMallGoods record);

    List<NewBeeMallGoods> findNewBeeMallGoodsList(PageQueryUtil pageUtil);
    
    int getTotalNewBeeMallGoods(PageQueryUtil pageUtil);
    
    List<SkuColumnMangementEntity> selectSkuColumnMangement(List<String> column);
    String selectSkuId(Map<String, String> paramList);
    SkuEntity selectSkuById(String skuId);
    List<String> selectImgById(String skuId);

    int showAllNewBeeMallGoods(PageQueryUtil pageUtil);

    List<NewBeeMallGoods> selectByPrimaryKeys(List<Long> goodsIds);

    List<NewBeeMallGoods> findNewBeeMallGoodsListBySearch(PageQueryUtil pageUtil);
    List<NewBeeMallGoods> showAllNewBeeMallGoodsList(PageQueryUtil pageUtil);

    int getTotalNewBeeMallGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("newBeeMallGoodsList") List<NewBeeMallGoods> newBeeMallGoodsList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int recoverStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);

}