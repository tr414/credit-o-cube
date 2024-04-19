package com.fdmgroup.creditocube.model;

import java.math.BigDecimal;
import java.util.Map;

public class CurrencyExchange {
	//success
	private String success;
	//timestamp
	private String timestamp;
	//base
	private String base;
	//date
	private String date;
	//rates
	private Map<String, Double> rates;
	
	public CurrencyExchange() {
		// TODO Auto-generated constructor stub
	}

	public CurrencyExchange(String success, String timestamp, String base, String date, Map<String, Double> rates) {
		super();
		this.success = success;
		this.timestamp = timestamp;
		this.base = base;
		this.date = date;
		this.rates = rates;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Map<String, Double> getRates() {
		return rates;
	}

	public void setRates(Map<String, Double> rates) {
		this.rates = rates;
	}
	
	public BigDecimal convertToEuro(BigDecimal amount, String currency) {
		BigDecimal exchangeRate = new BigDecimal(1 / rates.get(currency));
		BigDecimal euroAmount = amount.multiply(exchangeRate);
		System.out.println(rates.get(currency));
		System.out.println(exchangeRate);
		System.out.println(euroAmount);
		return euroAmount;
	}
	
	public BigDecimal convertEuroToSGD(BigDecimal amount) {
		BigDecimal exchangeRate = new BigDecimal(rates.get("SGD"));
		BigDecimal sgdAmount = amount.multiply(exchangeRate);
		System.out.println(rates.get("SGD"));
		System.out.println(exchangeRate);
		System.out.println(sgdAmount);
		return sgdAmount;
	}
	
	public BigDecimal exchangeRateToSGD(String currency) {
		BigDecimal convertBaseToEuroRate = new BigDecimal(1 / rates.get(currency));
		BigDecimal convertEuroToSGDRate = new BigDecimal(rates.get("SGD"));
		BigDecimal combinedExchangeRate = convertBaseToEuroRate.multiply(convertEuroToSGDRate);
	
		return combinedExchangeRate;
	}

}
