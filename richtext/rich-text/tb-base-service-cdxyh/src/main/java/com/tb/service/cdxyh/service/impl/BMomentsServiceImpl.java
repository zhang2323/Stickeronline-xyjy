package com.tb.service.cdxyh.service.impl;

import com.sticker.online.core.anno.AsyncServiceHandler;
import com.sticker.online.core.model.BaseAsyncService;
import com.tb.base.common.vo.PageVo;
import com.tb.service.baidu.service.impl.ContentCensorServiceImpl;
import com.tb.service.cdxyh.entity.BMomentsCommentEntity;
import com.tb.service.cdxyh.entity.BMomentsEntity;
import com.tb.service.cdxyh.entity.BMomentsLikeEntity;
import com.tb.service.cdxyh.repository.BMomentsCommentRepository;
import com.tb.service.cdxyh.repository.BMomentsLikeRepository;
import com.tb.service.cdxyh.repository.BMomentsRepository;
import com.tb.service.cdxyh.service.BMomentsService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@AsyncServiceHandler
public class BMomentsServiceImpl implements BMomentsService, BaseAsyncService {
    @Autowired
    private BMomentsRepository bMomentsRepository;
    @Autowired
    private BMomentsCommentRepository bMomentsCommentRepository;
    @Autowired
    private BMomentsLikeRepository bMomentsLikeRepository;
    @Autowired
    private ContentCensorServiceImpl contentCensorServiceImpl;

    @Override
    public void add(JsonObject params, Handler<AsyncResult<JsonObject>> handler) {
        Future<JsonObject> future = Future.future();
        BMomentsEntity bMomentsEntity = new BMomentsEntity(params);
        bMomentsEntity.setCreateTime(new Date());
        BMomentsEntity save = bMomentsRepository.save(bMomentsEntity);
        contentCensorServiceImpl.getContentCensorInfo(new JsonObject().put("text",bMomentsEntity.getContent()),res->{
            if(res.succeeded()&&res.result().getInteger("conclusionType")==1){
                save.setStatus(1);
                BMomentsEntity save1=bMomentsRepository.save(save);
                future.complete(new JsonObject(Json.encode(save1)));
            }else{
                save.setStatus(-1);
                BMomentsEntity save1=bMomentsRepository.save(save);
                future.complete(new JsonObject(Json.encode(save1)));
            }
        });
        handler.handle(future);
    }

    @Override
    public void queryPageList(JsonObject params, Handler<AsyncResult<JsonObject>> handler) {
        Future<JsonObject> future = Future.future();
        PageVo pageVo = new PageVo(params);
        BMomentsEntity bMomentsEntity = new BMomentsEntity(params);
        String likeCount = params.getString("order");
        String selfId = params.getString("selfId");
        Sort sort;
        if (likeCount != null&&!likeCount.isEmpty()){
            sort =new Sort(Sort.Direction.DESC, likeCount);
        } else {
            sort = new Sort(Sort.Direction.DESC, "createTime");
        }
        Pageable pageable = PageRequest.of(pageVo.getPageNo() - 1, pageVo.getPageSize(), sort);
        ExampleMatcher matcher = ExampleMatcher.matching(); //构建对象
        matcher.withMatcher("status", ExampleMatcher.GenericPropertyMatchers.contains());
        //创建实例
        bMomentsEntity.setStatus(1);
        if (selfId != null&&!selfId.isEmpty()){
            bMomentsEntity.setUserId(selfId);
        }
        Example<BMomentsEntity> ex = Example.of(bMomentsEntity,matcher);

        Page<BMomentsEntity> plist = bMomentsRepository.findAll(ex,pageable);
        JsonObject resObj = new JsonObject(Json.encode(plist));
        JsonArray contents = resObj.getJsonArray("content");
        for (int i = 0; i < contents.size(); i++) {
            JsonObject content = contents.getJsonObject(i);
            String commentId = content.getString("id");
            //统计浏览量
            Integer viewCount = content.getInteger("viewCount");
            if (viewCount != null){
                content.put("viewCount",viewCount+1);
            } else {
                content.put("viewCount",1);
            }

//            bMomentsRepository.save(new BMomentsEntity(content));
//            //获取评论信息
//            List<BMomentsCommentEntity> commentList = bMomentsCommentRepository.queryByCommentId(commentId);
//            resObj.getJsonArray("content").getJsonObject(i).put("commentList1", new JsonArray(Json.encode(commentList)));
            //获取当前用户点赞状态
            List<BMomentsLikeEntity> likeList = bMomentsLikeRepository.findAllByUserIdAndMomentId(params.getString("userId"),commentId);
            if(likeList.size()>0){
                resObj.getJsonArray("content").getJsonObject(i).put("islike", likeList.get(0).getStatus());
            }else {
                resObj.getJsonArray("content").getJsonObject(i).put("islike", "unlike");
            }
        }
        future.complete(resObj);
        handler.handle(future);
    }

