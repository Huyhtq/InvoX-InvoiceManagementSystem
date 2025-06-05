-- Drop tables in reverse order of creation (dependent tables first)
DROP TABLE History CASCADE CONSTRAINTS;
DROP TABLE PointTransaction CASCADE CONSTRAINTS;
DROP TABLE InvoiceDetail CASCADE CONSTRAINTS;
DROP TABLE Invoice CASCADE CONSTRAINTS;
DROP TABLE AppUser CASCADE CONSTRAINTS;
DROP TABLE Employee CASCADE CONSTRAINTS;
DROP TABLE Customer CASCADE CONSTRAINTS;
DROP TABLE MemberRank CASCADE CONSTRAINTS;
DROP TABLE Product CASCADE CONSTRAINTS;
DROP TABLE Category CASCADE CONSTRAINTS;
DROP TABLE Role CASCADE CONSTRAINTS;

DROP SEQUENCE Category_SEQ;
DROP SEQUENCE Product_SEQ;
DROP SEQUENCE MemberRank_SEQ;
DROP SEQUENCE Customer_SEQ;
DROP SEQUENCE Role_SEQ;
DROP SEQUENCE Employee_SEQ;
DROP SEQUENCE AppUser_SEQ;
DROP SEQUENCE Invoice_SEQ;
DROP SEQUENCE InvoiceDetail_SEQ;
DROP SEQUENCE PointTransaction_SEQ;
DROP SEQUENCE History_SEQ;

-- Create table Category
CREATE TABLE Category (
    id NUMBER(3, 0) PRIMARY KEY,
    name VARCHAR2(50) NOT NULL UNIQUE,
    code VARCHAR2(3) NOT NULL UNIQUE,
    total NUMBER(3,0) DEFAULT 0,
    description VARCHAR2(255)
);

-- Create table Product
CREATE TABLE Product (
    id NUMBER(5, 0) PRIMARY KEY,
    sku VARCHAR2(50) NOT NULL UNIQUE,
    name VARCHAR2(100) NOT NULL,
    price NUMBER(12, 0) NOT NULL, -- Giá bán
    cost_price NUMBER(12, 0),    -- Giá nhập (để tính lợi nhuận)
    quantity NUMBER(6) NOT NULL, -- Số lượng tồn kho
    category_id NUMBER(3, 0),
    brand VARCHAR2(50),
    image_url VARCHAR2(255),     -- Đường dẫn hình ảnh sản phẩm
    description CLOB,            -- Mô tả chi tiết sản phẩm
    status VARCHAR2(20) DEFAULT 'ACTIVE' NOT NULL, -- ACTIVE, INACTIVE, OUT_OF_STOCK
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES Category(id)
);

-- Create table MemberRank (NEW)
CREATE TABLE MemberRank (
    id NUMBER(3, 0) PRIMARY KEY,
    name VARCHAR2(50) NOT NULL UNIQUE, -- Ví dụ: 'Đồng', 'Bạc', 'Vàng', 'Kim Cương'
    min_total_points NUMBER(8, 0) NOT NULL, -- Số điểm tối thiểu để đạt hạng này
    points_earning_rate NUMBER(5, 2) NOT NULL, -- Tỷ lệ tích điểm cho hạng này (ví dụ: 0.01 = 1%)
    description VARCHAR2(255),
    CONSTRAINT chk_min_total_points CHECK (min_total_points >= 0)
);

