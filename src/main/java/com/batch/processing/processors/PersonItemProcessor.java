package com.batch.processing.processors;

import com.batch.processing.entities.Person;
import com.batch.processing.entities.PersonDb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import java.sql.Timestamp;
import java.time.Instant;

public class PersonItemProcessor implements ItemProcessor<Person, PersonDb> {

	private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

	@Override
	public PersonDb process(final Person person) {
		final String firstName = person.getFirstName().toUpperCase();
		final String lastName = person.getLastName().toUpperCase();

		final Timestamp lastModified = Timestamp.from(Instant.now());
		final PersonDb transformedPerson = new PersonDb(firstName, lastName, lastModified);

		log.info("Converting (" + person + ") into (" + transformedPerson + ")");

		return transformedPerson;
	}

}
