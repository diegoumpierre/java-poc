# ✅ Java 8 Functional Interfaces – Feature List

Java 8 introduced functional interfaces to support lambda expressions. A **functional interface** has exactly **one abstract method**, and Java provides many built-in types to help with everyday logic.

| #  | Interface             | Purpose                                     | Example (Lambda Style)                                                                 |
|----|-----------------------|---------------------------------------------|----------------------------------------------------------------------------------------|
| 1. | **`Function<T,R>`**    | Takes `T`, returns `R`                      | `Function<User, String> getName = u -> u.getName();`                                   |
| 2. | **`Predicate<T>`**     | Takes `T`, returns `boolean`                | `Predicate<Post> isPublished = p -> p.isPublished();`                                  |
| 3. | **`Consumer<T>`**      | Takes `T`, returns `void`                   | `Consumer<User> printName = u -> System.out.println(u.getName());`                     |
| 4. | **`Supplier<T>`**      | Takes nothing, returns `T`                  | `Supplier<User> newUser = () -> new User("Anonymous");`                                |
| 5. | **`BiFunction<T,U,R>`**| Takes `T` and `U`, returns `R`              | `BiFunction<User, Post, String> describe = (u, p) -> u.getName() + ": " + p.getTitle();`|
| 6. | **`BiPredicate<T,U>`** | Takes `T` and `U`, returns `boolean`        | `BiPredicate<User, Post> isOwner = (u, p) -> p.getAuthor().equals(u);`                 |
| 7. | **`UnaryOperator<T>`** | Function where input/output are same type   | `UnaryOperator<String> toUpper = s -> s.toUpperCase();`                                |
| 8. | **`BinaryOperator<T>`**| Binary version of `UnaryOperator<T>`        | `BinaryOperator<Integer> sum = (a, b) -> a + b;`                                        |
| 9. | **Custom Interface**   | Your own single-abstract method interface   | `@FunctionalInterface interface Validator<T> { boolean validate(T t); }`<br>`Validator<User> v = u -> u.isActive();`|
