package com.fdmgroup.creditocube.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Merchant {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long merchantId;
}
