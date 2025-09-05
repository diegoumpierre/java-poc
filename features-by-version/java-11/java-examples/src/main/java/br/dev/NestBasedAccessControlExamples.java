package br.dev;

/**
 * Nest-Based Access Control Example (Java 11)
 *
 * Java 11 improves access between nested classes (inner, static, local) and their enclosing class.
 * Private members can be accessed directly between nestmates (classes in the same nest).
 */
public class NestBasedAccessControlExamples {
    private String secret = "Top Secret";

    // Inner class accessing private member of outer class
    class Inner {
        public String revealSecret() {
            // Direct access to private member of enclosing class
            return secret;
        }
    }

    // Static nested class accessing private member of outer class
    static class StaticNested {
        public String getOuterSecret(NestBasedAccessControlExamples outer) {
            // Direct access to private member of outer class
            return outer.secret;
        }
    }

    public void demonstrate() {
        Inner inner = new Inner();
        System.out.println("Inner reveals: " + inner.revealSecret());

        StaticNested staticNested = new StaticNested();
        System.out.println("StaticNested reveals: " + staticNested.getOuterSecret(this));
    }

    public static void main(String[] args) {
        NestBasedAccessControlExamples example = new NestBasedAccessControlExamples();
        example.demonstrate();
    }
}

