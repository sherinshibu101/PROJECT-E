# 🏪 EMART Invoice System

A complete invoice management system built with **Spring Boot** and **React** for small to medium businesses.

![EMART Dashboard](https://img.shields.io/badge/Status-Production%20Ready-brightgreen)
![Java](https://img.shields.io/badge/Java-17+-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0+-green)
![React](https://img.shields.io/badge/React-18+-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue)

---

>  **Deployment Update:** The application is fully deployed, but live access via [EMART Invoices](https://emartinvoices.web.app/) is temporarily paused due to Google Cloud Run credit limits. 
> 
>  **CI/CD Pipeline:** You can check out the basic CI/CD pipeline automation setup over in my secondary repository: [sherinshibu1001/EMART](https://github.com/sherinshibu1001/EMART).
> 
> *Note: This is my very first full-stack project! It was built with a real-world retail shop in mind, and I had an absolute blast putting all these moving pieces together.*

---

## ✨ Features

### 🔐 Authentication
- Secure login system
- Protected routes
- Session management

### 👥 Customer Management
- Add and edit customers
- Search functionality
- Customer details management

### 📦 Product Management
- Product catalog management
- Inventory tracking
- Price management

### 📋 Order Management
- Create orders with multiple items
- Order status tracking
- Automatic total calculation

### 📄 Invoice System
- Generate professional PDF invoices
- Email invoices to customers
- Download invoices
- Professional formatting

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 1. Clone Repository
```bash
git clone https://github.com/yourusername/emart-invoice-system.git
cd emart-invoice-system
```

### 2. Security Setup (IMPORTANT!)
```bash
# Read the security guide first
cat SECURITY_SETUP.md

# Copy and configure application.properties
cp invoice-generator/src/main/resources/application.properties.template invoice-generator/src/main/resources/application.properties

# Edit application.properties with your database and email settings
```

### 3. Database Setup
```bash
# Create database
mysql -u root -p
CREATE DATABASE emart_invoices;

# Import schema
mysql -u root -p emart_invoices < database/setup.sql
```

### 4. Backend Setup
```bash
cd invoice-generator
mvn clean install
mvn spring-boot:run
```

### 5. Frontend Setup
```bash
cd invoice-frontend
npm install
npm start
```

### 6. Access Application
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Login: admin / emartadmin@123

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React.js      │    │   Spring Boot   │    │     MySQL       │
│   Frontend      │◄──►│    Backend      │◄──►│    Database     │
│   (Port 3000)   │    │   (Port 8080)   │    │   (Port 3306)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Tech Stack
- **Frontend**: React.js, Bootstrap, Axios
- **Backend**: Spring Boot, Spring Data JPA, Spring Mail
- **Database**: MySQL
- **PDF Generation**: iText
- **Email**: JavaMail with Gmail SMTP

## 📁 Project Structure

```
EMART/
├── invoice-frontend/          # React frontend
│   ├── src/
│   │   ├── components/       # Reusable components
│   │   ├── pages/           # Page components
│   │   ├── services/        # API services
│   │   └── styles/          # CSS files
│   └── public/
├── invoice-generator/         # Spring Boot backend
│   ├── src/main/java/
│   │   └── com/emart/invoice/
│   │       ├── controller/   # REST controllers
│   │       ├── entity/       # JPA entities
│   │       ├── repository/   # Data repositories
│   │       └── service/      # Business logic
│   └── src/main/resources/
├── database/                  # Database scripts
└── docs/                     # Documentation
```

## 🔒 Security

- Environment variables for sensitive data
- Secure password hashing
- CORS configuration
- Input validation
- SQL injection prevention

**Important**: Never commit sensitive data to version control. Use the provided templates and environment variables.

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


**Made with ❤️ for small businesses**
