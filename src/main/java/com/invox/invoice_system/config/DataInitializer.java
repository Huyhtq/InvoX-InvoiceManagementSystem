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
@RequiredArgsConstructor
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

        if (roleRepository.count() == 0) {
            System.out.println("DataInitializer: No existing data found. Proceeding with initialization.");
            createRoles();
            createCategories(); // Categories được tạo với code và total=0 ban đầu
            createMemberRanks();
            createEmployees();
            createAppUsers();
            createCustomers();
            createProducts();   // Products được tạo và gán vào categories
            
            // Cập nhật 'total' trong Category dựa trên số lượng sản phẩm mẫu đã tạo
            updateCategoryTotalsBasedOnSampleProducts(); 
            
            createSampleInvoicesAndDetails();
            System.out.println("DataInitializer: Data initialization completed.");
        } else {
            System.out.println("DataInitializer: Data already exists. Skipping initialization.");
        }
    }

    private void createRoles() {
        System.out.println("DataInitializer: Creating roles...");
        Role employeeRole = new Role();
        employeeRole.setName("ROLE_EMPLOYEE");
        roleRepository.save(employeeRole);

        Role accountantRole = new Role();
        accountantRole.setName("ROLE_ACCOUNTANT");
        roleRepository.save(accountantRole);

        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        roleRepository.save(adminRole);
        System.out.println("DataInitializer: Roles created.");
    }

    private void createCategories() {
        System.out.println("DataInitializer: Creating categories...");
        Category cat1 = new Category();
        cat1.setName("Beverages");
        cat1.setCode("BEV");
        cat1.setTotal(0); // Khởi tạo total là 0
        cat1.setDescription("Soft drinks, milk, tea, coffee");
        categoryRepository.save(cat1);

        Category cat2 = new Category();
        cat2.setName("Dry Foods");
        cat2.setCode("DRY");
        cat2.setTotal(0);
        cat2.setDescription("Instant noodles, canned goods, spices, rice, cereals");
        categoryRepository.save(cat2);

        Category cat3 = new Category();
        cat3.setName("Snacks");
        cat3.setCode("SNA");
        cat3.setTotal(0);
        cat3.setDescription("Candies, crisps, nuts");
        categoryRepository.save(cat3);

        Category cat4 = new Category();
        cat4.setName("Personal Care");
        cat4.setCode("PER"); // Hoặc PC0, PCL - đảm bảo 3 ký tự
        cat4.setTotal(0);
        cat4.setDescription("Shampoo, shower gel, toothpaste");
        categoryRepository.save(cat4);

        Category cat5 = new Category();
        cat5.setName("Household Cleaning");
        cat5.setCode("HCL");
        cat5.setTotal(0);
        cat5.setDescription("Dishwashing liquid, detergent, floor cleaner");
        categoryRepository.save(cat5);

        Category cat6 = new Category();
        cat6.setName("Electronics");
        cat6.setCode("ELE");
        cat6.setTotal(0);
        cat6.setDescription("Electronic devices");
        categoryRepository.save(cat6);

        Category cat7 = new Category();
        cat7.setName("Clothing");
        cat7.setCode("CLO");
        cat7.setTotal(0);
        cat7.setDescription("Fashion apparel");
        categoryRepository.save(cat7);
        System.out.println("DataInitializer: Categories created.");
    }

    private void createMemberRanks() {
        System.out.println("DataInitializer: Creating member ranks...");
        MemberRank bronze = new MemberRank(); bronze.setName("BRONZE"); bronze.setMinTotalPoints(0L); bronze.setPointsEarningRate(BigDecimal.valueOf(0.01)); bronze.setDescription("Bronze Tier - 1% earning rate"); memberRankRepository.save(bronze);
        MemberRank silver = new MemberRank(); silver.setName("SILVER"); silver.setMinTotalPoints(500000L); silver.setPointsEarningRate(BigDecimal.valueOf(0.02)); silver.setDescription("Silver Tier - 2% earning rate"); memberRankRepository.save(silver);
        MemberRank gold = new MemberRank(); gold.setName("GOLD"); gold.setMinTotalPoints(2000000L); gold.setPointsEarningRate(BigDecimal.valueOf(0.03)); gold.setDescription("Gold Tier - 3% earning rate"); memberRankRepository.save(gold);
        MemberRank diamond = new MemberRank(); diamond.setName("DIAMOND"); diamond.setMinTotalPoints(5000000L); diamond.setPointsEarningRate(BigDecimal.valueOf(0.05)); diamond.setDescription("Diamond Tier - 5% earning rate"); memberRankRepository.save(diamond);
        System.out.println("DataInitializer: Member ranks created.");
    }

    private void createEmployees() {
        System.out.println("DataInitializer: Creating employees...");
        Employee emp1 = new Employee(); emp1.setName("Alice Smith"); emp1.setPhone("0901234567"); emp1.setEmail("alice@invox.com"); emp1.setAddress("123 Global St, Biz City"); emp1.setPosition("Sales Employee"); emp1.setHireDate(LocalDate.of(2020, 1, 1)); emp1.setStatus(EmployeeStatus.ACTIVE); employeeRepository.save(emp1);
        Employee emp2 = new Employee(); emp2.setName("Bob Johnson"); emp2.setPhone("0907654321"); emp2.setEmail("bob@invox.com"); emp2.setAddress("456 Main Ave, Finance Town"); emp2.setPosition("Accountant"); emp2.setHireDate(LocalDate.of(2019, 5, 15)); emp2.setStatus(EmployeeStatus.ACTIVE); employeeRepository.save(emp2);
        Employee emp3 = new Employee(); emp3.setName("Charlie Brown"); emp3.setPhone("0901112223"); emp3.setEmail("charlie@invox.com"); emp3.setAddress("789 Tech Rd, Admin Central"); emp3.setPosition("Admin"); emp3.setHireDate(LocalDate.of(2018, 10, 1)); emp3.setStatus(EmployeeStatus.ACTIVE); employeeRepository.save(emp3);
        System.out.println("DataInitializer: Employees created.");
    }

    private void createAppUsers() {
        System.out.println("DataInitializer: Creating app users...");
        Role roleEmployee = roleRepository.findByName("ROLE_EMPLOYEE").orElseThrow(() -> new RuntimeException("Error: ROLE_EMPLOYEE is not found."));
        Role roleAccountant = roleRepository.findByName("ROLE_ACCOUNTANT").orElseThrow(() -> new RuntimeException("Error: ROLE_ACCOUNTANT is not found."));
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("Error: ROLE_ADMIN is not found."));

        Employee empAlice = employeeRepository.findByEmail("alice@invox.com").orElseThrow(() -> new RuntimeException("Error: Employee Alice is not found."));
        Employee empBob = employeeRepository.findByEmail("bob@invox.com").orElseThrow(() -> new RuntimeException("Error: Employee Bob is not found."));
        Employee empCharlie = employeeRepository.findByEmail("charlie@invox.com").orElseThrow(() -> new RuntimeException("Error: Employee Charlie is not found."));

        AppUser userAlice = new AppUser(); userAlice.setUsername("alice"); userAlice.setPassword(passwordEncoder.encode("password123")); userAlice.setEmployee(empAlice); userAlice.setRole(roleEmployee); appUserRepository.save(userAlice);
        AppUser userBob = new AppUser(); userBob.setUsername("bob"); userBob.setPassword(passwordEncoder.encode("password123")); userBob.setEmployee(empBob); userBob.setRole(roleAccountant); appUserRepository.save(userBob);
        AppUser userCharlie = new AppUser(); userCharlie.setUsername("charlie"); userCharlie.setPassword(passwordEncoder.encode("password123")); userCharlie.setEmployee(empCharlie); userCharlie.setRole(roleAdmin); appUserRepository.save(userCharlie);
        System.out.println("DataInitializer: App users created.");
    }

    private void createCustomers() {
        System.out.println("DataInitializer: Creating customers...");
        MemberRank bronzeRank = memberRankRepository.findByName("BRONZE").orElseThrow(() -> new RuntimeException("Error: BRONZE rank is not found."));

        Customer cust1 = new Customer(); cust1.setName("David Lee"); cust1.setPhone("0912345001"); cust1.setEmail("david.lee@example.com"); cust1.setAddress("123 Oak St, Anytown"); cust1.setBirthDate(LocalDate.of(1990,3,10)); cust1.setGender(Gender.MALE); cust1.setTotalPoints(0L); cust1.setAvailablePoints(0L); cust1.setMemberRank(bronzeRank); customerRepository.save(cust1);
        Customer cust2 = new Customer(); cust2.setName("Sarah Miller"); cust2.setPhone("0987654002"); cust2.setEmail("sarah.miller@example.com"); cust2.setAddress("456 Pine Ave, Otherville"); cust2.setBirthDate(LocalDate.of(1985,7,22)); cust2.setGender(Gender.FEMALE); cust2.setTotalPoints(0L); cust2.setAvailablePoints(0L); cust2.setMemberRank(bronzeRank); customerRepository.save(cust2);
        System.out.println("DataInitializer: Customers created.");
    }

    private void createProducts() {
        System.out.println("DataInitializer: Creating products...");
        Category catBev = categoryRepository.findByCode("BEV").orElseThrow(() -> new RuntimeException("Category BEV not found"));
        Category catDry = categoryRepository.findByCode("DRY").orElseThrow(() -> new RuntimeException("Category DRY not found"));
        Category catSnack = categoryRepository.findByCode("SNA").orElseThrow(() -> new RuntimeException("Category SNA not found"));
        Category catPC = categoryRepository.findByCode("PER").orElseThrow(() -> new RuntimeException("Category PER not found"));
        Category catHC = categoryRepository.findByCode("HCL").orElseThrow(() -> new RuntimeException("Category HCL not found"));
        Category catElec = categoryRepository.findByCode("ELE").orElseThrow(() -> new RuntimeException("Category ELE not found"));
        Category catCloth = categoryRepository.findByCode("CLO").orElseThrow(() -> new RuntimeException("Category CLO not found"));

        // Các SKU này nên được giữ nguyên vì chúng là dữ liệu mẫu và đã theo định dạng
        productRepository.save(new Product(null, "BEV001", "Cola Drink Can", 10000L, 7000L, 200L, catBev, "Famous Soda", "cola.jpg", "Refreshing cola drink 330ml can", ProductStatus.ACTIVE, null, null, null));
        productRepository.save(new Product(null, "BEV002", "Fresh Milk 1L", 35000L, 28000L, 150L, catBev, "DairyBest", "milk.jpg", "UHT fresh milk 1 liter box", ProductStatus.ACTIVE, null, null, null));
        productRepository.save(new Product(null, "BEV003", "Instant Coffee 3in1", 55000L, 40000L, 100L, catBev, "Highlands Coffee", "instant_coffee.jpg", "Instant coffee 3in1 box, 20 sachets", ProductStatus.ACTIVE, null, null, null));
        productRepository.save(new Product(null, "BEV004", "Green Tea Bags", 30000L, 20000L, 80L, catBev, "TeaBrand", "greentea.jpg", "Green tea 25 tea bags box", ProductStatus.ACTIVE, null, null, null));

        productRepository.save(new Product(null, "DRY001", "Instant Noodles Spicy Shrimp", 4000L, 3000L, 500L, catDry, "NoodleTime", "noodles.jpg", "Instant noodles spicy shrimp flavor", ProductStatus.ACTIVE, null, null, null));
        productRepository.save(new Product(null, "DRY002", "Jasmine Rice 5kg Bag", 180000L, 150000L, 100L, catDry, "GoldenField", "rice.jpg", "Premium Jasmine rice 5kg bag", ProductStatus.ACTIVE, null, null, null));
        // ... (Thêm các sản phẩm khác)

        System.out.println("DataInitializer: Products created.");
    }

    private void updateCategoryTotalsBasedOnSampleProducts() {
        System.out.println("DataInitializer: Updating category totals based on sample products...");
        List<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            long productCountForCategory = productRepository.countByCategory(category);
            category.setTotal((int) productCountForCategory);
            categoryRepository.save(category);
            System.out.println("DataInitializer: Category '" + category.getName() + "' (Code: " + category.getCode() + ") total updated to: " + category.getTotal());
        }
        System.out.println("DataInitializer: Category totals updated.");
    }


    private void createSampleInvoicesAndDetails() {
        System.out.println("DataInitializer: Creating sample invoices and details...");
        Customer custDavid = customerRepository.findByEmail("david.lee@example.com").orElseThrow(() -> new RuntimeException("Customer David Lee not found"));
        Customer custSarah = customerRepository.findByEmail("sarah.miller@example.com").orElseThrow(() -> new RuntimeException("Customer Sarah Miller not found"));
        Employee empAlice = employeeRepository.findByEmail("alice@invox.com").orElseThrow(() -> new RuntimeException("Employee Alice not found"));

        Product prodCola = productRepository.findBySku("BEV001").orElseThrow(() -> new RuntimeException("Product BEV001 not found"));
        Product prodMilk = productRepository.findBySku("BEV002").orElseThrow(() -> new RuntimeException("Product BEV002 not found"));
        Product prodNoodles = productRepository.findBySku("DRY001").orElseThrow(() -> new RuntimeException("Product DRY001 not found"));

        // Invoice 1
        Invoice invoice1 = new Invoice();
        invoice1.setCustomer(custDavid);
        invoice1.setEmployee(empAlice);
        invoice1.setInvoiceDate(LocalDateTime.of(2024, 6, 1, 10, 30, 0)); // Ngày trong quá khứ hoặc hiện tại gần
        invoice1.setPaymentMethod(PaymentMethod.CASH);
        invoice1.setStatus(InvoiceStatus.COMPLETED);
        invoice1.setNotes("Beverages and milk purchase");
        
        List<InvoiceDetail> details1 = new ArrayList<>();
        long inv1TotalAmount = 0L;

        InvoiceDetail det1_1 = new InvoiceDetail();
        det1_1.setInvoice(invoice1); det1_1.setProduct(prodCola); det1_1.setQuantity(5L); det1_1.setUnitPrice(prodCola.getPrice()); det1_1.setProductNameSnapshot(prodCola.getName()); det1_1.setSubTotal(prodCola.getPrice() * 5);
        details1.add(det1_1);
        inv1TotalAmount += det1_1.getSubTotal();

        InvoiceDetail det1_2 = new InvoiceDetail();
        det1_2.setInvoice(invoice1); det1_2.setProduct(prodMilk); det1_2.setQuantity(2L); det1_2.setUnitPrice(prodMilk.getPrice()); det1_2.setProductNameSnapshot(prodMilk.getName()); det1_2.setSubTotal(prodMilk.getPrice() * 2);
        details1.add(det1_2);
        inv1TotalAmount += det1_2.getSubTotal();
        
        invoice1.setTotalAmount(inv1TotalAmount);
        invoice1.setDiscountAmount(0L); // Giả sử không giảm giá
        invoice1.setPointsRedeemed(0L);
        invoice1.setFinalAmount(inv1TotalAmount); // Giả sử final = total
        invoice1.setInvoiceDetails(details1);
        Invoice savedInvoice1 = invoiceRepository.save(invoice1); // Lưu invoice và details

        // Invoice 2 (Tương tự)
        // ...

        // Create PointTransaction for Invoice 1 (Ví dụ)
        if (custDavid.getMemberRank() != null && custDavid.getMemberRank().getPointsEarningRate() != null) {
            BigDecimal pointsEarnedDecimal = BigDecimal.valueOf(savedInvoice1.getFinalAmount())
                                             .multiply(custDavid.getMemberRank().getPointsEarningRate());
            long pointsEarned = pointsEarnedDecimal.setScale(0, RoundingMode.FLOOR).longValue();

            if (pointsEarned > 0) {
                custDavid.setTotalPoints(custDavid.getTotalPoints() + pointsEarned);
                custDavid.setAvailablePoints(custDavid.getAvailablePoints() + pointsEarned);
                customerRepository.save(custDavid);

                PointTransaction pt = new PointTransaction();
                pt.setCustomer(custDavid);
                pt.setInvoice(savedInvoice1);
                pt.setTransactionType(PointTransactionType.EARN);
                pt.setPointsAmount(pointsEarned);
                pt.setCurrentTotalPoints(custDavid.getTotalPoints());
                pt.setCurrentAvailablePoints(custDavid.getAvailablePoints());
                pt.setDescription("Points earned from invoice #" + savedInvoice1.getId());
                pt.setCreatedBy(empAlice.getName());
                pointTransactionRepository.save(pt);
            }
        }
        System.out.println("DataInitializer: Sample invoices and point transactions created.");
    }
}