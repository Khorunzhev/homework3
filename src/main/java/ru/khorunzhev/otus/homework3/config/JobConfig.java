package ru.khorunzhev.otus.homework3.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import ru.khorunzhev.otus.homework3.model.jpa.Book;
import ru.khorunzhev.otus.homework3.model.jpa.User;
import ru.khorunzhev.otus.homework3.service.TransformBookService;
import ru.khorunzhev.otus.homework3.service.TransformUserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("all")
@Configuration
public class JobConfig {
    private static final int CHUNK_SIZE = 5;
    private final Logger logger = LoggerFactory.getLogger("Batch");

    public static final String IMPORT_USER_JOB_NAME = "migrateLibraryJob";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @StepScope
    @Bean
    public MongoItemReader<ru.khorunzhev.otus.homework3.model.mongo.User> reader(MongoTemplate mongoTemplate) {
        return new MongoItemReaderBuilder<ru.khorunzhev.otus.homework3.model.mongo.User>()
                .name("MongoReader")
                .template(mongoTemplate)
                .targetType(ru.khorunzhev.otus.homework3.model.mongo.User.class)
                .jsonQuery("{}")
                .sorts(new HashMap<>())
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor itemUserProcessor(TransformUserService transformUserService) {
        return (ItemProcessor<ru.khorunzhev.otus.homework3.model.mongo.User, User>) transformUserService::transformUser;
    }

    @StepScope
    @Bean
    public JpaItemWriter<User> writer() {
        return new JpaItemWriterBuilder<User>()
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .usePersist(true)
                .build();
    }

    @Bean
    public Job importUserJob(Step migrateUserStep, Step migrateBookStep) {
        return jobBuilderFactory.get(IMPORT_USER_JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(migrateUserStep)
                .next(migrateBookStep)
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        logger.info("Начало job");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        logger.info("Конец job");
                    }
                })
                .build();
    }

    @Bean
    public Step migrateUserStep(JpaItemWriter<User> userWriter,
                                MongoItemReader<ru.khorunzhev.otus.homework3.model.mongo.User> userReader,
                                ItemProcessor<ru.khorunzhev.otus.homework3.model.mongo.User, User> itemUserProcessor) {
        return stepBuilderFactory.get("migrateUserStep")
                .<ru.khorunzhev.otus.homework3.model.mongo.User, User>chunk(CHUNK_SIZE)
                .reader(userReader)
                .processor(itemUserProcessor)
                .writer(userWriter)
                .listener(new ItemReadListener<ru.khorunzhev.otus.homework3.model.mongo.User>() {
                    public void beforeRead() {
                        logger.info("Начало чтения");
                    }
                    public void afterRead(ru.khorunzhev.otus.homework3.model.mongo.User o) {
                        logger.info("Конец чтения " + o);
                    }

                    public void onReadError(Exception e) {
                        logger.info("Ошибка чтения");
                    }
                })
                .listener(new ItemWriteListener<User>() {
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


    @StepScope
    @Bean
    public MongoItemReader<ru.khorunzhev.otus.homework3.model.mongo.Book> bookReader(MongoTemplate mongoTemplate) {
        return new MongoItemReaderBuilder<ru.khorunzhev.otus.homework3.model.mongo.Book>()
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
        return (ItemProcessor<ru.khorunzhev.otus.homework3.model.mongo.Book, Book>) transformBookService::transformBook;
    }

    @StepScope
    @Bean
    public JpaItemWriter<Book> bookWriter() {
        return new JpaItemWriterBuilder<Book>()
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .usePersist(true)
                .build();
    }

    @Bean
    public Step migrateBookStep(JpaItemWriter<Book> bookWriter,
                                MongoItemReader<ru.khorunzhev.otus.homework3.model.mongo.Book> bookReader,
                                ItemProcessor<ru.khorunzhev.otus.homework3.model.mongo.Book, Book> bookItemProcessor) {
        return stepBuilderFactory.get("migrateBookStep")
                .<ru.khorunzhev.otus.homework3.model.mongo.Book, Book>chunk(CHUNK_SIZE)
                .reader(bookReader)
                .processor(bookItemProcessor)
                .writer(bookWriter)
                .listener(new ItemReadListener<ru.khorunzhev.otus.homework3.model.mongo.Book>() {
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
                .listener(new ItemWriteListener<Book>() {
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
