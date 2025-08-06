# ✅ Java 8 – Streams API Features (Table Format)

| #  | Feature Category          | Description                                                                                  | Example / Notes                                                                 |
|----|---------------------------|----------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------|
| 1. | Stream Creation           | Create a stream from collections, arrays, builders, or generators                           | `list.stream()`, `Stream.of(...)`, `Stream.generate(...)`                        |
| 2. | Lazy Evaluation           | Intermediate operations are only evaluated when a terminal operation is called              | Execution is deferred until `collect()`, `forEach()`, etc.                      |
| 3  | Intermediate Operations   | Transform or filter data; return a new stream                                                | `filter()`, `map()`, `flatMap()`, `distinct()`, `sorted()`, `limit()`, `skip()` |
| 4  | Terminal Operations       | Produce a result or side-effect, and end the stream pipeline                                | `collect()`, `forEach()`, `reduce()`, `count()`, `findFirst()`                  |
| 5  | Collectors Utility        | Use `Collectors` class for reduction to collections or summaries                            | `toList()`, `groupingBy()`, `partitioningBy()`, `joining()`, `counting()`       |
| 6  | Parallel Streams          | Stream operations executed in parallel for performance gains                                | `list.parallelStream().map(...).collect(...)`                                   |
| 7  | Primitive Streams         | Special streams for primitives to avoid boxing                                               | `IntStream`, `LongStream`, `DoubleStream`, `IntStream.range(1, 10)`             |
| 8  | Stream Pipelining         | Chain multiple operations into a readable and efficient pipeline                            | `stream().filter().map().collect(...)`                                          |
| 9  | Infinite Streams          | Streams that generate unlimited data, usually limited manually                              | `Stream.generate(...)`, `Stream.iterate(...)` + `limit()`                       |
| 10 | One-time Use              | Streams cannot be reused after a terminal operation                                          | Use a new stream if you need to process again                                   |




## 🔹 Core Features of the Streams API

1. **Stream Creation**
    - Create streams from collections, arrays, or generator functions.
    - Example: `list.stream()`, `Stream.of(...)`, `Stream.generate(...)`

2. **Lazy Evaluation**
    - Intermediate operations are **lazy** and only executed when a terminal operation is triggered.

3. **Intermediate Operations** (return another Stream)
    - `filter(Predicate)`
    - `map(Function)`
    - `flatMap(Function)`
    - `distinct()`
    - `sorted()`
    - `limit(n)`
    - `skip(n)`

4. **Terminal Operations** (trigger evaluation and return a result)
    - `forEach(Consumer)`
    - `collect(Collector)`
    - `reduce(...)`
    - `count()`
    - `anyMatch(...)`, `allMatch(...)`, `noneMatch(...)`
    - `findFirst()`, `findAny()`

5. **Collectors Utility**
    - Built-in collectors in `java.util.stream.Collectors`:
        - `toList()`, `toSet()`, `toMap(...)`
        - `joining()`
        - `groupingBy(...)`
        - `partitioningBy(...)`
        - `counting()`, `summarizingInt(...)`, `averagingDouble(...)`

6. **Parallel Streams**
    - Easy parallelism via `parallelStream()`
    - Example: `list.parallelStream().map(...).collect(...)`

7. **Primitive Streams**
    - Specialized streams for performance:
        - `IntStream`, `LongStream`, `DoubleStream`
        - With range methods: `IntStream.range(...)`, `DoubleStream.of(...)`

8. **Stream Pipelining**
    - Combine multiple operations into a **pipeline** for readability and efficiency.
    - Example:
      ```java
      list.stream()
          .filter(x -> x > 5)
          .map(x -> x * 2)
          .collect(Collectors.toList());
      ```

9. **Infinite Streams**
    - Using `Stream.generate()` or `Stream.iterate()` to build infinite sequences.
    - Must use `limit()` to avoid infinite processing.

---

## 📦 Bonus: Streams Are Not Collections
- Streams don’t store data; they represent a **flow of data**.
- You can use a Stream **once** — it is consumed after a terminal operation.

---

