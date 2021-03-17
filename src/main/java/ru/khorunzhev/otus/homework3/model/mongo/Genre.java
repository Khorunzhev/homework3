package ru.khorunzhev.otus.homework3.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString(exclude = "id")
@Document(value = "genre")
public class Genre {

    @Id
    private String id;
    private String name;

    public Genre (String name) {
        this.name = name;
    }

}
