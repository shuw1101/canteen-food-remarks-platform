package com.example.se.async.handler;

import com.example.se.async.EventHandler;
import com.example.se.async.EventModel;
import com.example.se.async.EventType;
import com.example.se.model.Message;
import com.example.se.model.User;
import com.example.se.service.MessageService;
import com.example.se.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        User user = userService.getUser(model.getActorId());
        message.setToId(model.getEntityOwnerId());
        message.setContent("用户" + user.getName() + " 赞了你的图片");
        // SYSTEM ACCOUNT
        message.setFromId(user.getId());
        message.setCreatedDate(new Date());
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