    @Override
    public void edit(JsonObject params, Handler<AsyncResult<JsonObject>> handler) {
        Future<JsonObject> future = Future.future();
        BMomentsEntity bMomentsEntity = new BMomentsEntity(params);
        bMomentsEntity.setCreateTime(new Date());
        BMomentsEntity save = bMomentsRepository.save(bMomentsEntity);
        contentCensorServiceImpl.getContentCensorInfo(new JsonObject().put("text",bMomentsEntity.getContent()),res->{
            if(res.succeeded()&&res.result().getInteger("conclusionType")==1){
                save.setStatus(1);
                BMomentsEntity save1=bMomentsRepository.save(save);
                future.complete(new JsonObject(Json.encode(save1)));
            }else{
                save.setStatus(-1);
                BMomentsEntity save1=bMomentsRepository.save(save);
                future.complete(new JsonObject(Json.encode(save1)));
            }
        });
        handler.handle(future);
    }
    @Override
    public void delete(JsonObject params, Handler<AsyncResult<String>> handler) {
        Future<String> future = Future.future();
        String[] ids = params.getString("id").split(",");
        for (int i = 0; i < ids.length; i++) {
            bMomentsRepository.deleteById(ids[i]);
        }
        future.complete("删除成功!");
        handler.handle(future);
    }


    @Override
    public void queryall(JsonObject params, Handler<AsyncResult<JsonArray>> handler) {
        Future<JsonArray> future = Future.future();
//        ExampleMatcher matcher = ExampleMatcher.matching(); //构建对象
        BMomentsEntity bMomentsEntity = new BMomentsEntity(params);
//        matcher.withMatcher("userId", ExampleMatcher.GenericPropertyMatchers.contains());
        //创建实例
        Example<BMomentsEntity> ex = Example.of(bMomentsEntity);
        List<BMomentsEntity> newsList = bMomentsRepository.findAll(ex);
        JsonArray resList = new JsonArray(Json.encode(newsList));
       for (int i = 0; i < resList.size(); i++) {
            String commentId = resList.getJsonObject(i).getString("id");
            List<BMomentsCommentEntity> commentList = bMomentsCommentRepository.queryByCommentId(commentId);
            System.out.println(i);
           resList.getJsonObject(i).put("commentList", commentList);
        }
       future.complete(resList);
        handler.handle(future);
    }

    @Override
    public void queryById(JsonObject params, Handler<AsyncResult<JsonObject>> handler){
        Future<JsonObject> future = Future.future();
        ExampleMatcher matcher = ExampleMatcher.matching();
        BMomentsEntity bAlummunsMemberEntity = new BMomentsEntity(params);
        Optional<BMomentsEntity> res = bMomentsRepository.findById(bAlummunsMemberEntity.getId());
        if (res.isPresent()){
            future.complete(new JsonObject(Json.encode(res.get())));
        } else {
            future.complete(new JsonObject());
        }
        handler.handle(future);
    }

    @Override
    public void likeClick(JsonObject params, Handler<AsyncResult<JsonObject>> handler) {
        Future<JsonObject> future = Future.future();
        String type = params.getString("type");  //like unlike
        String id = params.getString("id");
        Optional<BMomentsEntity> res =  bMomentsRepository.findById(id);
        if (res.isPresent()){
            Integer likeCount = res.get().getLikeCount();
            BMomentsEntity bMomentsEntity = new BMomentsEntity();
            if (type.equals("like")){
                bMomentsEntity.setLikeCount(likeCount+1);
            } else if (type.equals("unlike") && likeCount<0){
                bMomentsEntity.setLikeCount(likeCount-1);
            }
            BMomentsEntity update = bMomentsRepository.save(bMomentsEntity);
            future.complete(new JsonObject(Json.encode(res.get())));
        } else {
            future.complete(new JsonObject());
        }

        handler.handle(future);
    }

