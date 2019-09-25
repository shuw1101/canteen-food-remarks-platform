package com.example.se.controller;

import com.example.se.async.EventModel;
import com.example.se.async.EventProducer;
import com.example.se.async.EventType;
import com.example.se.model.Dish;
import com.example.se.model.EntityType;
import com.example.se.model.HostHolder;
import com.example.se.service.DishService;
import com.example.se.service.LikeService;
import com.example.se.util.SeUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    DishService dishService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@Param("dishId") int dishId) {
        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_DISH, dishId);
        // 更新喜欢数
        Dish dish = dishService.getById(dishId);
        dishService.updateLikeCount(dishId, (int) likeCount);
        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setEntityOwnerId(dish.getUserId())
                .setActorId(hostHolder.getUser().getId()).setEntityId(dishId));
        return SeUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dislike(@Param("dishId") int dishId) {
        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_DISH, dishId);
        // 更新喜欢数
        dishService.updateLikeCount(dishId, (int) likeCount);
        return SeUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
