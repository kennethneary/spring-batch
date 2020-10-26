package com.batch.processing.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDb {
	private String lastName;
	private String firstName;
	private Timestamp lastModified;
}
