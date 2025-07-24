# ✅ Java 8 Lambda Expressions – Feature List

Lambda expressions in Java 8 allow concise function implementations without the need for verbose anonymous classes.

| #  | Use Case                         | Description                                                    | Example (Lambda Style)                                                                 |
|----|----------------------------------|----------------------------------------------------------------|----------------------------------------------------------------------------------------|
| 1. | **Runnable Implementation**      | Execute code in a thread or background task                    | `Runnable r = () -> System.out.println("Running");`                                    |
| 2. | **Comparator for Sorting**       | Sort collections by field or logic                             | `list.sort((a, b) -> a.getName().compareTo(b.getName()));`                             |
| 3. | **Filtering a Collection**       | Remove unwanted elements from a collection                     | `users.stream().filter(User::isActive).collect(...)`                                   |
| 4. | **Mapping a Collection**         | Transform one value to another (e.g., object → field)          | `posts.stream().map(Post::getTitle).collect(...)`                                      |
| 5. | **Combining Stream Operations**  | Chain filter, map, and forEach                                 | `posts.stream().filter(...).map(...).forEach(...)`                                     |
| 6. | **ForEach Loop**                 | Iterate and execute a lambda for each element                  | `list.forEach(u -> System.out.println(u.getName()));`                                  |
| 7. | **Method References**            | Shorthand for lambdas calling a single method                  | `users.sort(Comparator.comparing(User::getName));`                                     |
| 8. | **Function Interface Usage**     | Replace custom logic with `Function`, `Predicate`, etc.        | `Function<User, String> nameFn = u -> u.getName();`                                    |
| 9  | **Predicate Filtering**          | Use lambdas with `Predicate<T>` to encapsulate conditions      | `Predicate<Post> published = p -> p.isPublished();`                                     |
| 10 | **Grouping with Collectors**     | Count, sum, or group items using lambda grouping               | `groupingBy(p -> p.getAuthor().getName(), counting())`                                 |
| 11 | **Optional ifPresent()**         | Handle presence of value using lambda                          | `optional.ifPresent(value -> System.out.println(value));`                              |
| 12 | **Custom Functional Interfaces** | Define your own `@FunctionalInterface`                         | `MyFn fn = x -> x + 1;`                                                                |

