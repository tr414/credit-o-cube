package com.fdmgroup.creditocube.model;


import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a Merchant entity in the database.
 * 
 * Each merchant is identified by a unique code and categorized for business
 * purposes. This class is part of the domain model and is mapped to a database
 * table 'merchants'.
 * 
 * @author Wong Mann Joe
 * @version 1.0
 * @since 2023-04-16
 */
@Entity
@Table(name = "merchants")
public class Merchant {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "merchant_code_id")
	private long id;

	@Column(name = "merchant_code")
	private String merchantCode;

	private String category;
	private boolean isActive;

	/**
	 * Default constructor for JPA.
	 */
	public Merchant() {
		// JPA requires a no-argument constructor.
	}

	/**
	 * Constructs a new Merchant with specified merchant code and category.
	 * 
	 * @param merchantCode The code that uniquely identifies the merchant.
	 * @param category     The category that describes the merchant.
	 */
	public Merchant(String merchantCode, String category) {
		setMerchantCode(merchantCode);
		setCategory(category);
	}

	/**
	 * Gets the ID of the merchant.
	 * 
	 * @return The ID of this merchant.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the ID of the merchant. Typically not used as ID is auto-generated.
	 * 
	 * @param id The new ID for this merchant.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the merchant code of the merchant.
	 * 
	 * @return The merchant code of this merchant.
	 */
	public String getMerchantCode() {
		return merchantCode;
	}

	/**
	 * Sets the merchant code of the merchant.
	 * 
	 * @param merchantCode The new merchant code for this merchant.
	 */
	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	/**
	 * Gets the category of the merchant.
	 * 
	 * @return The category of this merchant.
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets the category of the merchant.
	 * 
	 * This method also ensures the category is stored with the first letter
	 * capitalized.
	 * 
	 * @param category The new category for this merchant.
	 */
	public void setCategory(String category) {
		this.category = category.substring(0, 1).toUpperCase() + category.substring(1);
	}

	/**
	 * Checks if the merchant is active.
	 * 
	 * @return true if the merchant is active, otherwise false.
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * Sets the active status of the merchant.
	 * 
	 * @param isActive The new active status for this merchant.
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
