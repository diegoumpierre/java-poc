# Java 11 Overview

## 🗓️ Release Info

- **Version**: Java SE 11 (JDK 11)
- **Release Date**: **September 25, 2018**
- **LTS**: Yes (Long-Term Support)
- **Maintained by**: Oracle, OpenJDK

---

## 🚀 Why Java 11 Matters

Java 11 is the first LTS version after Java 8 and marked the start of Oracle's 6-month release cadence. It removed deprecated features and streamlined the JDK.

---

## ⭐ Key Goals

- Provide a **stable LTS** release
- Remove outdated and deprecated modules (e.g., Java EE, CORBA)
- Introduce **modern language features**
- Improve **performance** and **container awareness**
- Simplify deployment and reduce footprint

---

## 🧩 Major Features

| Feature                               | Description                                              |
|---------------------------------------| -------------------------------------------------------- |
| **.Local-Variable Syntax for Lambda** | `var` can be used in lambda parameters                    |
| **.HttpClient (Standardized)**        | Modern HTTP client with async and reactive capabilities  |
| **.Flight Recorder**                  | Low-overhead data collection for profiling               |
| **.ZGC (Experimental)**               | Low-latency garbage collector                            |
| **.Nest-Based Access Control**        | Better encapsulation for nested classes                  |
| **.Unicode 10 Support**               | Enhanced Unicode character support                       |
| **.Removal of Java EE & CORBA**       | Modular and streamlined JDK                              |
| **.Launch Single-File Source Code**   | Run `.java` files without compiling manually              |

---

## 📦 Adoption

- **First LTS** since Java 8
- Default for many enterprises upgrading from Java 8
- Used widely in modern container-based deployments

---

## 🔗 Related

- [Java 11 API Documentation](https://docs.oracle.com/en/java/javase/11/docs/api/)
- [Java SE 11 Release Notes](https://www.oracle.com/java/technologies/javase/11-relnote.html)

## Removal of Java EE & CORBA
❌ Removed Java EE modules:

java.xml.bind (JAXB)
java.xml.ws (JAX-WS)
java.activation (JAF)
java.transaction (JTA)
java.corba (CORBA)
java.xml.ws.annotation
java.se.ee (aggregated all above)