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
import ru.khorunzhev.otus.homework3.model.mongo.Genre;
import ru.khorunzhev.otus.homework3.service.transformEntities.TransformGenreService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;

@Configuration
public class GenreMigrationStepConfig {
    
    private static final int CHUNK_SIZE = 5;
    private final Logger logger = LoggerFactory.getLogger("Batch");

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @StepScope
    @Bean
    public MongoItemReader<Genre> genreReader(MongoTemplate mongoTemplate) {
        return new MongoItemReaderBuilder<Genre>()
                .name("MongoReader")
                .template(mongoTemplate)
                .targetType(ru.khorunzhev.otus.homework3.model.mongo.Genre.class)
                .jsonQuery("{}")
                .sorts(new HashMap<>())
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor genreItemProcessor(TransformGenreService transformGenreService) {
        return (ItemProcessor<ru.khorunzhev.otus.homework3.model.mongo.Genre, ru.khorunzhev.otus.homework3.model.jpa.Genre>) transformGenreService::transformGenre;
    }

    @StepScope
    @Bean
    public JpaItemWriter<ru.khorunzhev.otus.homework3.model.jpa.Genre> genreWriter() {
        return new JpaItemWriterBuilder<ru.khorunzhev.otus.homework3.model.jpa.Genre>()
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .usePersist(true)
                .build();
    }

    @Bean
    public Step migrateGenreStep(JpaItemWriter<ru.khorunzhev.otus.homework3.model.jpa.Genre> genreWriter,
                                  MongoItemReader<ru.khorunzhev.otus.homework3.model.mongo.Genre> genreReader,
                                  ItemProcessor<ru.khorunzhev.otus.homework3.model.mongo.Genre, ru.khorunzhev.otus.homework3.model.jpa.Genre> genreItemProcessor) {
        return stepBuilderFactory.get("migrateGenreStep")
                .<ru.khorunzhev.otus.homework3.model.mongo.Genre, ru.khorunzhev.otus.homework3.model.jpa.Genre>chunk(CHUNK_SIZE)
                .reader(genreReader)
                .processor(genreItemProcessor)
                .writer(genreWriter)
                .listener(new ItemReadListener<Genre>() {
                    public void beforeRead() {
                        logger.info("Начало чтения");
                    }
                    public void afterRead(ru.khorunzhev.otus.homework3.model.mongo.Genre o) {
                        logger.info("Конец чтения " + o);
                    }

                    public void onReadError(Exception e) {
                        logger.info("Ошибка чтения");
                    }
                })
                .listener(new ItemWriteListener<ru.khorunzhev.otus.homework3.model.jpa.Genre>() {
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
