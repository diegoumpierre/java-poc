package com.poc.springsecurity.config;

import com.poc.springsecurity.entity.Role;
import com.poc.springsecurity.entity.User;
import com.poc.springsecurity.repository.RoleRepository;
import com.poc.springsecurity.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public AdminUserConfig(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception{

        method1();
        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());
        var userAdmin = userRepository.findByUsername("admin");

        userAdmin.ifPresentOrElse(
                user -> System.out.println("admin exists"),
                () -> {
                    var user = new User();
                    user.setUsername("admin");
                    user.setPassword(passwordEncoder.encode("123"));
                    user.setRoles(Set.of(roleAdmin.get()));
                    userRepository.save(user);
                }

        );

    }

    private void method1() {
        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());
        roleAdmin.ifPresentOrElse(
                (role -> {
                    System.out.println("role admin present");
                }),
                () -> {
                    var role1 = new Role();
                    role1.setName(Role.Values.ADMIN.name());
                    role1.setRoleId(Role.Values.ADMIN.getRoleId());
                    roleRepository.save(role1);

                    var role2 = new Role();
                    role2.setName(Role.Values.BASIC.name());
                    role2.setRoleId(Role.Values.BASIC.getRoleId());
                    roleRepository.save(role2);

                    roleRepository.flush();
                }

        );
    }
}
