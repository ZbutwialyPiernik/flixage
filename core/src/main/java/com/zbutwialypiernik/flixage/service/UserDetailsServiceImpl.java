package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;

        User admin = new User();
        admin.setUsername("admin");
        admin.setName("John Wick");
        admin.setPassword("Passw0rd");
        admin.setRole(Role.ADMIN);

        for (int i = 0; i < 100; i++) {
            int leftLimit = 97; // letter 'a'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();

            String generatedString = random.ints(leftLimit, rightLimit + 1)
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            User newUser = new User();
            newUser.setRole(Role.USER);
            newUser.setUsername(generatedString);
            newUser.setName(generatedString + "name");
            newUser.setPassword("Passw0rd");
            userService.create(newUser);
        }

        userService.create(admin);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }

}
