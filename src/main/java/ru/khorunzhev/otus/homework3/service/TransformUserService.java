package ru.khorunzhev.otus.homework3.service;

import org.springframework.stereotype.Service;
import ru.khorunzhev.otus.homework3.model.jpa.User;

@Service
public class TransformUserService {

    public User transformUser(ru.khorunzhev.otus.homework3.model.mongo.User mongoUser){
        User user = new User.UserBuilder()
                .username(mongoUser.getUsername())
                .password(mongoUser.getPassword())
                .build();
        return person;
    }
}
