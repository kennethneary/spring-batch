package com.batch.processing.listeners;

import com.batch.processing.entities.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

			final List<Person> query = jdbcTemplate.query("SELECT first_name, last_name, last_modified FROM people",
					(rs, row) -> new Person(
							rs.getString(1),
							rs.getString(2),
							rs.getTimestamp(3).toInstant())
			);

			query.forEach(person -> log.info("Found <" + person + "> in the database."));
		}
	}
}
