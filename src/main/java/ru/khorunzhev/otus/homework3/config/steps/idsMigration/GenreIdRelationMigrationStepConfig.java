package ru.khorunzhev.otus.homework3.config.steps.idsMigration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.khorunzhev.otus.homework3.model.mongo.Genre;
import ru.khorunzhev.otus.homework3.service.migrateIds.MigrateGenreIdService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Configuration
public class GenreIdRelationMigrationStepConfig {

    private static final int CHUNK_SIZE = 5;
    private final Logger logger = LoggerFactory.getLogger("Batch");

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @StepScope
    @Bean
    public ItemProcessor genreIdRelationItemProcessor(MigrateGenreIdService migrateGenreIdService) {
        return (ItemProcessor<Genre, ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.GenreMigrationId>) migrateGenreIdService::migrate;
    }

    @StepScope
    @Bean
    public JpaItemWriter<ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.GenreMigrationId> genreIdRelationWriter() {
        return new JpaItemWriterBuilder<ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.GenreMigrationId>()
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .usePersist(true)
                .build();
    }

    @Bean
    public Step migrateGenreRelationIDStep(JpaItemWriter<ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.GenreMigrationId> genreIdRelationWriter,
                                  MongoItemReader<Genre> genreReader,
                                  ItemProcessor<Genre, ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.GenreMigrationId> genreIdRelationItemProcessor) {
        return stepBuilderFactory.get("migrateGenreRelationIDStep")
                .<Genre, ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.GenreMigrationId>chunk(CHUNK_SIZE)
                .reader(genreReader)
                .processor(genreIdRelationItemProcessor)
                .writer(genreIdRelationWriter)
                .listener(new ItemReadListener<Genre>() {
                    public void beforeRead() {
                        logger.info("Начало чтения");
                    }
                    public void afterRead(Genre o) {
                        logger.info("Конец чтения " + o);
                    }

                    public void onReadError(Exception e) {
                        logger.info("Ошибка чтения");
                    }
                })
                .listener(new ItemWriteListener<ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.GenreMigrationId>() {
                    public void beforeWrite(List list) {
                        logger.info("Начало записи");
                    }

                    public void afterWrite(List list) {
                        logger.info("Конец записи");
                    }

                    public void onWriteError(Exception e, List list) {
                        logger.info("Ошибка записи");
                    }
                })
                .build();
    }
}
