/*
DROP TABLE History CASCADE CONSTRAINTS;
DROP TABLE InvoiceDetail CASCADE CONSTRAINTS;
DROP TABLE Invoice CASCADE CONSTRAINTS;
DROP TABLE AppUser CASCADE CONSTRAINTS;
DROP TABLE Role CASCADE CONSTRAINTS;
DROP TABLE Employee CASCADE CONSTRAINTS;
DROP TABLE Customer CASCADE CONSTRAINTS;
DROP TABLE Product CASCADE CONSTRAINTS;
DROP TABLE Category CASCADE CONSTRAINTS;

*/
-- Create table Category
CREATE TABLE Category (
    id NUMBER(3, 0) PRIMARY KEY,
    name VARCHAR2(25) NOT NULL
);

-- Create table Product
CREATE TABLE Product (
    id NUMBER(5, 0) PRIMARY KEY,
    name VARCHAR2(100) NOT NULL,
    price NUMBER(12, 0),
    quantity NUMBER(6),
    category_id NUMBER(3, 0),
    brand VARCHAR2(50),
    FOREIGN KEY (category_id) REFERENCES Category(id)
);

-- Create table Customer
CREATE TABLE Customer (
    id NUMBER(12, 0) PRIMARY KEY,
    name VARCHAR2(30) NOT NULL,
    phone VARCHAR2(15),
    email VARCHAR2(100),
    total_points NUMBER(5),
    available_points NUMBER(5)
);

-- Create table Employee
CREATE TABLE Employee (
    id NUMBER(12, 0) PRIMARY KEY,
    name VARCHAR2(30) NOT NULL,
    position VARCHAR2(50)
);

-- Create table Role
CREATE TABLE Role (
    id NUMBER(3, 0) PRIMARY KEY,
    name VARCHAR2(30) NOT NULL
);

-- Create table AppUser
CREATE TABLE AppUser (
    id NUMBER(8, 0) PRIMARY KEY,
    username VARCHAR2(30) NOT NULL,
    password VARCHAR2(100) NOT NULL,
    employee_id NUMBER(12, 0),
    role_id NUMBER(3, 0),
    FOREIGN KEY (employee_id) REFERENCES Employee(id),
    FOREIGN KEY (role_id) REFERENCES Role(id)
);

-- Create table Invoice
CREATE TABLE Invoice (
    id NUMBER(12, 0) PRIMARY KEY,
    customer_id NUMBER(12, 0),
    employee_id NUMBER(12, 0),
    created_date DATE NOT NULL,
    total NUMBER(14, 0),
    FOREIGN KEY (customer_id) REFERENCES Customer(id),
    FOREIGN KEY (employee_id) REFERENCES Employee(id)
);

-- Create table InvoiceDetail
CREATE TABLE InvoiceDetail (
    id NUMBER(12, 0) PRIMARY KEY,
    invoice_id NUMBER(12, 0),
    product_id NUMBER(5, 0),
    quantity NUMBER(6, 0) NOT NULL,
    unit_price NUMBER(12, 0) NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES Invoice(id),
    FOREIGN KEY (product_id) REFERENCES Product(id)
);

-- Create table History
CREATE TABLE History (
    id NUMBER(12, 0) PRIMARY KEY,
    user_id NUMBER(8, 0),
    action VARCHAR2(255) NOT NULL,
    target_type VARCHAR2(100),
    target_id NUMBER(12, 0),
    timestamp TIMESTAMP NOT NULL,
    --detail_json CLOB,  -- Optional, store JSON data if needed
    FOREIGN KEY (user_id) REFERENCES AppUser(id)
);
