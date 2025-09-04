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
| **Flight Recorder**                   | Low-overhead data collection for profiling               |
| **ZGC (Experimental)**                | Low-latency garbage collector                            |
| **Nest-Based Access Control**         | Better encapsulation for nested classes                  |
| **Unicode 10 Support**                | Enhanced Unicode character support                       |
| **Removal of Java EE & CORBA**        | Modular and streamlined JDK                              |
| **Launch Single-File Source Code**    | Run `.java` files without compiling manually              |

---

## 📦 Adoption

- **First LTS** since Java 8
- Default for many enterprises upgrading from Java 8
- Used widely in modern container-based deployments

---

## 🔗 Related

- [Java 11 API Documentation](https://docs.oracle.com/en/java/javase/11/docs/api/)
- [Java SE 11 Release Notes](https://www.oracle.com/java/technologies/javase/11-relnote.html)

## 📚 Full Feature Table with Example Ideas

| Feature                             | Description                                              | Example Ideas (Best Related)                                  | Major Feature? |
|-------------------------------------|----------------------------------------------------------|----------------------------------------------------------------|----------------|
| **Local-Variable Syntax for Lambda**| Use `var` in lambda parameters                            | Use `var` in `(var x, var y) -> x + y`                         | ❌ No          |
| **HttpClient (Standardized)**       | Modern async and reactive HTTP client                     | Send async requests, handle response bodies                    | ✅ Yes         |
| **Flight Recorder**                 | JVM profiling tool for performance monitoring             | Capture JVM events, analyze performance                        | ✅ Yes         |
| **ZGC (Experimental)**              | Low-latency scalable garbage collector                    | Configure and benchmark GC behavior                            | ❌ No          |
| **Nest-Based Access Control**       | Improves access between nested classes                    | Use nested classes with shared access                          | ❌ No          |
| **Unicode 10 Support**              | Enhanced Unicode character support                        | Use extended emoji or characters                               | ❌ No          |
| **Removal of Java EE & CORBA**      | Removed outdated modules                                  | Verify removed packages are no longer accessible               | ✅ Yes         |
| **Launch Single-File Source Code**  | Run `.java` files directly                                | `java HelloWorld.java` without compiling                       | ✅ Yes         |