-- Create table Customer
CREATE TABLE Customer (
    id NUMBER(12, 0) PRIMARY KEY,
    name VARCHAR2(100) NOT NULL, 
    phone VARCHAR2(15) UNIQUE,   -- Số điện thoại có thể là duy nhất
    email VARCHAR2(100) UNIQUE,  -- Email có thể là duy nhất
    address VARCHAR2(255),       -- Địa chỉ khách hàng
    birth_date DATE,
    gender VARCHAR2(10),         -- Male, Female, Other
    total_points NUMBER(10, 0) DEFAULT 0 NOT NULL,     -- Tổng điểm tích lũy trọn đời
    available_points NUMBER(10, 0) DEFAULT 0 NOT NULL,  -- Số điểm có thể sử dụng
    member_rank_id NUMBER(3, 0) DEFAULT 1 NOT NULL, -- Mặc định là hạng đầu tiên (ví dụ: Đồng)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_customer_member_rank FOREIGN KEY (member_rank_id) REFERENCES MemberRank(id),
    CONSTRAINT chk_customer_points CHECK (total_points >= 0 AND available_points >= 0)
);

-- Create table Role
CREATE TABLE Role (
    id NUMBER(3, 0) PRIMARY KEY,
    name VARCHAR2(30) NOT NULL UNIQUE
);

-- Create table Employee
CREATE TABLE Employee (
    id NUMBER(12, 0) PRIMARY KEY,
    name VARCHAR2(100) NOT NULL, 
    phone VARCHAR2(15) UNIQUE,
    email VARCHAR2(100) UNIQUE,
    address VARCHAR2(255),
    position VARCHAR2(50),
    hire_date DATE DEFAULT SYSDATE NOT NULL,
    termination_date DATE,
    is_active VARCHAR2(10) DEFAULT 'ACTIVE' NOT NULL, -- 'ACTIVE' hoặc 'INACTIVE'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT chk_employee_is_active CHECK (is_active IN ('ACTIVE', 'INACTIVE'))
);

-- Create table App_User (for login)
CREATE TABLE AppUser (
    id NUMBER(8, 0) PRIMARY KEY,
    username VARCHAR2(30) NOT NULL UNIQUE,
    password VARCHAR2(255) NOT NULL, 
    employee_id NUMBER(12, 0) UNIQUE, -- Một nhân viên chỉ có một tài khoản user
    role_id NUMBER(3, 0) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_app_user_employee FOREIGN KEY (employee_id) REFERENCES Employee(id),
    CONSTRAINT fk_app_user_role FOREIGN KEY (role_id) REFERENCES Role(id)
);

