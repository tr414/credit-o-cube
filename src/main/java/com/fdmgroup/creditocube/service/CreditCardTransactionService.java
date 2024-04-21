package com.fdmgroup.creditocube.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.fdmgroup.creditocube.model.CreditCard;
import com.fdmgroup.creditocube.model.CreditCardTransaction;
import com.fdmgroup.creditocube.model.Merchant;
import com.fdmgroup.creditocube.repository.CreditCardTransactionRepository;

import jakarta.annotation.PostConstruct;

@Service
public class CreditCardTransactionService {
	
	private static final Logger logger = LogManager.getLogger(CreditCardTransactionService.class);
	
	@Autowired
	private CreditCardTransactionRepository repo;
	
//	@Autowired
//	private ResourceLoader resourceLoader;
//	
//	
//	/**
//	 * Initializes the merchant database with data from a CSV file. This method is
//	 * automatically invoked after the bean's properties have been set, ensuring
//	 * that the database is populated at application startup.
//	 */
//	@PostConstruct
//	public void loadDataOnStartup() {
//		logger.info("Starting database initialization from CSV file");
//		importCSV("classpath:data/card-transactions.csv");
//	}
//
//	/**
//	 * Imports merchant data from a CSV file specified by the resource path.
//	 * 
//	 * Opens a CSV file, reads its contents, and either updates existing merchants
//	 * or adds new ones. Each record in the CSV file is expected to contain a
//	 * merchant code, category, and active status.
//	 *
//	 * @param resourcePath A String representing the path to the CSV file in the
//	 *                     classpath.
//	 * @throws Exception if there are issues opening or reading the file or parsing
//	 *                   the CSV.
//	 */
//	public void importCSV(String resourcePath) {
//		Resource resource = resourceLoader.getResource(resourcePath);
//		try (BufferedReader reader = new BufferedReader(
//				new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
//			CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();
//			try (CSVParser csvParser = new CSVParser(reader, csvFormat)) {
//				for (CSVRecord record : csvParser) {
//					BigDecimal transactionId = new BigDecimal(record.get("Transaction Id"));
//					Optional<CreditCardTransaction> existingTransaction = repo.findById(transactionId.longValue());
//
//					CreditCardTransaction transaction = existingTransaction.orElse(new CreditCardTransaction());
//					transaction.setTransactionCard(null);
//					transaction.setMerchantCode(null);
//					transaction.setTransactionAmount(0);
//					transaction.setTransactionDate(null);
//					//transaction.setActive(Boolean.parseBoolean(record.get("Active")));
//					repo.save(transaction);
//
//					logger.debug("Saved or updated transaction with id {}", transactionId);
//				}
//			}
//			logger.info("Successfully loaded transaction data from CSV.");
//		} catch (Exception e) {
//			logger.error("Failed to load transaction data from CSV", e);
//		}
//	}
	
	
	public Optional<CreditCardTransaction> createCreditCardTransaction(CreditCardTransaction transaction) {
		Optional<CreditCardTransaction> createdTransaction;
		
		try {
			createdTransaction = Optional.ofNullable(repo.save(transaction));
		} catch(Exception e) {
			//LOGGER.error("Unable to create new task in Task Service.", e);
			return Optional.ofNullable(null);
		}
		
		//LOGGER.info("Created new task with id: {}", createdTask.get().getId());
		return createdTransaction;
	}
	
	public List<CreditCardTransaction> findAllCreditCardTransactions(CreditCard card) {
		return repo.findByTransactionCardIs(card);
	}
	
	public Optional<CreditCardTransaction> updateCreditCardTransaction(CreditCardTransaction transaction) {
		Optional<CreditCardTransaction> updatedTransaction;
		
		try {
			updatedTransaction = Optional.ofNullable(repo.save(transaction));
		} catch(Exception e) {
			//LOGGER.error("Unable to create new task in Task Service.", e);
			return Optional.ofNullable(null);
		}
		
		//LOGGER.info("Created new task with id: {}", createdTask.get().getId());
		return updatedTransaction;
	}
	
	// In case the bill is not paid on time, this function will record the charging of a late payment fees for the credit card
	public void createLatePaymentTransaction(CreditCard card, double latePaymentFees) {
		CreditCardTransaction latePaymentTransaction = new CreditCardTransaction(card, LocalDateTime.now(), latePaymentFees, "Late bill payment fees");
		createCreditCardTransaction(latePaymentTransaction);
	}

}
