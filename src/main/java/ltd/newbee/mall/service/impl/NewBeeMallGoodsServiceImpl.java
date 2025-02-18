/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.common.NewBeeMallCategoryLevelEnum;
import ltd.newbee.mall.common.NewBeeMallException;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallSearchGoodsVO;
import ltd.newbee.mall.dao.GoodsCategoryMapper;
import ltd.newbee.mall.dao.NewBeeMallGoodsMapper;
import ltd.newbee.mall.entity.Answer;
import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.entity.Comment;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.entity.GoodsReview;
import ltd.newbee.mall.entity.GoodsReviewPhoto;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.entity.UserCheckedHistory;
import ltd.newbee.mall.service.NewBeeMallGoodsService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.NewBeeMallUtils;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class NewBeeMallGoodsServiceImpl implements NewBeeMallGoodsService {

    @Autowired
    private NewBeeMallGoodsMapper goodsMapper;
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public PageResult getNewBeeMallGoodsPage(PageQueryUtil pageUtil) {
        List<NewBeeMallGoods> goodsList = goodsMapper.findNewBeeMallGoodsList(pageUtil);
        int total = goodsMapper.getTotalNewBeeMallGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveNewBeeMallGoods(NewBeeMallGoods goods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        if (goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId()) != null) {
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        goods.setGoodsName(NewBeeMallUtils.cleanString(goods.getGoodsName()));
        goods.setGoodsIntro(NewBeeMallUtils.cleanString(goods.getGoodsIntro()));
        goods.setTag(NewBeeMallUtils.cleanString(goods.getTag()));
        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveNewBeeMallGoods(List<NewBeeMallGoods> newBeeMallGoodsList) {
        if (!CollectionUtils.isEmpty(newBeeMallGoodsList)) {
            goodsMapper.batchInsert(newBeeMallGoodsList);
        }
    }

    @Override
    public String updateNewBeeMallGoods(NewBeeMallGoods goods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        NewBeeMallGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        NewBeeMallGoods temp2 = goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId());
        if (temp2 != null && !temp2.getGoodsId().equals(goods.getGoodsId())) {
            //name和分类id相同且不同id 不能继续修改
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        goods.setGoodsName(NewBeeMallUtils.cleanString(goods.getGoodsName()));
        goods.setGoodsIntro(NewBeeMallUtils.cleanString(goods.getGoodsIntro()));
        goods.setTag(NewBeeMallUtils.cleanString(goods.getTag()));
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public NewBeeMallGoods getNewBeeMallGoodsById(Long id) {
        NewBeeMallGoods newBeeMallGoods = goodsMapper.selectByPrimaryKey(id);
        if (newBeeMallGoods == null) {
            NewBeeMallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        return newBeeMallGoods;
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchNewBeeMallGoods(PageQueryUtil pageUtil) {
        List<NewBeeMallGoods> goodsList = goodsMapper.findNewBeeMallGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalNewBeeMallGoodsBySearch(pageUtil);
        List<NewBeeMallSearchGoodsVO> newBeeMallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            newBeeMallSearchGoodsVOS = BeanUtil.copyList(goodsList, NewBeeMallSearchGoodsVO.class);
            for (NewBeeMallSearchGoodsVO newBeeMallSearchGoodsVO : newBeeMallSearchGoodsVOS) {
                String goodsName = newBeeMallSearchGoodsVO.getGoodsName();
                String goodsIntro = newBeeMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    newBeeMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    newBeeMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(newBeeMallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
    
    @Override
    public PageResult showAllNewBeeMallGoods(PageQueryUtil pageUtil) {
        List<NewBeeMallGoods> goodsList = goodsMapper.showAllNewBeeMallGoodsList(pageUtil);
        int total = goodsMapper.showAllNewBeeMallGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

	@Override
	public List<Answer> getAnswerById(List<Long> answerId) {
		// TODO Auto-generated method stub
		List<Answer> answer = goodsMapper.getAnswerById(answerId);
		return answer;
	}
	
	@Override
	public int deleteAnswerById(List<Long> answerId) {
		// TODO Auto-generated method stub
		int delete = goodsMapper.deleteAnswerById(answerId);
		return delete;
	}
	
	@Override
	public void updateAnswerById(Map<Object, String> updateAnswer) {
		// TODO Auto-generated method stub
		goodsMapper.updateAnswerById(updateAnswer);

	}

	@Override
    public PageResult getAnswerPage(PageQueryUtil pageUtil) {
        List<Answer> answerList = goodsMapper.findAnswerList(pageUtil);
        int total = goodsMapper.getTotalAnswer(pageUtil);
        PageResult pageResult = new PageResult(answerList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
	
	public void setUserCheckedHistory(UserCheckedHistory userCheckedHistory) {
		goodsMapper.setUserCheckedHistory(userCheckedHistory);
    }
	
	@Override
    public PageResult getCommentPage(PageQueryUtil pageUtil, long goodsId) {
        List<Comment> commentList = goodsMapper.findCommentList(pageUtil);
        List<Long> commentId = goodsMapper.selectCommentId(pageUtil);
        List<List<Long>> likeUserIdList = new ArrayList<List<Long>>();
        List<Integer> likesCountList = new ArrayList<Integer>();
        for(Long cmtId : commentId) {
        	List<Long> likeUserId = goodsMapper.findLikeUserId(cmtId);
        	likesCountList.add(goodsMapper.getLikesCount(cmtId));
        	likeUserIdList.add(likeUserId);
        }
        for (int i = 0; i < commentList.size(); i++) {
        	commentList.get(i).setLikeUserId(likeUserIdList.get(i));
        	commentList.get(i).setLikesCount(likesCountList.get(i));
        }
        int total = goodsMapper.getTotalComment(pageUtil);
        PageResult pageResult = new PageResult(commentList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
	
	@Override
	public void setCommentLike(Map<Object, Long> commentLike) {
		// TODO Auto-generated method stub
		goodsMapper.setCommentLike(commentLike);
	}
	
	@Override
	public void deleteCommentLike(Map<Object, Long> commentLikeDelete) {
		// TODO Auto-generated method stub
		goodsMapper.deleteCommentLike(commentLikeDelete);
	}
	
	@Override
	public void setCommentSubmit(Map<String, Object> commentSubmit) {
		Comment newComment = new Comment();
		newComment.setTime(new Date());
		newComment.setUserId(Long.valueOf(commentSubmit.get("userId").toString()));
		newComment.setComment(commentSubmit.get("comment").toString());
		newComment.setGoodsId(Long.valueOf(commentSubmit.get("goodsId").toString()));
		goodsMapper.setCommentSbumit(newComment);
	}
	
	@Override
	public void setGoodsReview(Map<String, Object> goodsReviewList, MultipartFile[] file) {
		GoodsReview goodsReview = new GoodsReview();
		goodsReview.setNickName(goodsReviewList.get("nickName").toString());
		goodsReview.setReview(Long.valueOf(goodsReviewList.get("review").toString()));
		goodsReview.setTitle(goodsReviewList.get("title").toString());
		goodsReview.setText(goodsReviewList.get("text").toString());
		goodsReview.setGoodsId(Long.valueOf(goodsReviewList.get("goodsId").toString()));
		goodsReview.setUserId(Long.valueOf(goodsReviewList.get("userId").toString()));
		goodsReview.setTime(new Date());
		String[] photo = new String[file.length];
	    try {
	    	for(int i = 0; i< photo.length; i++) {
	    		GoodsReviewPhoto goodsReviewPhoto = new GoodsReviewPhoto();
	    		Path dst = Path.of("C:\\Users\\liyin\\OneDrive\\画像\\destination", file[i].getOriginalFilename());
	    		Files.copy(file[i].getInputStream(), dst, StandardCopyOption.REPLACE_EXISTING);
	    		goodsReviewPhoto.setPhoto(dst.toString()); 
	    		goodsReviewPhoto.setGoodsId(Long.valueOf(goodsReviewList.get("goodsId").toString()));
	    		goodsReviewPhoto.setUserId(Long.valueOf(goodsReviewList.get("userId").toString()));
	    		goodsReviewPhoto.setReview(Long.valueOf(goodsReviewList.get("review").toString()));
	    		goodsMapper.setGoodsReviewPhoto(goodsReviewPhoto);
	    	}
        } catch (IOException e) {
            System.err.println("Error copying file: " + e.getMessage());
            // 在实际应用中，可能需要采取适当的措施来处理异常，例如记录日志或向用户显示错误信息。
        }

		goodsMapper.setGoodsReview(goodsReview);
	}

}
