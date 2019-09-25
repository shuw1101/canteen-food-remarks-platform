package com.example.se.controller;

import com.example.se.model.*;
import com.example.se.service.CommentService;
import com.example.se.service.DishService;
import com.example.se.service.LikeService;
import com.example.se.service.UserService;
import com.example.se.util.SeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class DishController {
    private static final Logger logger = LoggerFactory.getLogger(DishController.class);
    @Autowired
    DishService dishService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @RequestMapping(path = {"/dish/{dishId}"}, method = {RequestMethod.GET})
    public String dishDetail(@PathVariable("dishId") int dishId, Model model) {
        Dish dish = dishService.getById(dishId);
        if (dish != null) {
            int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
            if (localUserId != 0) {
                model.addAttribute("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_DISH, dish.getId()));
            } else {
                model.addAttribute("like", 0);
            }

            List<Comment> comments=commentService.getCommentsByEntity(dish.getId(), EntityType.ENTITY_DISH);
            List<ViewObject> commentVOs = new ArrayList<>();
            for (Comment comment : comments) {
                ViewObject vo = new ViewObject();
                vo.set("comment", comment);
                vo.set("user", userService.getUser(comment.getUserId()));
                commentVOs.add(vo);
            }
            model.addAttribute("comments", commentVOs);
        }
        model.addAttribute("dish", dish);
        model.addAttribute("owner", userService.getUser(dish.getUserId()));
        return "detail";
    }

    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("dishId") int dishId, @RequestParam("content") String content) {
        try {
            content = HtmlUtils.htmlEscape(content);
            Comment comment = new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setContent(content);
            comment.setEntityId(dishId);
            comment.setEntityType(EntityType.ENTITY_DISH);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);

            commentService.addComment(comment);

            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            dishService.updateCommentCount(comment.getEntityId(), count);
        } catch (Exception e) {
            logger.error("add comment error" + e.getMessage());
        }
        return "redirect:/dish/" + (dishId);
    }

    @RequestMapping(path = {"/image"}, method = RequestMethod.GET)
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName, HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(new File(SeUtil.IMAGE_DIR + imageName)), response.getOutputStream());
        } catch (Exception e) {
            logger.error("read pic error" + e.getMessage());
        }
    }

    @RequestMapping(path = {"/uploadImage/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = dishService.saveImage(file);
            if (fileUrl == null) {
                return SeUtil.getJSONString(1, "upload error");
            }
            return SeUtil.getJSONString(0, fileUrl);
        } catch (Exception e) {
            logger.error("upload error" + e.getMessage());
            return SeUtil.getJSONString(1, "upload err");
        }
    }

    @RequestMapping(path = {"/user/addDish/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addDish(@RequestParam("image") String image, @RequestParam("title") String title,
                          @RequestParam("link") String link) {
        try {
            Dish dish = new Dish();
            dish.setCreatedDate(new Date());
            dish.setTitle(title);
            dish.setImage(image);
            dish.setLink(link);
            if (hostHolder.getUser() != null) {
                dish.setUserId(hostHolder.getUser().getId());
            } else {
                dish.setUserId(1);
            }
            dishService.addDish(dish);
            return SeUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("add dish error" + e.getMessage());
            return SeUtil.getJSONString(1, "add dish error");
        }
    }
}