-- Create table Invoice
CREATE TABLE Invoice (
    id NUMBER(12, 0) PRIMARY KEY,
    customer_id NUMBER(12, 0),
    employee_id NUMBER(12, 0) NOT NULL, -- Nhân viên tạo hóa đơn phải có
    invoice_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- Thời điểm tạo hóa đơn
    total_amount NUMBER(14, 0) NOT NULL, -- Tổng tiền của hóa đơn (sau khi giảm giá và trước khi trừ điểm)
    discount_amount NUMBER(12, 0) DEFAULT 0 NOT NULL, -- Số tiền giảm giá trên tổng hóa đơn
    points_redeemed NUMBER(10, 0) DEFAULT 0 NOT NULL, -- Số điểm đã dùng cho hóa đơn này
    final_amount NUMBER(14, 0) NOT NULL, -- Tổng tiền cuối cùng khách phải trả
    payment_method VARCHAR2(50), -- CASH, CARD, TRANSFER, ...
    status VARCHAR2(20) DEFAULT 'COMPLETED' NOT NULL, -- PENDING, COMPLETED, CANCELLED, REFUNDED
    notes VARCHAR2(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_invoice_customer FOREIGN KEY (customer_id) REFERENCES Customer(id),
    CONSTRAINT fk_invoice_employee FOREIGN KEY (employee_id) REFERENCES Employee(id),
    CONSTRAINT chk_invoice_amounts CHECK (total_amount >= 0 AND discount_amount >= 0 AND final_amount >= 0 AND points_redeemed >= 0)
);

-- Create table InvoiceDetail
CREATE TABLE InvoiceDetail (
    id NUMBER(12, 0) PRIMARY KEY,
    invoice_id NUMBER(12, 0) NOT NULL,
    product_id NUMBER(5, 0) NOT NULL,
    quantity NUMBER(6, 0) NOT NULL,
    unit_price NUMBER(12, 0) NOT NULL,     -- Giá bán của sản phẩm tại thời điểm bán (snapshot)
    product_name_snapshot VARCHAR2(100) NOT NULL, -- Tên sản phẩm tại thời điểm bán (snapshot)
    sub_total NUMBER(14, 0) NOT NULL,       -- Thành tiền cho dòng này (quantity * unit_price)
    CONSTRAINT fk_invoicedetail_invoice FOREIGN KEY (invoice_id) REFERENCES Invoice(id),
    CONSTRAINT fk_invoicedetail_product FOREIGN KEY (product_id) REFERENCES Product(id),
    CONSTRAINT chk_invoicedetail_quantity CHECK (quantity > 0),
    CONSTRAINT chk_invoicedetail_price CHECK (unit_price >= 0)
);

-- Create table PointTransaction (NEW)
CREATE TABLE PointTransaction (
    id NUMBER(15, 0) PRIMARY KEY,
    customer_id NUMBER(12, 0) NOT NULL,
    invoice_id NUMBER(12, 0),             -- Có thể NULL nếu giao dịch không liên quan đến hóa đơn (ví dụ: điều chỉnh thủ công)
    transaction_type VARCHAR2(20) NOT NULL, -- 'EARN', 'REDEEM', 'ADJUSTMENT'
    points_amount NUMBER(10, 0) NOT NULL,   -- Số điểm thay đổi (dương cho EARN, âm cho REDEEM)
    current_total_points NUMBER(10, 0),     -- Tổng điểm của khách hàng sau giao dịch này (có thể tính toán)
    current_available_points NUMBER(10, 0), -- Điểm khả dụng sau giao dịch này (có thể tính toán)
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    description VARCHAR2(255),
    created_by VARCHAR2(50),                -- Ai đã thực hiện giao dịch (ví dụ: tên user)
    CONSTRAINT fk_point_transaction_customer FOREIGN KEY (customer_id) REFERENCES Customer(id),
    CONSTRAINT fk_point_transaction_invoice FOREIGN KEY (invoice_id) REFERENCES Invoice(id)
);

-- Create table History (for audit trail)
CREATE TABLE History (
    id NUMBER(12, 0) PRIMARY KEY,
    user_id NUMBER(8, 0),                 -- Ai đã thực hiện hành động
    action VARCHAR2(50) NOT NULL,          -- Ví dụ: 'CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'CHANGE_STATUS'
    target_type VARCHAR2(100) NOT NULL,     -- Bảng bị ảnh hưởng (e.g., 'Product', 'Invoice', 'Customer')
    target_id NUMBER(12, 0),              -- ID của bản ghi bị ảnh hưởng
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    detail_json CLOB,                     -- Lưu trữ chi tiết thay đổi (JSON)
    CONSTRAINT fk_history_user FOREIGN KEY (user_id) REFERENCES AppUser(id)
);

-- Sequence cho bảng Category (ví dụ)
CREATE SEQUENCE Category_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Sequence cho bảng Product
CREATE SEQUENCE Product_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Sequence cho bảng MemberRank
CREATE SEQUENCE MemberRank_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Sequence cho bảng Customer
CREATE SEQUENCE Customer_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Sequence cho bảng Role
CREATE SEQUENCE Role_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Sequence cho bảng Employee
CREATE SEQUENCE Employee_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Sequence cho bảng AppUser
CREATE SEQUENCE AppUser_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Sequence cho bảng Invoice
CREATE SEQUENCE Invoice_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Sequence cho bảng InvoiceDetail
CREATE SEQUENCE InvoiceDetail_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Sequence cho bảng PointTransaction
CREATE SEQUENCE PointTransaction_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Sequence cho bảng History
CREATE SEQUENCE History_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;