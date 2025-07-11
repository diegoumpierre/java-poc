In Java 8, the `@FunctionalInterface` annotation is used to indicate that an **interface is intended to be a functional interface**—that is, it must have **exactly one abstract method**.

### ✅ Why Use `@FunctionalInterface`

- **Compiler Enforcement**: Ensures the interface has only one abstract method. If more are added accidentally, the compiler will show an error.
- **Documentation**: Clearly communicates the intent that the interface is meant for use with **lambda expressions** or **method references**.
- **Support for Functional Programming**: Functional interfaces enable a more functional programming style in Java by allowing lambda expressions.

#### Invalid Functional Interface (Compiler Error)

```java
@FunctionalInterface
public interface InvalidInterface {
    void methodOne();
    void methodTwo(); // ❌ This causes a compiler error
}
```

### 🧠 Behind the Scenes

Even if you don’t add `@FunctionalInterface`, any interface with a single abstract method is still a functional interface and can be used in lambdas. However, the annotation provides **safety and clarity**.

---

### 🧩 Related Built-in Functional Interfaces (from `java.util.function`)

| Interface     | Description                             |
|---------------|-----------------------------------------|
| `Function<T,R>` | Accepts one argument, returns a result |
| `Predicate<T>` | Tests a condition and returns boolean   |
| `Consumer<T>`  | Performs an action, returns nothing     |
| `Supplier<T>`  | Supplies a value without input          |

---