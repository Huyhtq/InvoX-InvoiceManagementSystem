-- ===================================================================================
-- NOTE: ENSURE SEQUENCES ARE CREATED AND SYNCHRONIZED BEFORE RUNNING THESE INSERTS.
-- (E.g., Category_SEQ, Product_SEQ, MemberRank_SEQ, Customer_SEQ, Role_SEQ, 
--         Employee_SEQ, AppUser_SEQ, Invoice_SEQ, InvoiceDetail_SEQ, 
--         PointTransaction_SEQ, History_SEQ)
-- IF YOU DROPPED SEQUENCES, RECREATE THEM (e.g., START WITH 1 OR HIGHER THAN CURRENT MAX ID)
-- ===================================================================================

-- Insert Roles (Unchanged)
-- BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE Role_SEQ'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END; /
-- CREATE SEQUENCE Role_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
INSERT INTO Role (id, name) VALUES (Role_SEQ.NEXTVAL, 'ROLE_EMPLOYEE');
INSERT INTO Role (id, name) VALUES (Role_SEQ.NEXTVAL, 'ROLE_ACCOUNTANT');
INSERT INTO Role (id, name) VALUES (Role_SEQ.NEXTVAL, 'ROLE_ADMIN');

-- Insert Categories (Translated and expanded)
-- BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE Category_SEQ'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END; /
-- CREATE SEQUENCE Category_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
INSERT INTO Category (id, name, description) VALUES (Category_SEQ.NEXTVAL, 'Beverages', 'Soft drinks, milk, tea, coffee'); -- ID assumed to be 1
INSERT INTO Category (id, name, description) VALUES (Category_SEQ.NEXTVAL, 'Dry Foods', 'Instant noodles, canned goods, spices, rice, cereals'); -- ID assumed to be 2
INSERT INTO Category (id, name, description) VALUES (Category_SEQ.NEXTVAL, 'Snacks', 'Candies, crisps, nuts'); -- ID assumed to be 3
INSERT INTO Category (id, name, description) VALUES (Category_SEQ.NEXTVAL, 'Personal Care', 'Shampoo, shower gel, toothpaste'); -- ID assumed to be 4
INSERT INTO Category (id, name, description) VALUES (Category_SEQ.NEXTVAL, 'Household Cleaning', 'Dishwashing liquid, detergent, floor cleaner'); -- ID assumed to be 5
INSERT INTO Category (id, name, description) VALUES (Category_SEQ.NEXTVAL, 'Electronics', 'Electronic devices'); -- ID assumed to be 6 (for existing products)
INSERT INTO Category (id, name, description) VALUES (Category_SEQ.NEXTVAL, 'Clothing', 'Fashion apparel'); -- ID assumed to be 7 (for existing products)


-- Insert MemberRanks (Translated descriptions)
-- BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE MemberRank_SEQ'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END; /
-- CREATE SEQUENCE MemberRank_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
INSERT INTO MemberRank (id, name, min_total_points, points_earning_rate, description) VALUES (MemberRank_SEQ.NEXTVAL, 'BRONZE', 0, 0.01, 'Bronze Tier - 1% earning rate'); -- ID 1
INSERT INTO MemberRank (id, name, min_total_points, points_earning_rate, description) VALUES (MemberRank_SEQ.NEXTVAL, 'SILVER', 500000, 0.015, 'Silver Tier - 1.5% earning rate'); -- ID 2
INSERT INTO MemberRank (id, name, min_total_points, points_earning_rate, description) VALUES (MemberRank_SEQ.NEXTVAL, 'GOLD', 2000000, 0.02, 'Gold Tier - 2% earning rate');   -- ID 3
INSERT INTO MemberRank (id, name, min_total_points, points_earning_rate, description) VALUES (MemberRank_SEQ.NEXTVAL, 'DIAMOND', 5000000, 0.03, 'Diamond Tier - 3% earning rate'); -- ID 4

