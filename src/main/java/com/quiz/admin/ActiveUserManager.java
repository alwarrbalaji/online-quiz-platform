package com.quiz.admin;

import com.quiz.admin.dto.ActiveUserDto;
import com.quiz.model.User;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@WebListener
public class ActiveUserManager implements HttpSessionListener, HttpSessionAttributeListener {
    // Map sessionId to ActiveUserDto
    private static final Map<String, ActiveUserDto> activeUsers = new ConcurrentHashMap<>();

    public static List<ActiveUserDto> getActiveUsers() {
        return new ArrayList<>(activeUsers.values());
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        String name = event.getName();
        if ("user".equals(name) || "adminUser".equals(name)) {
            Object val = event.getValue();
            if (val instanceof User) {
                User u = (User) val;
                ActiveUserDto dto = new ActiveUserDto();
                dto.setUsername(u.getUsername());
                dto.setEmail(u.getEmail());
                dto.setLoginTime(new Date());
                activeUsers.put(event.getSession().getId(), dto);
            }
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        String name = event.getName();
        if ("user".equals(name) || "adminUser".equals(name)) {
            activeUsers.remove(event.getSession().getId());
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        activeUsers.remove(se.getSession().getId());
    }
    
    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {}
    @Override
    public void sessionCreated(HttpSessionEvent se) {}
}
