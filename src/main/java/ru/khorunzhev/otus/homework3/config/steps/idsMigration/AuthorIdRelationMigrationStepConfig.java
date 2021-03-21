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
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.khorunzhev.otus.homework3.model.mongo.Author;
import ru.khorunzhev.otus.homework3.service.migrateIds.MigrateAuthorIdService;
import ru.khorunzhev.otus.homework3.service.transformEntities.TransformAuthorService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;

@Configuration
public class AuthorIdRelationMigrationStepConfig {

    private static final int CHUNK_SIZE = 5;
    private final Logger logger = LoggerFactory.getLogger("Batch");

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @StepScope
    @Bean
    public ItemProcessor authorIdRelationItemProcessor(MigrateAuthorIdService migrateAuthorIdService) {
        return (ItemProcessor<Author, ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.AuthorMigrationId>) migrateAuthorIdService::migrate;
    }

    @StepScope
    @Bean
    public JpaItemWriter<ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.AuthorMigrationId> authorIdRelationWriter() {
        return new JpaItemWriterBuilder<ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.AuthorMigrationId>()
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .usePersist(true)
                .build();
    }

    @Bean
    public Step migrateAuthorRelationIDStep(JpaItemWriter<ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.AuthorMigrationId> authorIdRelationWriter,
                                  MongoItemReader<Author> authorReader,
                                  ItemProcessor<Author, ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.AuthorMigrationId> authorIdRelationItemProcessor) {
        return stepBuilderFactory.get("migrateAuthorRelationIDStep")
                .<Author, ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.AuthorMigrationId>chunk(CHUNK_SIZE)
                .reader(authorReader)
                .processor(authorIdRelationItemProcessor)
                .writer(authorIdRelationWriter)
                .listener(new ItemReadListener<Author>() {
                    public void beforeRead() {
                        logger.info("Начало чтения");
                    }

                    public void afterRead(Author o) {
                        logger.info("Конец чтения " + o);
                    }

                    public void onReadError(Exception e) {
                        logger.info("Ошибка чтения");
                    }
                })
                .listener(new ItemWriteListener<ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.AuthorMigrationId>() {
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
