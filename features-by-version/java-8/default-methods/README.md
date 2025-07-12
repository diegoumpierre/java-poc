# Default Methods in Java 8 – What Is the Goal?

## 🧩 Introduction

Java 8 introduced a powerful new feature called **default methods**, which allow interfaces to include **concrete method implementations**.

This feature was designed to address long-standing limitations in Java’s type system and make the language more flexible and forward-compatible.

---

## 🎯 Main Goals of Default Methods

### 1. ✅ Interface Evolution Without Breaking Changes

Before Java 8, if you added a new method to an interface, **all implementing classes would break** unless they implemented the new method.

With default methods:
- Interfaces can be extended with new methods
- Default behavior can be provided
- **Existing code continues to work without modification**

> **Example:**
```java
interface MyInterface {
    default void log(String msg) {
        System.out.println("LOG: " + msg);
    }
}
```

### 2. ✅ Enable Functional Programming APIs
To support Java 8’s new Streams, lambda expressions, and functional interfaces, many existing interfaces (like Iterable, List, Map) needed new methods.

Default methods made this possible without breaking backward compatibility.
> **Example in Iterable:**
```java
default void forEach(Consumer<? super T> action) {
    for (T t : this) {
        action.accept(t);
    }
}
```
### 3. 🔁 Multiple Inheritance of Behavior
Default methods allow a form of code reuse similar to traits or mixins in other languages. You can implement multiple interfaces with default behavior.

When conflicts occur, the implementing class must resolve them explicitly.

> **Example:**
```java
interface A {
    default void greet() { System.out.println("Hello from A"); }
}

interface B {
    default void greet() { System.out.println("Hello from B"); }
}

class C implements A, B {
    @Override
    public void greet() {
        A.super.greet();  // Resolve conflict explicitly
    }
}

```