    @Override
    public void queryByUserId(JsonObject params, Handler<AsyncResult<JsonObject>> handler) {
        Future<JsonObject> future = Future.future();
        PageVo pageVo = new PageVo(params);
        BMomentsEntity bMomentsEntity = new BMomentsEntity(params);
        String likeCount = params.getString("order");
        String userId = params.getString("userId");
        Sort sort;
        if (likeCount != null&&!likeCount.isEmpty()){
            sort =new Sort(Sort.Direction.DESC, likeCount);
        } else {
            sort = new Sort(Sort.Direction.DESC, "createTime");
        }
        Pageable pageable = PageRequest.of(pageVo.getPageNo() - 1, pageVo.getPageSize(), sort);
        ExampleMatcher matcher = ExampleMatcher.matching(); //构建对象
        matcher.withMatcher("status", ExampleMatcher.GenericPropertyMatchers.contains());
        //创建实例
        bMomentsEntity.setStatus(1);
        Example<BMomentsEntity> ex = Example.of(bMomentsEntity,matcher);
        Integer offset=(pageVo.getPageNo()-1)*pageVo.getPageSize();
        List<BMomentsEntity> plist = bMomentsRepository.queryUserId(userId,pageable.getPageSize(),offset);
        JsonObject resObj = new JsonObject();
        JsonArray contents=new JsonArray();

        for (int i = 0; i < plist.size(); i++) {
            JsonObject content = plist.get(i).toJson();
            String commentId = content.getString("id");
            content.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(plist.get(i).getCreateTime()));
            //统计浏览量
            Integer viewCount = content.getInteger("viewCount");
            if (viewCount != null){
                content.put("viewCount",viewCount+1);
            } else {
                content.put("viewCount",1);
            }
            List<BMomentsLikeEntity> likeList = bMomentsLikeRepository.findAllByUserIdAndMomentId(params.getString("userId"),commentId);
            if(likeList.size()>0){
                content.put("islike", likeList.get(0).getStatus());
            }else {
                content.put("islike", "unlike");
            }
            contents.add(content);
        }
        resObj.put("content",contents);
        future.complete(resObj);
        handler.handle(future);
    }

    @Override
    public void queryByAlumnusId(JsonObject params, Handler<AsyncResult<JsonObject>> handler) {
        Future<JsonObject> future = Future.future();
        PageVo pageVo = new PageVo(params);
        BMomentsEntity bMomentsEntity = new BMomentsEntity(params);
        String likeCount = params.getString("order");
        String alumnusId = params.getString("alumnusId");
        Sort sort;
        if (likeCount != null&&!likeCount.isEmpty()){
            sort =new Sort(Sort.Direction.DESC, likeCount);
        } else {
            sort = new Sort(Sort.Direction.DESC, "createTime");
        }
        Pageable pageable = PageRequest.of(pageVo.getPageNo() - 1, pageVo.getPageSize(), sort);
        ExampleMatcher matcher = ExampleMatcher.matching(); //构建对象
        matcher.withMatcher("status", ExampleMatcher.GenericPropertyMatchers.contains());
        //创建实例
        bMomentsEntity.setStatus(1);
        Example<BMomentsEntity> ex = Example.of(bMomentsEntity,matcher);
        Integer offset=(pageVo.getPageNo()-1)*pageVo.getPageSize();
        List<BMomentsEntity> plist = bMomentsRepository.queryByAlumnusId(alumnusId,pageable.getPageSize(),offset);
        JsonObject resObj = new JsonObject();
        JsonArray contents=new JsonArray();

        for (int i = 0; i < plist.size(); i++) {
            JsonObject content = plist.get(i).toJson();
            String commentId = content.getString("id");
            content.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(plist.get(i).getCreateTime()));
            //统计浏览量
            Integer viewCount = content.getInteger("viewCount");
            if (viewCount != null){
                content.put("viewCount",viewCount+1);
            } else {
                content.put("viewCount",1);
            }
            List<BMomentsLikeEntity> likeList = bMomentsLikeRepository.findAllByUserIdAndMomentId(params.getString("userId"),commentId);
            if(likeList.size()>0){
                content.put("islike", likeList.get(0).getStatus());
            }else {
                content.put("islike", "unlike");
            }
            contents.add(content);
        }
        resObj.put("content",contents);
        future.complete(resObj);
        handler.handle(future);
    }

}
