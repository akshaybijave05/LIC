package com.license.model;



import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ch.qos.logback.core.subst.Token.Type;

@Entity
@ToString
@Data
public class License {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String email;

	@Column(unique = true) // Ensure uniqueness in the database
	private String macId;

	private String licenseKey;

	private int duration;

	private Date expirationDate;

	private String type;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timeStamp;

	@PrePersist
	public void prePersist() {
		// Generate a unique license key using UUID
		this.licenseKey = UUID.randomUUID().toString().toUpperCase();

		// Set the current timestamp
		this.timeStamp = new Date();

	}

}

