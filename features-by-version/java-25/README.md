# Java 25 Overview

## 🗓️ Release Info

- **Version**: Java SE 25 (JDK 25)
- **Release Date**: **September 2025**
- **LTS**: No (Feature Release)
- **Maintained by**: Oracle, OpenJDK

---

## 🚀 Why Java 25 Matters

Java 25 continues the rapid evolution of the platform, introducing new language features, performance improvements, and enhanced developer ergonomics. It builds on the foundation of previous releases, focusing on modern programming paradigms and native integration.

---

## ⭐ Key Goals

- Advance **native interop** and memory management
- Expand **pattern matching** and data-oriented programming
- Improve **performance** and **scalability** for cloud-native workloads
- Simplify **developer experience** with new language constructs

---

## 🧩 Major Features

| Feature                                 | Description                                              |
|-----------------------------------------| -------------------------------------------------------- |
| **.Pattern Matching for Collections**   | Match and destructure collections in switch/case         |
| *.*Value Objects (Preview)**            | Immutable, identity-less objects for performance         |
| **.String Templates (Stable)**          | Embedded expressions in string literals                  |
| **Universal Generics**                  | Enhanced generics for primitives and value types         |
| **Structured Concurrency Enhancements** | Improved APIs for concurrent task management             |
| **Foreign Function & Memory API v2**    | Expanded native interop and memory safety                |
| **Scoped Threads**                      | Fine-grained thread scoping for concurrency              |
| **JEP: Data Classes**                   | Concise syntax for data-centric classes                  |

---

## 📦 Adoption

- Recommended for projects needing cutting-edge language features
- Ideal for high-performance, cloud-native, and data-centric applications
- Aligns with modern concurrency and native integration models

---

## 🔗 Related

- [Java 25 API Documentation](https://docs.oracle.com/en/java/javase/25/docs/api/)
- [Java SE 25 Release Notes](https://www.oracle.com/java/technologies/javase/25-relnote.html)

## 📚 Full Feature Table with Example Ideas

| Feature                                        | Description                                              | Example Ideas (Best Related)                                  | Major Feature? |
|------------------------------------------------|----------------------------------------------------------|----------------------------------------------------------------|----------------|
| **Pattern Matching for Collections**           | Match and destructure lists, sets, maps                  | `case List(String name, int age)` in switch                    | ✅ Yes         |
| **Value Objects (Preview)**                    | Identity-less, immutable objects for performance         | `value class Point(int x, int y)`                              | ✅ Yes         |
| **String Templates (Stable)**                  | Embed expressions in strings                             | `STR."Total: $\{amount}"`                                    | ✅ Yes         |
| **Universal Generics**                         | Generics for primitives and value types                  | `List<int>`, `Optional<double>`                                | ✅ Yes         |
| **Structured Concurrency Enhancements**        | Improved concurrent task management                      | `try (var scope = StructuredTaskScope...)`                     | ✅ Yes         |
| **Foreign Function & Memory API v2**           | Expanded native interop                                  | Call C libraries with enhanced safety                          | ✅ Yes         |
| **Scoped Threads**                             | Fine-grained thread scoping                              | Limit thread lifetime and resource scope                       | ❌ No          |
| **JEP: Data Classes**                          | Concise syntax for data-centric classes                  | `data class User(String name, int age)`                        | ✅ Yes         |

---

## 🧑‍💻 Pattern Matching for Collections Example

See [`PatternMatchingForCollections.java`](java-examples/src/main/java/br/dev/PatternMatchingForCollections.java) for a real-world example using collection pattern matching:

```java
public static String describe(Object obj) {
    return switch (obj) {
        case List(Post p1, Post p2) when p1.isPublished() && p2.isDraft() -> "Published and Draft Posts";
        case List(User u1, User u2) -> "Two users: " + u1.getName() + ", " + u2.getName();
        case Map(String key, User user) when user.getPosts().size() > 0 -> "User with posts: " + key;
        default -> "Unknown collection";
    };
}
```

**Demo output:**
```
Published and Draft Posts
Two users: John Doe, Jane Doe
User with posts: admin
Unknown collection
```

This demonstrates collection pattern matching in a switch, leveraging Java 25's advanced pattern matching capabilities.

