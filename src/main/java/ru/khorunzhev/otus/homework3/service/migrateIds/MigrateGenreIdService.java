package ru.khorunzhev.otus.homework3.service.migrateIds;

import org.springframework.stereotype.Service;
import ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.GenreMigrationId;

@Service
public class MigrateGenreIdService {

    public GenreMigrationId migrate(ru.khorunzhev.otus.homework3.model.mongo.Genre mongoGenre) {
            return GenreMigrationId.builder().noSqlId(mongoGenre.getId()).build();
    }
}
