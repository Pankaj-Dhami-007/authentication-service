# 🔐 AuthService - Secure Authentication Microservice

A **robust, scalable, and production-ready authentication service** built using **Java & Spring Boot**, supporting both traditional and modern login flows including **email-password with JWT** and **mobile-based OTP authentication**.

---

## 🚀 Features

- ✅ **User Registration** with email verification via OTP (JavaMailSender)
- 🔐 **Login** with email & password using **JWT Token**
- 🧑‍⚖️ **Role-Based Access Control** (e.g., USER, ADMIN)
- 📱 **Mobile OTP Login** using **Twilio**
- 📬 **Email Verification** using **Java Mail Sender**
- 💾 **MySQL Database** for user data
- ⚡ **Redis** for temporary OTP and session storage
- 🔁 **Scalable & Stateless JWT-based Auth**
- 🧩 **OpenFeign Client Integration** for internal microservice communication
- 📡 **Message Service Integration** for event-driven architecture
- ☁️ **Built with scalability, performance, and clean architecture in mind**

---

## 🛠️ Tech Stack

| Layer              | Technology            |
|-------------------|------------------------|
| Language           | Java 17               |
| Framework          | Spring Boot           |
| Security           | Spring Security, JWT  |
| API Client         | OpenFeign             |
| Database           | MySQL                 |
| Cache              | Redis                 |
| Messaging          | MessageService        |
| Email Service      | Java Mail Sender      |
| SMS Service        | Twilio                |
| Build Tool         | Maven                 |

---

## 📌 Authentication Flows

### 🔹 1. Sign-Up (Email Based)
- User registers with email & password
- OTP is sent to email (JavaMailSender)
- After OTP verification, user is activated

### 🔹 2. Login with Email & Password
- Validated with MySQL-stored credentials
- On success, JWT token is generated & returned
- Role-based access control is applied

### 🔹 3. Login with Mobile OTP
- OTP is generated and stored in **Redis**
- Sent via **Twilio SMS**
- Verified before issuing access token

---

## 🧠 Architecture Highlights

- 🧱 **Modular Code Structure**
- 📡 **Microservices-Friendly via Feign Clients**
- 🔐 **Stateless Security using JWT**
- ⚙️ **Highly Configurable with application.yml**
- 🧪 Unit & integration tested (assumed)

---


> Clean separation of concerns for maintainability and scalability.

---

## 🧑‍💻 Author

👤 **Pankaj Dhami**  
🌐 GitHub: [@Pankaj-Dhami-007](https://github.com/Pankaj-Dhami-007)  
📫 Email: pankajdhami811@gmail.com

---

## 🏁 Final Words

> This project demonstrates real-world authentication needs, including **OTP, email verification, and JWT** security. It's designed to scale and integrate with other microservices cleanly using **OpenFeign**, making it ideal for modern backend systems.

---



