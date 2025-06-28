# 1. JEP 306: Restore Always-Strict Floating-Point Semantics
> This JEP is for numerically-sensitive programs, mainly scientific purposes; It again made default floating-point operations strict or Strictfp, ensure the same results from the floating-point calculations on every platform.

* Short history
1. Before Java 1.2, all floating-point calculations were strict; and it caused overheats on x87-based hardware.
2. Since Java 1.2, we need the keyword strictfp to enable the strict floating-point calculation. The default floating-point calculation was changed from strict to subtly different floating-point calculations (avoid overheats issues).
3. Now, since Intel and AMD have both support SSE2 (Streaming SIMD Extensions 2) extensions, which could support for the strict JVM floating-point operations without the overheats, so, the previous (before Java 1.2) overheats issues on x87-based hardware are irreverent in nowaday hardware.
4. Java 17 restores the pre-Java 1.2 strict floating-point calculations as default, which means the keyword strictfp is now optional.

# 2. JEP 356: Enhanced Pseudo-Random Number Generators
> This JEP introduced a new interface called RandomGenerator to make future pseudorandom number generator (PRNG), is an algorithm for generating a sequence of numbers whose properties approximate the properties of sequences of random numbers.) algorithms easier to implement or use.
- PRNG - A pseudorandom number generator, also known as a deterministic random bit generator (DRBG)
- DRBG - Deterministic Random Bit Generator
- Java 17 also refactored the legacy random classes like java.util.Random, SplittableRandom and SecureRandom to extend the new RandomGenerator interface.
- Example class: **Jep356.java** 

# 3. JEP 382: New macOS Rendering Pipeline
> This JEP changes the Java 2D (like Swing GUI) internal rendering pipeline for macOS from Apple OpenGL API to Apple Metal API; this is an internal change; there are no new Java 2D APIs, nor change any existing APIs.
- Apple deprecated the OpenGL rendering library in macOS 10.14 release(September 2018), in favor of the new Metal framework for better performance.

# 4. JEP 391: macOS/Arch64 Port
> Port the JDK to the new architecture macOS/Arch64 expecting future demand
- Apple's decision to move from x64 to AArch64 on its Macintosh computers. For Linux, an AArch64 version of Java is already available, and development on a Windows port is currently underway.
- Because of discrepancies in low-level conventions such as the programme binary interface and the collection of reserved processor registers, Java developers plan to reuse existing AArch64 code from these ports by using conditional compilation, as is standard in JDK ports.
- Changes for macOS/AArch64 have the potential to split current Linux/AArch64, Windows/Arch64, and MacOS/x64 ports, although this possibility can be mitigated by pre-integration testing.

# 5. JEP 398: Deprecate the Applet API for Removal
> Applet API is effectively useless, because all web browser vendors have either removed or revealed plans to drop support for Java browser plug-ins . While the Applet API was deprecated in Java 9, it was not removed earlier.
<br> This JEP marked the Applet API for removal.
```
@Deprecated(since = "9", forRemoval = true)
@SuppressWarnings("removal")
public class Applet extends Panel {
//...
}
```

# 6. JEP 403: Strongly Encapsulate JDK Internals
> This JEP is the successor to the above Java 16 JEP 396, and it takes one more step by removing the --illegal-access option, which means we have no ways to access the internal APIs, except for critical internal APIs such as sun.misc.Unsafe.
- Many third-party libraries, frameworks, and tools are accessing the internal APIs and packages of the JDK. The Java 16, JEP 396 make the strong encapsulation by default (we are not allowed to access the internal APIs easily). However, we can still use --illegal-access to switch to the simple encapsulation to still access the internal APIs.
```
--> Try the --illegal-access=warn in Java 17:

java --illegal-access=warn

OpenJDK 64-Bit Server VM warning: Ignoring option --illegal-access=warn; support was removed in 17.0   
```
 
