package com.invox.invoice_system.config; // Hoặc package phù hợp

import com.invox.invoice_system.entity.*;
import com.invox.invoice_system.enums.*;
import com.invox.invoice_system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor // Sử dụng Lombok để tự động inject qua constructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRankRepository memberRankRepository;
    private final EmployeeRepository employeeRepository;
    private final AppUserRepository appUserRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;
    private final PointTransactionRepository pointTransactionRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional // Bao bọc toàn bộ trong một transaction
    public void run(String... args) throws Exception {
        System.out.println("DataInitializer: Starting data initialization...");

        // Chỉ chạy nếu chưa có dữ liệu (ví dụ: kiểm tra bảng Role)
        if (roleRepository.count() == 0) {
            System.out.println("DataInitializer: No existing data found. Proceeding with initialization.");
            createRoles();
            createCategories();
            createMemberRanks();
            createEmployees();
            createAppUsers();
            createCustomers();
            createProducts();
            createSampleInvoicesAndDetails();
            System.out.println("DataInitializer: Data initialization completed.");
        } else {
            System.out.println("DataInitializer: Data already exists. Skipping initialization.");
        }
    }

    private void createRoles() {
        Role employeeRole = new Role();
        employeeRole.setName("ROLE_EMPLOYEE");
        roleRepository.save(employeeRole);

        Role accountantRole = new Role();
        accountantRole.setName("ROLE_ACCOUNTANT");
        roleRepository.save(accountantRole);

        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        roleRepository.save(adminRole);
    }

    private void createCategories() {
        Category cat1 = new Category(); cat1.setName("Beverages"); cat1.setDescription("Soft drinks, milk, tea, coffee"); categoryRepository.save(cat1);
        Category cat2 = new Category(); cat2.setName("Dry Foods"); cat2.setDescription("Instant noodles, canned goods, spices, rice, cereals"); categoryRepository.save(cat2);
        Category cat3 = new Category(); cat3.setName("Snacks"); cat3.setDescription("Candies, crisps, nuts"); categoryRepository.save(cat3);
        Category cat4 = new Category(); cat4.setName("Personal Care"); cat4.setDescription("Shampoo, shower gel, toothpaste"); categoryRepository.save(cat4);
        Category cat5 = new Category(); cat5.setName("Household Cleaning"); cat5.setDescription("Dishwashing liquid, detergent, floor cleaner"); categoryRepository.save(cat5);
        Category cat6 = new Category(); cat6.setName("Electronics"); cat6.setDescription("Electronic devices"); categoryRepository.save(cat6);
        Category cat7 = new Category(); cat7.setName("Clothing"); cat7.setDescription("Fashion apparel"); categoryRepository.save(cat7);
    }

    private void createMemberRanks() {
        MemberRank bronze = new MemberRank(); bronze.setName("BRONZE"); bronze.setMinTotalPoints(0L); bronze.setPointsEarningRate(BigDecimal.valueOf(0.01)); bronze.setDescription("Bronze Tier - 1% earning rate"); memberRankRepository.save(bronze);
        MemberRank silver = new MemberRank(); silver.setName("SILVER"); silver.setMinTotalPoints(500000L); silver.setPointsEarningRate(BigDecimal.valueOf(0.02)); silver.setDescription("Silver Tier - 2% earning rate"); memberRankRepository.save(silver);
        MemberRank gold = new MemberRank(); gold.setName("GOLD"); gold.setMinTotalPoints(2000000L); gold.setPointsEarningRate(BigDecimal.valueOf(0.03)); gold.setDescription("Gold Tier - 3% earning rate"); memberRankRepository.save(gold);
        MemberRank diamond = new MemberRank(); diamond.setName("DIAMOND"); diamond.setMinTotalPoints(5000000L); diamond.setPointsEarningRate(BigDecimal.valueOf(0.05)); diamond.setDescription("Diamond Tier - 5% earning rate"); memberRankRepository.save(diamond);
    }

    private void createEmployees() {
        Employee emp1 = new Employee(); 
        emp1.setName("Alice Smith"); 
        emp1.setPhone("0901234567"); 
        emp1.setEmail("alice@invox.com"); 
        emp1.setAddress("123 Global St, Biz City"); 
        emp1.setPosition("Sales Employee");
        emp1.setHireDate(LocalDate.of(2020, 1, 1)); 
        emp1.setStatus(EmployeeStatus.ACTIVE); 
        employeeRepository.save(emp1);

        Employee emp2 = new Employee(); 
        emp2.setName("Bob Johnson"); 
        emp2.setPhone("0907654321"); 
        emp2.setEmail("bob@invox.com"); 
        emp2.setAddress("456 Main Ave, Finance Town"); 
        emp2.setPosition("Accountant"); 
        emp2.setHireDate(LocalDate.of(2019, 5, 15)); 
        emp2.setStatus(EmployeeStatus.ACTIVE); 
        employeeRepository.save(emp2);

        Employee emp3 = new Employee(); 
        emp3.setName("Charlie Brown"); 
        emp3.setPhone("0901112223"); 
        emp3.setEmail("charlie@invox.com"); 
        emp3.setAddress("789 Tech Rd, Admin Central"); 
        emp3.setPosition("Admin"); 
        emp3.setHireDate(LocalDate.of(2018, 10, 1)); 
        emp3.setStatus(EmployeeStatus.ACTIVE); employeeRepository.save(emp3);
    }

    private void createAppUsers() {
        Role roleEmployee = roleRepository.findByName("ROLE_EMPLOYEE").orElseThrow();
        Role roleAccountant = roleRepository.findByName("ROLE_ACCOUNTANT").orElseThrow();
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseThrow();

        Employee empAlice = employeeRepository.findByEmail("alice@invox.com").orElseThrow();
        Employee empBob = employeeRepository.findByEmail("bob@invox.com").orElseThrow();
        Employee empCharlie = employeeRepository.findByEmail("charlie@invox.com").orElseThrow();

        AppUser userAlice = new AppUser(); userAlice.setUsername("alice"); userAlice.setPassword(passwordEncoder.encode("password")); userAlice.setEmployee(empAlice); userAlice.setRole(roleEmployee); appUserRepository.save(userAlice);
        AppUser userBob = new AppUser(); userBob.setUsername("bob"); userBob.setPassword(passwordEncoder.encode("password")); userBob.setEmployee(empBob); userBob.setRole(roleAccountant); appUserRepository.save(userBob);
        AppUser userCharlie = new AppUser(); userCharlie.setUsername("charlie"); userCharlie.setPassword(passwordEncoder.encode("password")); userCharlie.setEmployee(empCharlie); userCharlie.setRole(roleAdmin); appUserRepository.save(userCharlie);
    }

    private void createCustomers() {
        MemberRank bronzeRank = memberRankRepository.findByName("BRONZE").orElseThrow();

        Customer cust1 = new Customer(); cust1.setName("David Lee"); cust1.setPhone("0912345001"); cust1.setEmail("david.lee@example.com"); cust1.setAddress("123 Oak St, Anytown"); cust1.setBirthDate(LocalDate.of(1990,3,10)); cust1.setGender(Gender.MALE); cust1.setTotalPoints(0L); cust1.setAvailablePoints(0L); cust1.setMemberRank(bronzeRank); customerRepository.save(cust1);
        Customer cust2 = new Customer(); cust2.setName("Sarah Miller"); cust2.setPhone("0987654002"); cust2.setEmail("sarah.miller@example.com"); cust2.setAddress("456 Pine Ave, Otherville"); cust2.setBirthDate(LocalDate.of(1985,7,22)); cust2.setGender(Gender.FEMALE); cust2.setTotalPoints(0L); cust2.setAvailablePoints(0L); cust2.setMemberRank(bronzeRank); customerRepository.save(cust2);
        Customer cust3 = new Customer(); cust3.setName("Michael Chen"); cust3.setPhone("0933333003"); cust3.setEmail("michael.chen@example.com"); cust3.setAddress("789 Maple Dr, New City"); cust3.setBirthDate(LocalDate.of(1995,11,1)); cust3.setGender(Gender.MALE); cust3.setTotalPoints(0L); cust3.setAvailablePoints(0L); cust3.setMemberRank(bronzeRank); customerRepository.save(cust3);
        Customer cust4 = new Customer(); cust4.setName("Linda Garcia"); cust4.setPhone("0944444004"); cust4.setEmail("linda.garcia@example.com"); cust4.setAddress("101 Birch Ln, Old Town"); cust4.setBirthDate(LocalDate.of(1988,1,15)); cust4.setGender(Gender.FEMALE); cust4.setTotalPoints(0L); cust4.setAvailablePoints(0L); cust4.setMemberRank(bronzeRank); customerRepository.save(cust4);
        Customer cust5 = new Customer(); cust5.setName("Kevin Nguyen"); cust5.setPhone("0955555005"); cust5.setEmail("kevin.nguyen@example.com"); cust5.setAddress("222 Willow Way, Nextdoor"); cust5.setBirthDate(LocalDate.of(2000,9,30)); cust5.setGender(Gender.MALE); cust5.setTotalPoints(0L); cust5.setAvailablePoints(0L); cust5.setMemberRank(bronzeRank); customerRepository.save(cust5);
    }

    private void createProducts() {
        Category catBev = categoryRepository.findByName("Beverages").orElseThrow();
        Category catDry = categoryRepository.findByName("Dry Foods").orElseThrow();
        Category catSnack = categoryRepository.findByName("Snacks").orElseThrow();
        Category catPC = categoryRepository.findByName("Personal Care").orElseThrow();
        Category catHC = categoryRepository.findByName("Household Cleaning").orElseThrow();
        Category catElec = categoryRepository.findByName("Electronics").orElseThrow();
        Category catCloth = categoryRepository.findByName("Clothing").orElseThrow();

        productRepository.save(new Product(null, "BEV001", "Cola Drink Can", 10000L, 7000L, 200L, catBev, "Famous Soda", "cola.jpg", "Refreshing cola drink 330ml can", ProductStatus.ACTIVE, null, null, null));
        productRepository.save(new Product(null, "BEV002", "Fresh Milk 1L", 35000L, 28000L, 150L, catBev, "DairyBest", "milk.jpg", "UHT fresh milk 1 liter box", ProductStatus.ACTIVE, null, null, null));
        productRepository.save(new Product(null, "BEV003", "Instant Coffee 3in1", 55000L, 40000L, 100L, catBev, "Highlands Coffee", "instant_coffee.jpg", "Instant coffee 3in1 box, 20 sachets", ProductStatus.ACTIVE, null, null, null));
        productRepository.save(new Product(null, "BEV004", "Green Tea Bags", 30000L, 20000L, 80L, catBev, "TeaBrand", "greentea.jpg", "Green tea 25 tea bags box", ProductStatus.ACTIVE, null, null, null));

        productRepository.save(new Product(null, "DRY001", "Instant Noodles Spicy Shrimp", 4000L, 3000L, 500L, catDry, "NoodleTime", "noodles.jpg", "Instant noodles spicy shrimp flavor", ProductStatus.ACTIVE, null, null, null));
        productRepository.save(new Product(null, "DRY002", "Jasmine Rice 5kg Bag", 180000L, 150000L, 100L, catDry, "GoldenField", "rice.jpg", "Premium Jasmine rice 5kg bag", ProductStatus.ACTIVE, null, null, null));
        productRepository.save(new Product(null, "DRY003", "Soybean Cooking Oil 1L", 60000L, 50000L, 120L, catDry, "HealthyOil", "cookingoil.jpg", "Soybean cooking oil 1 liter bottle", ProductStatus.ACTIVE, null, null, null));
        productRepository.save(new Product(null, "DRY004", "Premium Fish Sauce 500ml", 30000L, 22000L, 200L, catDry, "SeaEssence", "fishsauce.jpg", "Premium fish sauce 500ml bottle", ProductStatus.ACTIVE, null, null, null));

        productRepository.save(new Product(null, "SNA001", "Chocolate Pie 12pcs Box", 50000L, 38000L, 80L, catSnack, "SweetTreats", "chocopie.jpg", "Classic chocolate pie box", ProductStatus.ACTIVE, null, null, null));
        productRepository.save(new Product(null, "SNA002", "Prawn Crackers Snack", 7000L, 4500L, 300L, catSnack, "CrunchyCo", "prawncrackers.jpg", "Prawn crackers snack, spicy flavor", ProductStatus.ACTIVE, null, null, null));
        productRepository.save(new Product(null, "SNA003", "Mango Chili Candy Bag", 20000L, 15000L, 150L, catSnack, "CandyLove", "mangochilicandy.jpg", "Mango chili flavor candy bag", ProductStatus.ACTIVE, null, null, null));

        productRepository.save(new Product(null, "PC001", "Men Anti-Dandruff Shampoo 650g", 180000L, 140000L, 70L, catPC, "HeadWell", "shampoo.jpg", "Men anti-dandruff shampoo Cool Sport", ProductStatus.ACTIVE, null, null, null));
        productRepository.save(new Product(null, "PC002", "Protective Body Wash 1L", 150000L, 110000L, 90L, catPC, "PureClean", "bodywash.jpg", "Total protect body wash 1 liter", ProductStatus.ACTIVE, null, null, null));

        productRepository.save(new Product(null, "HC001", "Lemon Dishwashing Liquid 750g", 25000L, 18000L, 180L, catHC, "SparkleClean", "dishwash.jpg", "Lemon scent dishwashing liquid 750g", ProductStatus.ACTIVE, null, null, null));
        productRepository.save(new Product(null, "HC002", "Laundry Powder Top Load 6kg", 220000L, 180000L, 60L, catHC, "BrightWash", "laundrypowder.jpg", "Laundry powder for top load machines 6kg", ProductStatus.ACTIVE, null, null, null));
        
        productRepository.save(new Product(null, "ELEC001", "Gaming Laptop Pro", 25000000L, 20000000L, 15L, catElec, "TechBrand", "gaminglaptop.jpg", "High performance gaming laptop", ProductStatus.ACTIVE, null, null, null));
        productRepository.save(new Product(null, "CLO001", "Men T-Shirt Cotton", 250000L, 100000L, 0L, catCloth, "FashionCo", "tshirt_men.jpg", "Cotton T-Shirt for men", ProductStatus.OOS, null, null, null));
    }


    private void createSampleInvoicesAndDetails() {
        Customer custDavid = customerRepository.findByEmail("david.lee@example.com").orElseThrow();
        Customer custSarah = customerRepository.findByEmail("sarah.miller@example.com").orElseThrow();
        Employee empAlice = employeeRepository.findByEmail("alice@invox.com").orElseThrow();

        Product prodCola = productRepository.findBySku("BEV001").orElseThrow();
        Product prodMilk = productRepository.findBySku("BEV002").orElseThrow();
        Product prodNoodles = productRepository.findBySku("DRY001").orElseThrow();
        Product prodOil = productRepository.findBySku("DRY003").orElseThrow();
        Product prodChocoPie = productRepository.findBySku("SNA001").orElseThrow();

        // Invoice 1
        Invoice invoice1 = new Invoice();
        invoice1.setCustomer(custDavid);
        invoice1.setEmployee(empAlice);
        invoice1.setInvoiceDate(LocalDateTime.of(2025, 5, 28, 10, 30, 0));
        invoice1.setPaymentMethod(PaymentMethod.CASH);
        invoice1.setStatus(InvoiceStatus.COMPLETED);
        invoice1.setNotes("Beverages and milk purchase");
        // Total amount calculation:
        long inv1TotalAmount = (prodCola.getPrice() * 5) + (prodMilk.getPrice() * 2);
        invoice1.setTotalAmount(inv1TotalAmount); // Sum of item subtotals (before overall invoice discount/tax)
        invoice1.setDiscountAmount(0L);
        invoice1.setPointsRedeemed(0L);
        // Final amount should be calculated after potential invoice-level tax and discounts
        // For simplicity in data loader, assuming no further discounts or invoice-level tax for this sample.
        // In a real scenario, service layer would calculate this.
        invoice1.setFinalAmount(inv1TotalAmount); // Assume total = final for this simple seed

        List<InvoiceDetail> details1 = new ArrayList<>();
        InvoiceDetail det1_1 = new InvoiceDetail();
        det1_1.setInvoice(invoice1); det1_1.setProduct(prodCola); det1_1.setQuantity(5L); det1_1.setUnitPrice(prodCola.getPrice()); det1_1.setProductNameSnapshot(prodCola.getName()); det1_1.setSubTotal(prodCola.getPrice() * 5);
        details1.add(det1_1);

        InvoiceDetail det1_2 = new InvoiceDetail();
        det1_2.setInvoice(invoice1); det1_2.setProduct(prodMilk); det1_2.setQuantity(2L); det1_2.setUnitPrice(prodMilk.getPrice()); det1_2.setProductNameSnapshot(prodMilk.getName()); det1_2.setSubTotal(prodMilk.getPrice() * 2);
        details1.add(det1_2);
        
        invoice1.setInvoiceDetails(details1); // Set details before saving invoice for cascade to work
        Invoice savedInvoice1 = invoiceRepository.save(invoice1); // This will save invoice and details due to CascadeType.ALL

        // Invoice 2
        Invoice invoice2 = new Invoice();
        invoice2.setCustomer(custSarah);
        invoice2.setEmployee(empAlice);
        invoice2.setInvoiceDate(LocalDateTime.of(2025, 5, 29, 14, 15, 0));
        invoice2.setPaymentMethod(PaymentMethod.CARD);
        invoice2.setStatus(InvoiceStatus.COMPLETED);
        invoice2.setNotes("Groceries and snacks");
        long inv2TotalAmount = (prodNoodles.getPrice() * 10) + (prodOil.getPrice() * 1) + (prodChocoPie.getPrice() * 3);
        invoice2.setTotalAmount(inv2TotalAmount);
        invoice2.setDiscountAmount(0L);
        invoice2.setPointsRedeemed(0L);
        invoice2.setFinalAmount(inv2TotalAmount);

        List<InvoiceDetail> details2 = new ArrayList<>();
        InvoiceDetail det2_1 = new InvoiceDetail();
        det2_1.setInvoice(invoice2); det2_1.setProduct(prodNoodles); det2_1.setQuantity(10L); det2_1.setUnitPrice(prodNoodles.getPrice()); det2_1.setProductNameSnapshot(prodNoodles.getName()); det2_1.setSubTotal(prodNoodles.getPrice() * 10);
        details2.add(det2_1);

        InvoiceDetail det2_2 = new InvoiceDetail();
        det2_2.setInvoice(invoice2); det2_2.setProduct(prodOil); det2_2.setQuantity(1L); det2_2.setUnitPrice(prodOil.getPrice()); det2_2.setProductNameSnapshot(prodOil.getName()); det2_2.setSubTotal(prodOil.getPrice() * 1);
        details2.add(det2_2);

        InvoiceDetail det2_3 = new InvoiceDetail();
        det2_3.setInvoice(invoice2); det2_3.setProduct(prodChocoPie); det2_3.setQuantity(3L); det2_3.setUnitPrice(prodChocoPie.getPrice()); det2_3.setProductNameSnapshot(prodChocoPie.getName()); det2_3.setSubTotal(prodChocoPie.getPrice() * 3);
        details2.add(det2_3);

        invoice2.setInvoiceDetails(details2);
        Invoice savedInvoice2 = invoiceRepository.save(invoice2);

        // Create PointTransaction for Invoice 2
        // Assuming BRONZE rank (1% earning rate) for Sarah initially as her points were 0
        // finalAmount from invoice 2 = 250000. Points earned = 250000 * 0.01 = 2500
        if (custSarah.getMemberRank() != null && custSarah.getMemberRank().getPointsEarningRate() != null) {
            BigDecimal pointsEarnedDecimal = BigDecimal.valueOf(savedInvoice2.getFinalAmount())
                                                 .multiply(custSarah.getMemberRank().getPointsEarningRate());
            long pointsEarned = pointsEarnedDecimal.setScale(0, RoundingMode.FLOOR).longValue();

            if (pointsEarned > 0) {
                // Update Sarah's points (this logic should ideally be in a service to ensure consistency)
                custSarah.setTotalPoints(custSarah.getTotalPoints() + pointsEarned);
                custSarah.setAvailablePoints(custSarah.getAvailablePoints() + pointsEarned);
                // Potentially update rank here if CustomerService.updateCustomerPointsAfterEarning isn't called
                customerRepository.save(custSarah); // Save updated customer

                PointTransaction pt = new PointTransaction();
                pt.setCustomer(custSarah);
                pt.setInvoice(savedInvoice2);
                pt.setTransactionType(PointTransactionType.EARN);
                pt.setPointsAmount(pointsEarned);
                pt.setCurrentTotalPoints(custSarah.getTotalPoints());
                pt.setCurrentAvailablePoints(custSarah.getAvailablePoints());
                pt.setDescription("Points earned from invoice #" + savedInvoice2.getId());
                pt.setCreatedBy(empAlice.getName()); // Example, or username
                pointTransactionRepository.save(pt);
            }
        }
    }
}