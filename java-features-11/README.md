# Java 11 Overview

## 🗓️ Release Info

- **Version**: Java SE 11 (JDK 11)
- **Release Date**: **September 25, 2018**
- **LTS**: Yes (Long-Term Support)
- **Maintained by**: Oracle, OpenJDK, AdoptOpenJDK (now Adoptium)

---

## 🚀 Why Java 11 Matters

Java 11 was the **first LTS version after Java 8**, making it the go-to upgrade path for most enterprises. It removed deprecated features, modularized the runtime, and added modern improvements.

---

## ⭐ Key Goals

- Provide a **stable LTS** after the Java 9/10 feature bursts
- Clean up the JDK with **removal of legacy APIs**
- Enable more **modular, lightweight deployments**
- Add modern APIs for HTTP, strings, and more

---

## 🧩 Major Features

| Feature                                         | Description                                       |
| ----------------------------------------------- | ------------------------------------------------- |
| **LTS Version**                                 | First long-term support version since Java 8      |
| **HTTP Client API**                             | Standardized modern HTTP client (`java.net.http`) |
| **Local-Variable Syntax for Lambda Parameters** | `var` allowed in lambda params                    |
| **New String Methods**                          | `isBlank()`, `lines()`, `strip()`, `repeat()`     |
| **Files.readString()**                          | Read file content as string in one line           |
| **Optional.isEmpty()**                          | Added for better Optional handling                |
| **Nest-Based Access Control**                   | Better access control for nested classes          |
| **Flight Recorder**                             | Lightweight monitoring tool integrated            |
| **ZGC (Experimental)**                          | Low-latency garbage collector                     |

---

## ❌ Removed Components

- **Java EE modules removed**: `javax.xml.bind`, `javax.activation`, `javax.annotation`, etc.
- **Applets and Java Web Start** deprecated and removed
- **JavaFX** no longer bundled (now separate)

---

## 📦 Adoption

- Most common target for Java 8 migrations
- Still widely used in enterprise and cloud environments
- Stable for production use with long-term support

---

## 🔗 Related

- [Java 11 API Documentation](https://docs.oracle.com/en/java/javase/11/docs/api/)
- [Java SE 11 Release Notes](https://www.oracle.com/java/technologies/javase/jdk11-relnotes.html)