# 7. JEP 406: Pattern Matching for switch (Preview)
> This JEP adds pattern matching for switch statements and expressions. Since this is a preview feature, we need to use --enable-preview option to enable it.
- Example code: 
  - **Jep406_IfElse.java** 
  - **Jep406_nullSwitch.java**
  - **Jep406_RefiningPatternInSwitch.java**

# 8. JEP 407: Remove RMI Activation
> Java 15, JEP385 deprecated the RMI Activation for removal.
<br> This JEP removed the RMI Activation or java.rmi.activation package.

# 9. JEP 409: Sealed Classes
> This JEP finalized the sealed classes as the standard feature in Java 17, with no changes from Java 16.
<br> The sealed classes and interfaces control or restrict who can be a subtype.
- Java 15, JEP 360 and Java 16, JEP 397 introduced [sealed class(https://cr.openjdk.java.net/~briangoetz/amber/datum.html) as a preview feature.
``` 
public sealed interface Command
    permits LoginCommand, LogoutCommand, PluginCommand{
    //...
}
```

# 10. JEP 410: Remove the Experimental AOT and JIT Compiler
> This feature has little use since they were introduced and required significant effort to maintain it, so this JEP removed the experimental Java-based ahead-of-time (AOT) and just-in-time (JIT) compiler
-  Java 9, JEP 295 introduced the Ahead-of-time compilation (the jaotc tool) as an experimental feature.
- Later Java 10, JEP 317 proposed it again as an experimental JIT compiler.

- The following AOT packages, classes, tools and codes are removed:
  - jdk.aot — the jaotc tool
  - jdk.internal.vm.compiler — the Graal compiler
  - jdk.internal.vm.compiler.management — Graal’s MBean
  - src/hotspot/share/aot — dumps and loads AOT code
  - Additional code guarded by #if INCLUDE_AOT

# 11. JEP 411: Deprecate the Security Manager for Removal
> This JEP deprecates the Security Manager for removal.
- Java 1.0 introduced the Security Manager to secure the client-side Java code, and irrelevant now.
```
package java.lang;

 * @since   1.0
 * @deprecated The Security Manager is deprecated and subject to removal in a
 *       future release. There is no replacement for the Security Manager.
 *       See <a href="https://openjdk.java.net/jeps/411">JEP 411</a> for
 *       discussion and alternatives.
 */
@Deprecated(since="17", forRemoval=true)
public class SecurityManager {
  //...
}
```
# 12. JEP 412: Foreign Function & Memory API (Incubator)
> This Foreign Function & Memory API allows the developer to access the code outside the JVM (foreign functions), data stored outside the JVM (off-heap data), and accessing memory not managed by the JVM (foreign memory).

*P.S This is an incubating feature; need add --add-modules jdk.incubator.foreign to compile and run the Java code.*

- History:
  - Java 14 JEP 370 introduced Foreign-Memory Access API (Incubator).
  - Java 15 JEP 383 introduced Foreign-Memory Access API (Second Incubator).
  - Java 16 JEP 389 introduced Foreign Linker API (Incubator).
  - Java 16 JEP 393 introduced Foreign-Memory Access API (Third Incubator).
  - Java 17 JEP 412 introduced Foreign Function & Memory API (Incubator).

# 13. JEP 414: Vector API (Second incubator)
> This JEP improves the Vector API performance and other enhancements like support operations on characters, translating byte vectors to and from boolean arrays, etc.
- Java 16, JEP 414 introduced new Vector API as an incubating API.

# 14. JEP 415: Context-Specific Deserialization Filters
> Allow applications to configure context-specific and dynamically-selected deserialization filters using a JVM-wide filter factory that is used to choose a filter for each deserialization operation.
- Deserialization of untrusted data is an operation that is intrinsically risky, because the contents of incoming data streams are obtained in many cases via an unknown or unauthenticated client.
- The key to preventing serialisation attacks is to prohibit instances of arbitrary classes from being deserialized, which prevents the execution of their methods either directly or indirectly.
- An attacker can run code from any class with bad intent by carefully constructing the stream. The integrity of application objects, library objects, and the Java runtime could be compromised if object construction involves side effects that change state or trigger other operations.