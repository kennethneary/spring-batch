package com.batch.processing.config;

import javax.sql.DataSource;

import com.batch.processing.entities.PersonDb;
import com.batch.processing.listeners.JobCompletionNotificationListener;
import com.batch.processing.processors.PersonItemProcessor;
import com.batch.processing.entities.Person;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	public static final String SAMPLE_DATA_CSV_INPUT = "sample-data.csv";
	public static final String PERSON_ITEM_READER = "personItemReader";
	public static final String IMPORT_USER_JOB = "importUserJob";
	public static final String STEP_1 = "step1";

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	public FlatFileItemReader<Person> reader() {
		return new FlatFileItemReaderBuilder<Person>()
			.name(PERSON_ITEM_READER)
			.resource(new ClassPathResource(SAMPLE_DATA_CSV_INPUT))
			.delimited()
			.names(new String[]{"firstName", "lastName"})
			.fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
				setTargetType(Person.class);
			}})
			.build();
	}

	@Bean
	public PersonItemProcessor processor() {
		return new PersonItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<PersonDb> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<PersonDb>()
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql("INSERT INTO people (first_name, last_name, last_modified) VALUES (:firstName, :lastName, :lastModified)")
			.dataSource(dataSource)
			.build();
	}

	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get(IMPORT_USER_JOB)
			.incrementer(new RunIdIncrementer())
			.listener(listener)
			.flow(step1)
			.end()
			.build();
	}

	@Bean
	public Step step1(FlatFileItemReader<Person> reader, PersonItemProcessor processor, JdbcBatchItemWriter<PersonDb> writer) {
		return stepBuilderFactory.get(STEP_1)
			.<Person, PersonDb>chunk(10)
			.reader(reader)
			.processor(processor)
			.writer(writer)
			.build();
	}
}
