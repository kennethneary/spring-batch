package com.batch.processing.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
	private String lastName;
	private String firstName;
	private Instant lastModified;
}
