package com.fdmgroup.creditocube.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Bill {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@OneToOne
	@JoinColumn(name = "fk_card_id")
	private CreditCard card;
	
	private LocalDateTime previousBillIssueTime;
	
	private LocalDateTime billIssueTime;

	private double totalAmountDue;

	private double minimumAmountDue;

	private double outstandingAmount;
	
	private double previousBillOutstandingAmount;

	// private LocalDateTime dueDate;

	private boolean paid;

	public Bill() {

	}

	public Bill(CreditCard card, double totalAmountDue, double minimumAmountDue, boolean paid) {
		this.card = card;
		this.totalAmountDue = totalAmountDue;
		this.minimumAmountDue = minimumAmountDue;
		this.outstandingAmount = totalAmountDue;
		this.paid = paid;
	}

	public double getTotalAmountDue() {
		return totalAmountDue;
	}

	public void setTotalAmountDue(double totalAmountDue) {
		this.totalAmountDue = totalAmountDue;
	}

	public double getMinimumAmountDue() {
		return minimumAmountDue;
	}

	public void setMinimumAmountDue(double minimumAmountDue) {
		this.minimumAmountDue = minimumAmountDue;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CreditCard getCard() {
		return card;
	}

	public void setCard(CreditCard card) {
		this.card = card;
	}

	public double getOutstandingAmount() {
		return outstandingAmount;
	}

	public void setOutstandingAmount(double outstandingAmount) {
		this.outstandingAmount = outstandingAmount;
	}

	public LocalDateTime getBillIssueTime() {
		return billIssueTime;
	}

	public void setBillIssueTime(LocalDateTime billIssueTime) {
		this.billIssueTime = billIssueTime;
	}

	public LocalDateTime getPreviousBillIssueTime() {
		return previousBillIssueTime;
	}

	public void setPreviousBillIssueTime(LocalDateTime previousBillIssueTime) {
		this.previousBillIssueTime = previousBillIssueTime;
	}

	public double getPreviousBillOutstandingAmount() {
		return previousBillOutstandingAmount;
	}

	public void setPreviousBillOutstandingAmount(double previousBillOutstandingAmount) {
		this.previousBillOutstandingAmount = previousBillOutstandingAmount;
	}
	
	public String getFormattedDate(LocalDateTime dateTime) {
		String date = dateTime.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));	//ofLocalizedDateTime( new FormatStyle(dateStyle, timeStyle)));
//				+ transactionDate.format(DateTimeFormatter.ISO_TIME);
		return date;
	}
	
	
}