-- Insert Employees (Unchanged, assuming names are fine)
-- BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE Employee_SEQ'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END; /
-- CREATE SEQUENCE Employee_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
INSERT INTO Employee (id, name, phone, email, address, position, hire_date, is_active, created_at, updated_at)
VALUES (Employee_SEQ.NEXTVAL, 'Alice Smith', '0901234567', 'alice@invox.com', '123 Global St, Biz City', 'Sales Employee', TO_DATE('2020-01-01', 'YYYY-MM-DD'), 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 1
INSERT INTO Employee (id, name, phone, email, address, position, hire_date, is_active, created_at, updated_at)
VALUES (Employee_SEQ.NEXTVAL, 'Bob Johnson', '0907654321', 'bob@invox.com', '456 Main Ave, Finance Town', 'Accountant', TO_DATE('2019-05-15', 'YYYY-MM-DD'), 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 2
INSERT INTO Employee (id, name, phone, email, address, position, hire_date, is_active, created_at, updated_at)
VALUES (Employee_SEQ.NEXTVAL, 'Charlie Brown', '0901112223', 'charlie@invox.com', '789 Tech Rd, Admin Central', 'Admin', TO_DATE('2018-10-01', 'YYYY-MM-DD'), 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 3

-- Insert AppUsers (Passwords are BCrypt hashes of "password")
-- BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE AppUser_SEQ'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END; /
-- CREATE SEQUENCE AppUser_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
INSERT INTO AppUser (id, username, password, employee_id, role_id, created_at, updated_at)
VALUES (AppUser_SEQ.NEXTVAL, 'alice', '$2a$10$ZEWCp1PS4Gr.v5DDjyeUgOgoonebQejSnwTBjouRipQp5vD.PQB3u', 1, 1, SYSTIMESTAMP, SYSTIMESTAMP); -- Alice (Employee ID 1) is ROLE_EMPLOYEE (Role ID 1)
INSERT INTO AppUser (id, username, password, employee_id, role_id, created_at, updated_at)
VALUES (AppUser_SEQ.NEXTVAL, 'bob', '$2a$10$DpXhSrnjx8dEXH4lpfYyJeBGJznZOBvrDJql6OYZ8a2xx9dCn/GOS', 2, 2, SYSTIMESTAMP, SYSTIMESTAMP); -- Bob (Employee ID 2) is ROLE_ACCOUNTANT (Role ID 2)
INSERT INTO AppUser (id, username, password, employee_id, role_id, created_at, updated_at)
VALUES (AppUser_SEQ.NEXTVAL, 'charlie', '$2a$10$kq4P6006RVPasIjImZA70O8cPH9fdIw3n01lgUalftdh9fUzQQBS2', 3, 3, SYSTIMESTAMP, SYSTIMESTAMP); -- Charlie (Employee ID 3) is ROLE_ADMIN (Role ID 3)

-- Insert Customers (5 customers, points = 0, member_rank_id = 1 (BRONZE))
-- BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE Customer_SEQ'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END; /
-- CREATE SEQUENCE Customer_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
INSERT INTO Customer (id, name, phone, email, address, birth_date, gender, total_points, available_points, member_rank_id, created_at, updated_at)
VALUES (Customer_SEQ.NEXTVAL, 'David Lee', '0912345001', 'david.lee@example.com', '123 Oak St, Anytown', TO_DATE('1990-03-10', 'YYYY-MM-DD'), 'MALE', 0, 0, 1, SYSTIMESTAMP, SYSTIMESTAMP); -- ID 1
INSERT INTO Customer (id, name, phone, email, address, birth_date, gender, total_points, available_points, member_rank_id, created_at, updated_at)
VALUES (Customer_SEQ.NEXTVAL, 'Sarah Miller', '0987654002', 'sarah.miller@example.com', '456 Pine Ave, Otherville', TO_DATE('1985-07-22', 'YYYY-MM-DD'), 'FEMALE', 0, 0, 1, SYSTIMESTAMP, SYSTIMESTAMP); -- ID 2
INSERT INTO Customer (id, name, phone, email, address, birth_date, gender, total_points, available_points, member_rank_id, created_at, updated_at)
VALUES (Customer_SEQ.NEXTVAL, 'Michael Chen', '0933333003', 'michael.chen@example.com', '789 Maple Dr, New City', TO_DATE('1995-11-01', 'YYYY-MM-DD'), 'MALE', 0, 0, 1, SYSTIMESTAMP, SYSTIMESTAMP); -- ID 3
INSERT INTO Customer (id, name, phone, email, address, birth_date, gender, total_points, available_points, member_rank_id, created_at, updated_at)
VALUES (Customer_SEQ.NEXTVAL, 'Linda Garcia', '0944444004', 'linda.garcia@example.com', '101 Birch Ln, Old Town', TO_DATE('1988-01-15', 'YYYY-MM-DD'), 'FEMALE', 0, 0, 1, SYSTIMESTAMP, SYSTIMESTAMP); -- ID 4
INSERT INTO Customer (id, name, phone, email, address, birth_date, gender, total_points, available_points, member_rank_id, created_at, updated_at)
VALUES (Customer_SEQ.NEXTVAL, 'Kevin Nguyen', '0955555005', 'kevin.nguyen@example.com', '222 Willow Way, Nextdoor', TO_DATE('2000-09-30', 'YYYY-MM-DD'), 'MALE', 0, 0, 1, SYSTIMESTAMP, SYSTIMESTAMP); -- ID 5

-- Insert Products (15 supermarket items across 3-5 categories)
-- (Assuming Category IDs: Beverages=1, Dry Foods=2, Snacks=3, Personal Care=4, Household Cleaning=5, Electronics=6, Clothing=7)
-- BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE Product_SEQ'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END; /
-- CREATE SEQUENCE Product_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Category: Beverages (ID 1)
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'BEV001', 'Cola Drink Can', 10000, 7000, 200, 1, 'Famous Soda', 'cola.jpg', 'Refreshing cola drink 330ml can', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 1
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'BEV002', 'Fresh Milk 1L', 35000, 28000, 150, 1, 'DairyBest', 'milk.jpg', 'UHT fresh milk 1 liter box', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 2
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'BEV003', 'Instant Coffee 3in1', 55000, 40000, 100, 1, 'Highlands Coffee', 'instant_coffee.jpg', 'Instant coffee 3in1 box, 20 sachets', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 3
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'BEV004', 'Green Tea Bags', 30000, 20000, 80, 1, 'TeaBrand', 'greentea.jpg', 'Green tea 25 tea bags box', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 4

-- Category: Dry Foods (ID 2)
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'DRY001', 'Instant Noodles Spicy Shrimp', 4000, 3000, 500, 2, 'NoodleTime', 'noodles.jpg', 'Instant noodles spicy shrimp flavor, 30 packs carton', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 5
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'DRY002', 'Jasmine Rice 5kg Bag', 180000, 150000, 100, 2, 'GoldenField', 'rice.jpg', 'Premium Jasmine rice 5kg bag', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 6
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'DRY003', 'Soybean Cooking Oil 1L', 60000, 50000, 120, 2, 'HealthyOil', 'cookingoil.jpg', 'Soybean cooking oil 1 liter bottle', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 7
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'DRY004', 'Premium Fish Sauce 500ml', 30000, 22000, 200, 2, 'SeaEssence', 'fishsauce.jpg', 'Premium fish sauce 500ml bottle', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 8

-- Category: Snacks (ID 3)
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'SNA001', 'Chocolate Pie 12pcs Box', 50000, 38000, 80, 3, 'SweetTreats', 'chocopie.jpg', 'Classic chocolate pie box', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 9
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'SNA002', 'Prawn Crackers Snack', 7000, 4500, 300, 3, 'CrunchyCo', 'prawncrackers.jpg', 'Prawn crackers snack, spicy flavor', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 10
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'SNA003', 'Mango Chili Candy Bag', 20000, 15000, 150, 3, 'CandyLove', 'mangochilicandy.jpg', 'Mango chili flavor candy bag', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 11

-- Category: Personal Care (ID 4)
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'PC001', 'Men Anti-Dandruff Shampoo 650g', 180000, 140000, 70, 4, 'HeadWell', 'shampoo.jpg', 'Men anti-dandruff shampoo Cool Sport', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 12
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'PC002', 'Protective Body Wash 1L', 150000, 110000, 90, 4, 'PureClean', 'bodywash.jpg', 'Total protect body wash 1 liter', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 13

-- Category: Household Cleaning (ID 5)
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'HC001', 'Lemon Dishwashing Liquid 750g', 25000, 18000, 180, 5, 'SparkleClean', 'dishwash.jpg', 'Lemon scent dishwashing liquid 750g', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 14
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'HC002', 'Laundry Powder Top Load 6kg', 220000, 180000, 60, 5, 'BrightWash', 'laundrypowder.jpg', 'Laundry powder for top load machines 6kg', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 15

-- Example existing products for Electronics (ID 6) and Clothing (ID 7) mentioned in previous versions
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'ELEC001', 'Gaming Laptop Pro', 25000000, 20000000, 15, 6, 'TechBrand', 'gaminglaptop.jpg', 'High performance gaming laptop', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 16
INSERT INTO Product (id, sku, name, price, cost_price, quantity, category_id, brand, image_url, description, status, created_at, updated_at)
VALUES (Product_SEQ.NEXTVAL, 'CLO001', 'Men T-Shirt Cotton', 250000, 100000, 0, 7, 'FashionCo', 'tshirt_men.jpg', 'Cotton T-Shirt for men', 'OOS', SYSTIMESTAMP, SYSTIMESTAMP); -- ID 17


-- Insert Invoices (example data)
-- BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE Invoice_SEQ'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END; /
-- CREATE SEQUENCE Invoice_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
-- BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE InvoiceDetail_SEQ'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END; /
-- CREATE SEQUENCE InvoiceDetail_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Invoice 1: Customer David Lee (ID 1), Employee Alice Smith (ID 1)
-- Buys: Cola Drink Can (ID 1) - Qty 5, Fresh Milk 1L (ID 2) - Qty 2
-- Total amount = (10000 * 5) + (35000 * 2) = 50000 + 70000 = 120000
INSERT INTO Invoice (id, customer_id, employee_id, invoice_date, total_amount, discount_amount, points_redeemed, final_amount, payment_method, status, notes, created_at, updated_at)
VALUES (Invoice_SEQ.NEXTVAL, 1, 1, TO_TIMESTAMP('2025-05-28 10:30:00', 'YYYY-MM-DD HH24:MI:SS'), 
        120000, 0, 0, 120000, 
        'CASH', 'COMPLETED', 'Beverages and milk purchase', SYSTIMESTAMP, SYSTIMESTAMP); -- Invoice ID 1

INSERT INTO InvoiceDetail (id, invoice_id, product_id, quantity, unit_price, product_name_snapshot, sub_total)
VALUES (InvoiceDetail_SEQ.NEXTVAL, Invoice_SEQ.CURRVAL, 1, 5, 10000, 'Cola Drink Can', 50000);
INSERT INTO InvoiceDetail (id, invoice_id, product_id, quantity, unit_price, product_name_snapshot, sub_total)
VALUES (InvoiceDetail_SEQ.NEXTVAL, Invoice_SEQ.CURRVAL, 2, 2, 35000, 'Fresh Milk 1L', 70000);

-- Invoice 2: Customer Sarah Miller (ID 2), Employee Alice Smith (ID 1)
-- Buys: Instant Noodles (ID 5) - Qty 10, Cooking Oil (ID 7) - Qty 1, ChocoPie (ID 9) - Qty 3
-- Total amount = (4000 * 10) + (60000 * 1) + (50000 * 3) = 40000 + 60000 + 150000 = 250000
INSERT INTO Invoice (id, customer_id, employee_id, invoice_date, total_amount, discount_amount, points_redeemed, final_amount, payment_method, status, notes, created_at, updated_at)
VALUES (Invoice_SEQ.NEXTVAL, 2, 1, TO_TIMESTAMP('2025-05-29 14:15:00', 'YYYY-MM-DD HH24:MI:SS'), 
        250000, 0, 0, 250000, 
        'CARD', 'COMPLETED', 'Groceries and snacks', SYSTIMESTAMP, SYSTIMESTAMP); -- Invoice ID 2

INSERT INTO InvoiceDetail (id, invoice_id, product_id, quantity, unit_price, product_name_snapshot, sub_total)
VALUES (InvoiceDetail_SEQ.NEXTVAL, Invoice_SEQ.CURRVAL, 5, 10, 4000, 'Instant Noodles Spicy Shrimp', 40000);
INSERT INTO InvoiceDetail (id, invoice_id, product_id, quantity, unit_price, product_name_snapshot, sub_total)
VALUES (InvoiceDetail_SEQ.NEXTVAL, Invoice_SEQ.CURRVAL, 7, 1, 60000, 'Soybean Cooking Oil 1L', 60000);
INSERT INTO InvoiceDetail (id, invoice_id, product_id, quantity, unit_price, product_name_snapshot, sub_total)
VALUES (InvoiceDetail_SEQ.NEXTVAL, Invoice_SEQ.CURRVAL, 9, 3, 50000, 'Chocolate Pie 12pcs Box', 150000);


-- Insert PointTransactions (example)
-- BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE PointTransaction_SEQ'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END; /
-- CREATE SEQUENCE PointTransaction_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Points for Sarah Miller (ID 2) from Invoice 2 (ID 2)
-- Assuming Bronze rank (ID 1, earning rate 0.01) before this purchase, as her points were 0.
-- Final amount of invoice 2 = 250000. Points earned = 250000 * 0.01 = 2500
-- After this, Sarah's points: total = 2500, available = 2500. She's still BRONZE.
INSERT INTO PointTransaction (id, customer_id, invoice_id, transaction_type, points_amount, current_total_points, current_available_points, transaction_date, description, created_by)
VALUES (PointTransaction_SEQ.NEXTVAL, 2, 2, 'EARN', 2500, 2500, 2500, SYSTIMESTAMP, 'Points earned from invoice #2', 'alice');

-- Update customer points for Sarah Miller (ID 2) based on the transaction above
UPDATE Customer SET total_points = 2500, available_points = 2500 WHERE id = 2;


-- Insert History (example)
-- BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE History_SEQ'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END; /
-- CREATE SEQUENCE History_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
INSERT INTO History (id, user_id, action, target_type, target_id, timestamp, detail_json)
VALUES (History_SEQ.NEXTVAL, 3, 'CREATE_EMPLOYEE', 'Employee', 2, SYSTIMESTAMP, '{"employee_name": "Bob Johnson"}');
INSERT INTO History (id, user_id, action, target_type, target_id, timestamp, detail_json)
VALUES (History_SEQ.NEXTVAL, 1, 'UPDATE_PRODUCT_PRICE', 'Product', 1, SYSTIMESTAMP, '{"product_name": "Cola Drink Can", "old_price": 10000, "new_price": 11000}');

COMMIT;