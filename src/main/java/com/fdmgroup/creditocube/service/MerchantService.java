package com.fdmgroup.creditocube.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fdmgroup.creditocube.model.Merchant;
import com.fdmgroup.creditocube.repository.MerchantRepository;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Service class for managing merchant data.
 *
 * This service is responsible for loading and managing merchant data within the
 * application. It reads merchant details from a CSV file and populates the
 * database at application startup. Handles CSV file parsing and persists each
 * merchant record in the database.
 *
 * @author Wong Mann Joe
 * @version 1.0
 * @since 2023-04-16
 */
@Service
public class MerchantService {

	private static final Logger logger = LogManager.getLogger(MerchantService.class);

	@Autowired
	private MerchantRepository merchantRepository;

	@Autowired
	private ResourceLoader resourceLoader;

	/**
	 * Initializes the merchant database with data from a CSV file. This method is
	 * automatically invoked after the bean's properties have been set, ensuring
	 * that the database is populated at application startup.
	 */
	@PostConstruct
	public void loadDataOnStartup() {
		logger.info("Starting database initialization from CSV file");
		importCSV("classpath:data/merchant-codes.csv");
	}

	/**
	 * Imports merchant data from a CSV file specified by the resource path.
	 * 
	 * Opens a CSV file, reads its contents, and either updates existing merchants
	 * or adds new ones. Each record in the CSV file is expected to contain a
	 * merchant code, category, and active status.
	 *
	 * @param resourcePath A String representing the path to the CSV file in the
	 *                     classpath.
	 * @throws Exception if there are issues opening or reading the file or parsing
	 *                   the CSV.
	 */
	public void importCSV(String resourcePath) {
		Resource resource = resourceLoader.getResource(resourcePath);
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
			CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();
			try (CSVParser csvParser = new CSVParser(reader, csvFormat)) {
				for (CSVRecord record : csvParser) {
					String merchantCode = record.get("Merchant Code");
					Optional<Merchant> existingMerchant = merchantRepository.findByMerchantCode(merchantCode);

					Merchant merchant = existingMerchant.orElse(new Merchant());
					merchant.setMerchantCode(merchantCode);
					merchant.setCategory(record.get("Category"));
					merchant.setActive(Boolean.parseBoolean(record.get("Active")));
					merchantRepository.save(merchant);

					logger.debug("Saved or updated merchant with code: {}", merchantCode);
				}
			}
			logger.info("Successfully loaded merchant data from CSV.");
		} catch (Exception e) {
			logger.error("Failed to load merchant data from CSV", e);
		}
	}
}