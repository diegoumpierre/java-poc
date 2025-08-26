package main.java.br.dev;


interface AnyInterface {

    public void someMethod();

    // Default method
    default void defaultMethod() {
        System.out.println("This is a default method.");
    }

    // Static method
    static void staticMethod() {
        System.out.println("This is a static method.");
    }
}
public class DefaultStaticMethodsInterfaceExample implements AnyInterface {

    //in this case we are obligated to implement someMethod()
    @Override
    public void someMethod() {
        System.out.println("This is a someMethod.");
    }

    // No need to implement defaultMethod(), it's inherited
    public void useMethods() {

        // Calling the default method
        defaultMethod();

        // Calling the static method
        AnyInterface.staticMethod();
    }

    public static void main(String[] args) {
        DefaultStaticMethodsInterfaceExample defaultStaticMethodsInterfaceExample = new DefaultStaticMethodsInterfaceExample();
        defaultStaticMethodsInterfaceExample.someMethod();
        defaultStaticMethodsInterfaceExample.useMethods();
    }
}

// Output:
//This is a someMethod.
//This is a default method.
//This is a static method.
