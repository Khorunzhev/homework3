package ru.khorunzhev.otus.homework3.mongock;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.khorunzhev.otus.homework3.model.mongo.*;


@ChangeLog(order = "001")
public class DataBaseChangeLog {

    @ChangeSet(order = "001", id = "dropDb", author = "khorunzhev", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertLibrary", author = "khorunzhev", runAlways = true)
    public void insertLibrary(MongockTemplate template) {
        Author authorIvanov = new Author("Иванов");
        template.save(authorIvanov);

        Author authorSidorov = new Author("Сидоров");
        template.save(authorSidorov);

        Genre genreFantastic = new Genre("Фантастика");
        template.save(genreFantastic);

        Genre genreDetective = new Genre("Детектив");
        template.save(genreDetective);

        Book book1 = new Book("Name1", authorIvanov, genreDetective);
        template.save(book1);

        Book book2 = new Book("Name2", authorIvanov, genreDetective);
        template.save(book2);

        User user = new User("IvanLogin", "$2a$10$R7NIY9xaunw5K6IisHZxaOFqCCJAo4d1U7nKTEDEC.4z4MVZqGyO.");
        template.save(user);

    }

}
