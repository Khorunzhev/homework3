package ru.khorunzhev.otus.homework3.service.transformEntities;

import org.springframework.stereotype.Service;
import ru.khorunzhev.otus.homework3.model.jpa.User;

@Service
public class TransformUserService {

    public User transformUser(ru.khorunzhev.otus.homework3.model.mongo.User mongoUser) {
        return User.builder()
                .username(mongoUser.getUsername())
                .password(mongoUser.getPassword())
                .build();
    }
}
