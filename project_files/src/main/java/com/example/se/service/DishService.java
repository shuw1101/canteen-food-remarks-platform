package com.example.se.service;

import com.example.se.dao.DishDAO;
import com.example.se.model.Dish;
import com.example.se.util.SeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class DishService {
    @Autowired
    private DishDAO dishDAO;

    public List<Dish> getLatesDish(int userId, int offset, int limit) {
        return dishDAO.selectByUserIdAndOffset(userId, offset, limit);
    }

    public int addDish(Dish dish) {
        dishDAO.addDish(dish);
        return dish.getId();
    }

    public Dish getById(int dishId) {
        return dishDAO.getById(dishId);
    }

    public String saveImage(MultipartFile file) throws IOException {
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if (dotPos < 0) {
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
        if (!SeUtil.isFileAllowed(fileExt)) {
            return null;
        }

        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
        Files.copy(file.getInputStream(), new File(SeUtil.IMAGE_DIR+fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        return SeUtil.TOUTIAO_DOMAIN + "image?name=" + fileName;
    }

    public int updateCommentCount(int id, int count) {
        return dishDAO.updateCommentCount(id, count);
    }

    public int updateLikeCount(int id, int count){
        return dishDAO.updateLikeCount(id, count);
    }
}
