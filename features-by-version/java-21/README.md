# Java 21 Overview

## 🗓️ Release Info

- **Version**: Java SE 21 (JDK 21)
- **Release Date**: **September 19, 2023**
- **LTS**: Yes (Long-Term Support)
- **Maintained by**: Oracle, OpenJDK

---

## 🚀 Why Java 21 Matters

Java 21 is a powerful LTS release with many finalized language features and a mature platform for modern development. It solidifies efforts from versions 12 to 20.

---

## ⭐ Key Goals

- Finalize **preview features** introduced in prior versions
- Improve **developer productivity** with modern language constructs
- Enhance **performance**, **security**, and **native interop**
- Deliver on the long-term vision of **simplified Java**

---

## 🧩 Major Features

| Feature                             | Description                                            |
| ----------------------------------- | ------------------------------------------------------ |
| **Pattern Matching for switch**     | Type-safe and expressive switch cases                 |
| **Record Patterns**                 | Destructure record values inline                      |
| **String Templates (Preview)**      | Embedded expressions in string literals               |
| **Sequenced Collections**           | New collection interfaces with order-sensitive access |
| **Virtual Threads**                | Lightweight threads for scalable concurrency           |
| **Scoped Values**                   | Thread-local-like values for virtual threads          |
| **Unnamed Classes and Instance Main Methods (Preview)** | Simplify entry-point programs       |
| **Foreign Function & Memory API**   | Stable support for calling native code                |

---

## 📦 Adoption

- Recommended LTS for all new Java development
- Adopted for cloud-native and high-performance systems
- Aligns with modern concurrency models

---

## 🔗 Related

- [Java 21 API Documentation](https://docs.oracle.com/en/java/javase/21/docs/api/)
- [Java SE 21 Release Notes](https://www.oracle.com/java/technologies/javase/21-relnote.html)

## 📚 Full Feature Table with Example Ideas

| Feature                                          | Description                                              | Example Ideas (Best Related)                                  | Major Feature? |
|--------------------------------------------------|----------------------------------------------------------|----------------------------------------------------------------|----------------|
| **Pattern Matching for switch**                  | Type-safe, expressive switch cases                       | Use type + guard patterns in switch                            | ✅ Yes         |
| **Record Patterns**                              | Deconstruct record objects                               | `case User(String name, int age)` in switch                    | ✅ Yes         |
| **String Templates (Preview)**                   | Embed expressions in strings                             | `STR."Hello \{name}!"`                                         | ✅ Yes         |
| **Sequenced Collections**                        | New interfaces with defined element order                | Use `SequencedSet`, `SequencedMap`                             | ✅ Yes         |
| **Virtual Threads**                              | Lightweight threads for massive concurrency              | Replace thread pools with `Executors.newVirtualThreadPerTaskExecutor()` | ✅ Yes  |
| **Scoped Values**                                | Immutable values tied to virtual thread scopes           | Replace ThreadLocal in virtual threads                         | ❌ No          |
| **Unnamed Classes and Instance Main Methods**    | Simplify writing small programs                          | Create programs without class names or main boilerplate        | ❌ No          |
| **Foreign Function & Memory API**                | Stable access to native code and memory                  | Call C libraries using `Linker` and `MemorySegment`            | ✅ Yes         |
