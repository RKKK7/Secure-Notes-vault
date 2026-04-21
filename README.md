# 🔐 Secure Notes Vault

A full-stack web application for storing encrypted personal notes and sharing them securely via time-limited, password-protected links.

---

## 📋 Table of Contents

1. [Project Overview](#1-project-overview)
2. [Tech Stack](#2-tech-stack)
3. [Project Structure](#3-project-structure)
4. [Backend Setup](#4-backend-setup)
   - [Spring Initializr](#41-spring-initializr)
   - [Dependencies](#42-dependencies)
   - [Database Setup](#43-database-setup)
   - [Environment Variables](#44-environment-variables)
   - [application.properties](#45-applicationproperties)
   - [Running the Backend](#46-running-the-backend)
5. [Frontend Setup](#5-frontend-setup)
   - [Creating React App](#51-creating-react-app)
   - [Installing Dependencies](#52-installing-dependencies)
   - [Running the Frontend](#53-running-the-frontend)
6. [API Reference](#6-api-reference)
7. [How the App Works](#7-how-the-app-works)
8. [Security Features](#8-security-features)

---

## 1. Project Overview

**Secure Notes Vault** is a full-stack web application that allows users to store personal notes with end-to-end AES encryption, ensuring that even if the database is compromised, the data remains unreadable. It features JWT-based authentication, BCrypt password hashing, and ownership-based access control to keep each user's data completely private. The standout feature is a time-limited, password-protected note sharing system that lets users securely share notes via unique links — combining token-based access, BCrypt-hashed passwords, and automatic expiry for maximum security.

---

## 2. Tech Stack

### Backend
| Technology | Purpose |
|------------|---------|
| Java 21 | Programming language |
| Spring Boot 4.x | Backend framework |
| Spring Security 7 | Authentication & authorization |
| JWT (jjwt 0.11.5) | Stateless token-based auth |
| Spring Data JPA | Database ORM |
| Hibernate 7 | JPA implementation |
| PostgreSQL | Relational database |
| BCrypt | Password hashing |
| AES | Note content encryption |
| Lombok | Boilerplate reduction |
| HikariCP | Database connection pooling |

### Frontend
| Technology | Purpose |
|------------|---------|
| React 18 | UI framework |
| Vite | Build tool |
| React Router DOM | Client-side routing |
| Axios | HTTP client for API calls |
| Plain CSS | Styling |

---

## 3. Project Structure

```
secure-notes-vault/                          ← Spring Boot Backend
├── src/main/java/com/securenotes/secure_notes_vault/
│   ├── config/
│   │   └── CorsConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── NoteController.java
│   │   └── ShareController.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── RegisterRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── NoteRequest.java
│   │   │   ├── ShareLinkRequest.java
│   │   │   └── ShareAccessRequest.java
│   │   └── response/
│   │       ├── AuthResponse.java
│   │       ├── NoteResponse.java
│   │       ├── ShareLinkResponse.java
│   │       └── ApiResponse.java
│   ├── entity/
│   │   ├── User.java
│   │   ├── Note.java
│   │   └── ShareLink.java
│   ├── exception/
│   │   ├── ResourceNotFoundException.java
│   │   ├── UnauthorizedException.java
│   │   ├── BadRequestException.java
│   │   └── GlobalExceptionHandler.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── NoteRepository.java
│   │   └── ShareLinkRepository.java
│   ├── security/
│   │   ├── JwtUtil.java
│   │   ├── CustomUserDetailsService.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── SecurityConfig.java
│   ├── service/
│   │   ├── EncryptionService.java
│   │   ├── AuthService.java
│   │   ├── NoteService.java
│   │   └── ShareLinkService.java
│   └── SecureNotesVaultApplication.java
└── src/main/resources/
    └── application.properties

secure-notes-frontend/                       ← React Frontend
└── src/
    ├── api/
    │   └── axios.js
    ├── components/
    │   ├── NoteCard.jsx
    │   └── NoteModal.jsx
    ├── pages/
    │   ├── Login.jsx
    │   ├── Register.jsx
    │   ├── Dashboard.jsx
    │   └── SharedNote.jsx
    ├── styles/
    │   └── global.css
    ├── App.jsx
    └── main.jsx
```

---

## 4. Backend Setup

### 4.1 Spring Initializr

1. Go to **[https://start.spring.io](https://start.spring.io)**
2. Configure the project as follows:

| Field | Value |
|-------|-------|
| Project | Maven |
| Language | Java |
| Spring Boot | 3.2.x or latest stable |
| Group | `com.securenotes` |
| Artifact | `secure-notes-vault` |
| Packaging | Jar |
| Java | 21 |

3. Add the following dependencies from the Initializr UI:
   - ✅ Spring Web
   - ✅ Spring Security
   - ✅ Spring Data JPA
   - ✅ PostgreSQL Driver
   - ✅ Lombok
   - ✅ Validation
   - ✅ Spring Boot DevTools

4. Click **Generate** → Extract the zip → Open in **IntelliJ IDEA**

---

### 4.2 Dependencies

After opening the project in IntelliJ, replace the `<dependencies>` section in `pom.xml` with the following:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webmvc</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webmvc-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

After updating, click the **Maven refresh icon** in IntelliJ to download all dependencies.

---

### 4.3 Database Setup

1. Make sure **PostgreSQL** is installed and running
2. Open **pgAdmin** or **psql**
3. Create the database:

```sql
CREATE DATABASE secure_notes_db;
```

> The tables (`users`, `notes`, `share_links`) will be **automatically created** by Hibernate when you run the Spring Boot app for the first time.

---

### 4.4 Environment Variables

This project uses **IntelliJ Run Configuration Environment Variables** to keep secrets secure. Never hardcode secrets in `application.properties`.

#### Steps to set Environment Variables in IntelliJ:

1. Click the **dropdown arrow** next to the Run button (top right)
2. Select **Edit Configurations**
3. Select your `SecureNotesVaultApplication`
4. Click **Modify options** → select **Environment variables**
5. Click the **📋 icon** on the right of the Environment variables field
6. Add the following variables:

| Name | Value | Description |
|------|-------|-------------|
| `DB_USERNAME` | `postgres` | PostgreSQL username |
| `DB_PASSWORD` | `your_postgres_password` | PostgreSQL password |
| `JWT_SECRET` | `f9a83b7c6d2e1a4f8c9b7e6d5a4c3f2e1b9d8c7a6e5f4d3c2b1a09e8f7d6c5b` | JWT signing secret (min 256-bit) |
| `JWT_EXPIRATION` | `86400000` | Token expiry in ms (24 hours) |
| `ENC_SECRET` | `b7f3c9e2a1d4f6b8c2e9a7f3d1b4c6e8` | AES encryption key (exactly 16 chars) |

7. Click **OK** → **Apply** → **OK**

> ⚠️ **Important:** Never commit secrets to GitHub. Add `.env` and any secret files to `.gitignore`.

---

### 4.5 application.properties

Open `src/main/resources/application.properties` and add:

```properties
# Server
server.port=8080

# PostgreSQL Database
spring.datasource.url=jdbc:postgresql://localhost:5432/secure_notes_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Connection Pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5

# JWT
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=${JWT_EXPIRATION}

# AES Encryption
app.encryption.secret=${ENC_SECRET}
```

---

### 4.6 Running the Backend

1. Make sure PostgreSQL is running
2. Make sure environment variables are set in IntelliJ
3. Enable **Annotation Processing**:
   - `File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors`
   - ✅ Check **Enable annotation processing**
4. Click the **Run ▶️** button in IntelliJ

**Successful startup looks like:**
```
HikariPool-1 - Start completed.
Initialized JPA EntityManagerFactory for persistence unit 'default'
Tomcat started on port 8080 (http)
Started SecureNotesVaultApplication in X seconds
```

Backend is now running at: **http://localhost:8080**

---

## 5. Frontend Setup

### 5.1 Creating React App

Open **Command Prompt** and run:

```cmd
npm create vite@latest secure-notes-frontend -- --template react
```

Navigate into the project:

```cmd
cd secure-notes-frontend
```

---

### 5.2 Installing Dependencies

Install base dependencies:

```cmd
npm install
```

Install project-specific dependencies:

```cmd
npm install axios react-router-dom
```

#### Dependencies used:
| Package | Version | Purpose |
|---------|---------|---------|
| `axios` | latest | HTTP client for API calls to Spring Boot backend |
| `react-router-dom` | latest | Client-side routing between pages |

---

### 5.3 Running the Frontend

Start the development server:

```cmd
npm run dev
```

Frontend is now running at: **http://localhost:5173**

> ⚠️ Make sure the **Spring Boot backend is running** on port 8080 before using the frontend.

---

## 6. API Reference

### Auth APIs (Public — No token required)
| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/api/auth/register` | Register new user | `{ username, password }` |
| POST | `/api/auth/login` | Login, returns JWT | `{ username, password }` |

### Notes APIs (Protected — JWT required)
| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/api/notes` | Create a note | `{ title, content }` |
| GET | `/api/notes` | Get all my notes | — |
| GET | `/api/notes/{id}` | Get single note | — |
| PUT | `/api/notes/{id}` | Update note | `{ title, content }` |
| DELETE | `/api/notes/{id}` | Delete note | — |

### Share APIs
| Method | Endpoint | Auth | Description | Request Body |
|--------|----------|------|-------------|--------------|
| POST | `/api/share/create/{noteId}` | ✅ JWT | Generate share link | `{ password, expiryMinutes }` |
| POST | `/api/share/{token}/verify` | ❌ Public | Access shared note | `{ password }` |

### Request Headers (for protected routes)
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json
```

### Standard Response Format
```json
{
    "success": true,
    "message": "Operation successful",
    "data": { }
}
```

### Error Response Format
```json
{
    "success": false,
    "message": "Error description",
    "data": null
}
```

---

## 7. How the App Works

### User Registration & Login
1. User registers with a username and password
2. Password is **BCrypt hashed** before storing in database
3. On successful registration/login, a **JWT token** is returned
4. The token is stored in browser `localStorage`
5. Every subsequent API request includes the token in the `Authorization` header

### Note Storage with Encryption
1. User creates a note with title and content
2. Before saving to the database, the content is **AES encrypted**
3. The database stores only the encrypted content — never plain text
4. When notes are fetched, the content is **AES decrypted** before sending to the user
5. Each note is linked to the authenticated user — users can only access their own notes

### Secure Note Sharing
1. User selects a note and clicks the share button
2. User sets a **password** and **expiry time** (in minutes)
3. Backend generates a unique **UUID token** and stores:
   - The token
   - BCrypt-hashed password
   - Expiry timestamp
   - Reference to the note
4. A share URL is returned: `http://localhost:5173/share/{token}`
5. Receiver opens the link → enters the password
6. Backend validates:
   - Token exists in database
   - Link has not expired (auto-deleted if expired)
   - Password matches the BCrypt hash
7. If all checks pass → decrypted note content is returned **(read-only)**

### Complete Request Lifecycle
```
Browser Request
      ↓
JwtAuthenticationFilter  ← Validates JWT on every request
      ↓
Spring Security          ← Checks route permissions
      ↓
Controller               ← Receives and validates request
      ↓
Service                  ← Business logic + encryption
      ↓
Repository               ← Database operations
      ↓
PostgreSQL               ← Persistent storage
      ↓
Response flows back up → JSON response to browser
```

---

## 8. Security Features

| Feature | Implementation |
|---------|---------------|
| Password Hashing | BCrypt (one-way, salted) |
| Authentication | JWT (stateless, 24hr expiry) |
| Note Encryption | AES with Base64 encoding |
| Ownership Validation | Every note query filters by authenticated user |
| Share Link Security | UUID token + BCrypt password + timestamp expiry |
| Route Protection | Spring Security filter chain |
| Input Validation | Jakarta Validation annotations |
| Error Handling | Global exception handler with proper HTTP status codes |
| Secret Management | Environment variables (never hardcoded) |
| CORS | Configured to allow only frontend origin |

---

## 🚀 Quick Start Checklist

### Backend
- [ ] PostgreSQL installed and running
- [ ] `secure_notes_db` database created
- [ ] Project generated from Spring Initializr
- [ ] Dependencies added to `pom.xml`
- [ ] Maven dependencies downloaded
- [ ] Environment variables set in IntelliJ
- [ ] `application.properties` configured
- [ ] Annotation processing enabled
- [ ] Spring Boot app running on port 8080

### Frontend
- [ ] Node.js installed
- [ ] React app created with Vite
- [ ] `axios` and `react-router-dom` installed
- [ ] Source files created as per project structure
- [ ] Frontend running on port 5173
- [ ] Backend running simultaneously

---

*Secure Notes Vault — Built with Spring Boot 4.x + React + PostgreSQL*
