package ru.khorunzhev.otus.homework3.config.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


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

    @Bean
    public Job migrateLibraryJob(Step migrateUserStep,
                                 Step migrateAuthorStep,
                                 Step migrateAuthorRelationIDStep,
                                 Step migrateGenreStep,
                                 Step migrateGenreRelationIDStep,
                                 Step migrateBookStep) {
        return jobBuilderFactory.get(IMPORT_USER_JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(migrateUserStep)
                .next(migrateAuthorStep)
                .next(migrateAuthorRelationIDStep)
                .next(migrateGenreStep)
                .next(migrateGenreRelationIDStep)
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
}
