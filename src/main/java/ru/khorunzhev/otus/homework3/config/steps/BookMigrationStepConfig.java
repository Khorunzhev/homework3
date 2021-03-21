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
import ru.khorunzhev.otus.homework3.model.mongo.Book;
import ru.khorunzhev.otus.homework3.service.transformEntities.TransformBookService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;

@Configuration
public class BookMigrationStepConfig {

    private static final int CHUNK_SIZE = 5;
    private final Logger logger = LoggerFactory.getLogger("Batch");

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @StepScope
    @Bean
    public MongoItemReader<Book> bookReader(MongoTemplate mongoTemplate) {
        return new MongoItemReaderBuilder<Book>()
                .name("MongoReader")
                .template(mongoTemplate)
                .targetType(ru.khorunzhev.otus.homework3.model.mongo.Book.class)
                .jsonQuery("{}")
                .sorts(new HashMap<>())
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor bookItemProcessor(TransformBookService transformBookService) {
        return (ItemProcessor<ru.khorunzhev.otus.homework3.model.mongo.Book, ru.khorunzhev.otus.homework3.model.jpa.Book>) transformBookService::transformBook;
    }

    @StepScope
    @Bean
    public JpaItemWriter<ru.khorunzhev.otus.homework3.model.jpa.Book> bookWriter() {
        return new JpaItemWriterBuilder<ru.khorunzhev.otus.homework3.model.jpa.Book>()
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .usePersist(true)
                .build();
    }

    @Bean
    public Step migrateBookStep(JpaItemWriter<ru.khorunzhev.otus.homework3.model.jpa.Book> bookWriter,
                                MongoItemReader<ru.khorunzhev.otus.homework3.model.mongo.Book> bookReader,
                                ItemProcessor<ru.khorunzhev.otus.homework3.model.mongo.Book, ru.khorunzhev.otus.homework3.model.jpa.Book> bookItemProcessor) {
        return stepBuilderFactory.get("migrateBookStep")
                .<ru.khorunzhev.otus.homework3.model.mongo.Book, ru.khorunzhev.otus.homework3.model.jpa.Book>chunk(CHUNK_SIZE)
                .reader(bookReader)
                .processor(bookItemProcessor)
                .writer(bookWriter)
                .listener(new ItemReadListener<Book>() {
                    public void beforeRead() {
                        logger.info("Начало чтения");
                    }
                    public void afterRead(ru.khorunzhev.otus.homework3.model.mongo.Book o) {
                        logger.info("Конец чтения " + o);
                    }

                    public void onReadError(Exception e) {
                        logger.info("Ошибка чтения");
                    }
                })
                .listener(new ItemWriteListener<ru.khorunzhev.otus.homework3.model.jpa.Book>() {
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
