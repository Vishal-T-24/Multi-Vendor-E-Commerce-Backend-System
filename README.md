# Multi-Vendor E-Commerce Backend with RBAC

A fully custom backend system built in pure Core Java without 
any frameworks. Includes a hand-built HTTP router, JWT 
authentication, and a 3-tier role-based access system.

## Tech Stack
- Core Java (No frameworks)
- JDBC
- PostgreSQL
- Redis (JedisPool)
- JWT + BCrypt
- HikariCP

## Features
- Custom HTTP router built from scratch with dynamic path variables
- 3-tier Role-Based Access Control (Admin, Seller, Buyer)
- JWT + BCrypt authentication
- JedisPool session caching for high-concurrency performance
- PostgreSQL schema with foreign keys and cascading deletes

## How to Run
1. Clone the repository
   git clone https://github.com/Vishal-T-24/Multi-Vendor-E-Commerce-Backend-System.git
2. Set up PostgreSQL and Redis locally
3. Update DB credentials in the config file
4. Compile and run
   javac Main.java
   java Main
5. Test APIs using Postman on http://localhost:8080

## API Testing
All endpoints tested and validated using Postman.
