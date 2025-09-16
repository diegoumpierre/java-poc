# Java 17 Overview

## 🗓️ Release Info

- **Version**: Java SE 17 (JDK 17)
- **Release Date**: **September 14, 2021**
- **LTS**: Yes (Long-Term Support)
- **Maintained by**: Oracle, OpenJDK

---

## 🚀 Why Java 17 Matters

Java 17 is the second LTS after Java 11 and includes many enhancements and finalized preview features introduced in earlier versions.

---

## ⭐ Key Goals

- Deliver **modern language enhancements**
- Finalize several **preview and incubator features**
- Provide improved **performance and reliability**
- Prepare the ground for further **pattern matching** and **records**

---

## 🧩 Major Features

| Feature                                       | Description                                             |
|-----------------------------------------------| ------------------------------------------------------- |
| **.Sealed Classes**                           | Control which classes can extend or implement others    |
| **.Pattern Matching for `instanceof`**        | Simplifies type checks and casting                  |
| **.Switch Expressions (Preview)**             | Allows expressions with switch statements               |
| **Text Blocks**                               | Multi-line string literals                              |
| **Records**                                   | Immutable data carriers with minimal syntax             |
| **Foreign Function & Memory API (Incubator)** | Interact with native code                  |
| **New macOS Rendering Pipeline**              | Enhanced performance on Apple systems                   |
| **Deprecations and Removals**                 | Deprecated Applet API, removed experimental features    |

---

## 📦 Adoption

- Seen as a modern LTS replacement for Java 11
- Widely adopted in new enterprise and cloud-native applications

---

## 🔗 Related

- [Java 17 API Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/)
- [Java SE 17 Release Notes](https://www.oracle.com/java/technologies/javase/17-relnote.html)

## 📚 Full Feature Table with Example Ideas

| Feature                             | Description                                              | Example Ideas (Best Related)                                  | Major Feature? |
|-------------------------------------|----------------------------------------------------------|----------------------------------------------------------------|----------------|
| **Sealed Classes**                  | Restrict which classes can extend a type                 | Create sealed hierarchies like `sealed interface Shape`        | ✅ Yes         |
| **Pattern Matching for instanceof** | Simplify type checks and casting                         | Use `if (obj instanceof String s)`                             | ✅ Yes         |
| **Switch Expressions (Preview)**    | Use switch as expression with return value               | `switch (value) -> case 1 -> "one"`                            | ✅ Yes         |
| **Text Blocks**                     | Multi-line string literals                               | Format JSON/XML easily in strings                              | ✅ Yes         |
| **Records**                         | Immutable data carriers with minimal syntax              | Replace DTOs with `record User(String name, int age)`          | ✅ Yes         |
| **Foreign Function & Memory API**   | Access native code and memory safely                     | Call C functions from Java                                     | ✅ Yes         |
| **New macOS Rendering Pipeline**    | Improved graphics performance                            | UI rendering improvements (e.g., JavaFX)                       | ❌ No          |
| **Deprecations and Removals**       | Remove legacy/unused APIs                                | Removed Applet API                                             | ❌ No          |
