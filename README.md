# InvoX - Invoice Management System

**InvoX** is a web-based invoice and inventory management system, designed for small to medium-sized businesses. Built using **Spring Boot**, **JPA**, and **MySQL**, it provides an intuitive UI and efficient backend for managing customers, products, invoices, and stock.

---

## 🚀 Features

- ✅ Create & manage invoices
- 📦 Product & category management
- 🧮 Inventory tracking
- 👥 Role-based access control (admin, staff)
- 📊 Basic revenue statistics & top-selling products
- 📄 Export invoices to PDF (coming soon!)

---

## 🛠️ Tech Stack

- Spring Boot + Spring Web + Spring Data JPA
- MySQL / Oracle (configurable)
- Lombok for cleaner code
- Bootstrap (frontend)
- Spring Boot DevTools for hot reload
- Validation API for input control

---

## ⚙️ Getting Started

```
git clone https://github.com/your-username/InvoX-InvoiceManagementSystem.git
cd InvoX-InvoiceManagementSystem
./mvnw spring-boot:run
```
🔧 Update your src/main/resources/application.properties with your database settings.

---

## 📌 Database

- You can find sample SQL scripts to initialize tables under db/ folder.
- Default schema: invox_db
- User: root
- Password: your_password
