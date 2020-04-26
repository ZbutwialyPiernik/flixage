package com.zbutwialypiernik.flixage;

import com.zbutwialypiernik.flixage.repository.UserRepository;
import com.zbutwialypiernik.flixage.service.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;


public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

}
