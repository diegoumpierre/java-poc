package main.java.br.dev;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Roles {
    Role[] value();
}

@Repeatable(Roles.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Role {
    String value();
}

@Role("ADMIN")
@Role("USER")
class UserAccount {
}


public class RepeatableAnnotationExample {
    public static void main(String[] args) {
        Class<UserAccount> clazz = UserAccount.class;

        // Get all @Role annotations
        Role[] roles = clazz.getAnnotationsByType(Role.class);

        System.out.println("Roles:");
        for (Role role : roles) {
            System.out.println("- " + role.value());
        }
    }
}
//output:
/*
Roles:
- ADMIN
- USER
*/