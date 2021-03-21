package ru.khorunzhev.otus.homework3.service.migrateIds;

import org.springframework.stereotype.Service;
import ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.AuthorMigrationId;


@Service
public class MigrateAuthorIdService {

    public AuthorMigrationId migrate(ru.khorunzhev.otus.homework3.model.mongo.Author mongoAuthor) {
            return AuthorMigrationId.builder().noSqlId(mongoAuthor.getId()).build();
    }
}
