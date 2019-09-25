package com.example.se;

import com.example.se.dao.CommentDAO;
import com.example.se.dao.DishDAO;
import com.example.se.dao.LoginTicketDAO;
import com.example.se.dao.UserDAO;
import com.example.se.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SeApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTests {

    @Autowired
    DishDAO dishDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;

    @Autowired
    CommentDAO commentDAO;

    @Test
    public void initData() {
        Random random = new Random();
        for (int i = 0; i < 11; ++i) {
            User user = new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            user.setName(String.format("USER%d", i));
            user.setPassword("newpassword");
            user.setSalt("");
            if (i == 2) {
                user.setName("admin");
                user.setSalt("e47eb");
                user.setPassword("0F10338AEA688DCB9007C3AF427665CC");
            }
            userDAO.addUser(user);

            Dish dish = new Dish();
            dish.setCommentCount(0);
            Date date = new Date();
            date.setTime(date.getTime() - 1000*3600*5*(10-i));
            dish.setCreatedDate(date);
            dish.setImage(String.format("/images/%d.jpg", i));
            dish.setLikeCount(0);
            dish.setUserId(i+1);
            dish.setTitle(String.format("Name{%d}", i));
            dish.setLink(String.format("detail" + i));
            dishDAO.addDish(dish);

            for (int j = 0; j < 3; ++j) {
                Comment comment = new Comment();
                comment.setUserId(i+1);
                comment.setEntityId(dish.getId());
                comment.setEntityType(EntityType.ENTITY_DISH);
                comment.setStatus(0);
                comment.setCreatedDate(new Date());
                comment.setContent("Comment " + String.valueOf(j));
                commentDAO.addComment(comment);
            }

            userDAO.updatePassword(user);

            LoginTicket ticket = new LoginTicket();
            ticket.setStatus(0);
            ticket.setUserId(i+1);
            ticket.setExpired(date);
            ticket.setTicket(String.format("TICKET%d", i+1));
            loginTicketDAO.addTicket(ticket);

            loginTicketDAO.updateStatus(ticket.getTicket(), 2);

        }

        Assert.assertEquals("newpassword", userDAO.selectById(1).getPassword());
        userDAO.deleteById(1);
        Assert.assertNull(userDAO.selectById(1));

        Assert.assertEquals(1, loginTicketDAO.selectByTicket("TICKET1").getUserId());
        Assert.assertEquals(2, loginTicketDAO.selectByTicket("TICKET1").getStatus());

        Assert.assertNotNull(commentDAO.selectByEntity(1, EntityType.ENTITY_DISH).get(0));
    }

}
