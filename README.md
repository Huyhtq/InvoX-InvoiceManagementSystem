
# InvoX - Invoice Management System

InvoX is a web-based invoice and inventory management system, designed for small to medium-sized businesses. Built using Spring Boot, JPA, and Oracle, it provides an intuitive UI and efficient backend for managing customers, products, invoices, and stock.

---

## 🚀 Features

- ✅ Create & manage invoices  
- 📦 Product & category management  
- 🧮 Inventory tracking  
- 👥 Role-based access control (admin, staff)  
- 📊 Basic revenue statistics & top-selling products  
- 📄 Export invoices to PDF *(coming soon!)*  

---

## 🛠️ Tech Stack

- **Backend**: Spring Boot + Spring Web + Spring Data JPA  
- **Database**: Oracle  
- **Code Helpers**: Lombok  
- **Frontend**: Bootstrap  
- **Dev Tools**: Spring Boot DevTools (for hot reload)  
- **Input Validation**: Validation API  

---

## ⚙️ Getting Started

### 📋 Prerequisites

Ensure you have the following installed:

- Java Development Kit (JDK) 17 or newer  
- Apache Maven 3.6+  
- Oracle Database (XE, Standard, or Enterprise)  
- SQL Developer / DBeaver / DataGrip or another DB tool  

---

### 📥 Installation & Run

#### 1. Clone the Repository

```bash
git clone https://github.com/your-username/InvoX-InvoiceManagementSystem.git
cd InvoX-InvoiceManagementSystem
```

#### 2. Configure Oracle Database

##### Create User/Schema:

```sql
CREATE USER invox_user IDENTIFIED BY your_password;
GRANT CONNECT, RESOURCE TO invox_user;
ALTER USER invox_user QUOTA UNLIMITED ON users;
```

##### Update Application Configuration:

Edit `src/main/resources/application.properties`:

```properties
# Oracle DataSource Configuration
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=invox_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# JPA/Hibernate Configuration for Oracle
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect
spring.jpa.show-sql=true
```

> 📝 Replace `XE`, `localhost`, `1521`, `invox_user`, and `your_password` with your actual Oracle settings.

---

### 🗃️ Initialize Database (Optional)

You can find SQL scripts in the `db/` folder. Use your preferred DB tool to connect as `invox_user` and execute the scripts to create initial tables and data.

---

### ▶️ Run the Application

From the project root directory:

```bash
./mvnw spring-boot:run
```

> ⏱️ First-time run may take a few minutes to download dependencies.

---

### 🌐 Access the Application

Once running, open your browser and go to:

```
http://localhost:8080
```

*(or your configured port)*

---

## 📌 Database Details

- Default application schema: `invox_user`  
- Default DB URL: `jdbc:oracle:thin:@localhost:1521:XE`  
- Ensure `CONNECT` and `RESOURCE` privileges are granted  
- Sample SQL scripts are located in the `db/` directory  
