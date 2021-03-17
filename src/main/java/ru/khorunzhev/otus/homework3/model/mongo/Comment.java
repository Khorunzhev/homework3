package ru.khorunzhev.otus.homework3.model.mongo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Document(value = "comment")
public class Comment {

    @Id
    private String id;
    private String text;

    public Comment(String text) {
        this.text = text;
    }

}
