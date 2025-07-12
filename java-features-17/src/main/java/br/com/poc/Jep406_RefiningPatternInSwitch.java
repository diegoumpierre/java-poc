package br.com.poc;

public class Jep406_RefiningPatternInSwitch {
}

class Shape {}
class Rectangle extends Shape {}
class Triangle  extends Shape {
    int calculateArea() {
        //...
        return 0;
    }
}

class before2Java17{
       public static void testTriangle(Shape s) {
        switch (s) {
            case null:
                break;
            case Triangle t:
                if (t.calculateArea() > 100) {
                    System.out.println("Large triangle");
                    break;
                }else{
                    System.out.println("Triangle");
                }
            default:
                System.out.println("Unknown!");
        }
    }
}

class after2Java17{
    static void testTriangle2(Shape s) {
        switch (s) {
            case null ->
            {}
            case Triangle t && (t.calculateArea() > 100) ->
                    System.out.println("Large triangle");
            case Triangle t ->
                    System.out.println("Triangle");
            default ->
                    System.out.println("Unknown!");
        }
    }
}

