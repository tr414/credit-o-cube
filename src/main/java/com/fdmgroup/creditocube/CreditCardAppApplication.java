package com.fdmgroup.creditocube;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import com.fdmgroup.creditocube.model.CardType;
//import com.fdmgroup.creditocube.model.Rewards;
//import com.fdmgroup.creditocube.repository.CardTypeRepository;
//import com.fdmgroup.creditocube.repository.CustomerRepository;
//import com.fdmgroup.creditocube.repository.RewardsRepository;
//import com.fdmgroup.creditocube.service.CardTypeService;
//import com.fdmgroup.creditocube.service.RewardsService;

@SpringBootApplication
public class CreditCardAppApplication {
	// add this if you want to test paying credit card bills
// implements CommandLineRunner

//	@Autowired
//	private RewardsRepository rewardsRepo;
//
//	@Autowired
//	private CardTypeRepository cardTypeRepo;
//
//	@Autowired
//	private CustomerRepository customerRepo;
//
//	@Autowired
//	private CardTypeService cardTypeService;
//
//	@Autowired
//	private RewardsService rewardsService;

	public static void main(String[] args) {
		SpringApplication.run(CreditCardAppApplication.class, args);

	}

//	public void run(String... args) throws Exception {
//		createPreExistingData();
//	}
//
//	private void createPreExistingData() {
//		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
//		if (cardTypeService.isCardTypeTableEmpty() && rewardsService.isRewardsTableEmpty()) {
//			Rewards diningReward = context.getBean("reward_10percent", Rewards.class);
//			System.out.println(diningReward.getCategory());
//			CardType diningCard = context.getBean("diningCard", CardType.class);
//			cardTypeRepo.save(diningCard);
//			System.out.println("Dining card saved");
//			rewardsRepo.save(diningReward);
//			System.out.println("rewards saved");
//		}
//
//	}

}
