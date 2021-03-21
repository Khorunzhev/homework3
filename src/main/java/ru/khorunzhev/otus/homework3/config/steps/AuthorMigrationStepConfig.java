package ru.khorunzhev.otus.homework3.config.steps;

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
import ru.khorunzhev.otus.homework3.service.transformEntities.TransformAuthorService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;

@Configuration
public class AuthorMigrationStepConfig {

    private static final int CHUNK_SIZE = 5;
    private final Logger logger = LoggerFactory.getLogger("Batch");

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @StepScope
    @Bean
    public MongoItemReader<Author> authorReader(MongoTemplate mongoTemplate) {
        return new MongoItemReaderBuilder<Author>()
                .name("MongoReader")
                .template(mongoTemplate)
                .targetType(ru.khorunzhev.otus.homework3.model.mongo.Author.class)
                .jsonQuery("{}")
                .sorts(new HashMap<>())
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor authorItemProcessor(TransformAuthorService transformAuthorService) {
        return (ItemProcessor<ru.khorunzhev.otus.homework3.model.mongo.Author, ru.khorunzhev.otus.homework3.model.jpa.Author>) transformAuthorService::transformAuthor;
    }

    @StepScope
    @Bean
    public JpaItemWriter<ru.khorunzhev.otus.homework3.model.jpa.Author> authorWriter() {
        return new JpaItemWriterBuilder<ru.khorunzhev.otus.homework3.model.jpa.Author>()
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .usePersist(true)
                .build();
    }

    @Bean
    public Step migrateAuthorStep(JpaItemWriter<ru.khorunzhev.otus.homework3.model.jpa.Author> authorWriter,
                                  MongoItemReader<ru.khorunzhev.otus.homework3.model.mongo.Author> authorReader,
                                  ItemProcessor<ru.khorunzhev.otus.homework3.model.mongo.Author, ru.khorunzhev.otus.homework3.model.jpa.Author> authorItemProcessor) {
        return stepBuilderFactory.get("migrateAuthorStep")
                .<ru.khorunzhev.otus.homework3.model.mongo.Author, ru.khorunzhev.otus.homework3.model.jpa.Author>chunk(CHUNK_SIZE)
                .reader(authorReader)
                .processor(authorItemProcessor)
                .writer(authorWriter)
                .listener(new ItemReadListener<Author>() {
                    public void beforeRead() {
                        logger.info("Начало чтения");
                    }
                    public void afterRead(ru.khorunzhev.otus.homework3.model.mongo.Author o) {
                        logger.info("Конец чтения " + o);
                    }

                    public void onReadError(Exception e) {
                        logger.info("Ошибка чтения");
                    }
                })
                .listener(new ItemWriteListener<ru.khorunzhev.otus.homework3.model.jpa.Author>() {
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
