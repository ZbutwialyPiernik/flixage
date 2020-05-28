package com.zbutwialypiernik.flixage;

import com.zbutwialypiernik.flixage.config.GatewayUriBuilder;
import com.zbutwialypiernik.flixage.config.MapperConfiguration;
import com.zbutwialypiernik.flixage.config.WebSecurityConfig;
import com.zbutwialypiernik.flixage.controller.PlaylistController;
import com.zbutwialypiernik.flixage.dto.mapper.DtoMappersConfiguration;
import com.zbutwialypiernik.flixage.dto.mapper.converter.CustomConverterConfiguration;
import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.handler.DefaultExceptionHandler;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationParser;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationToken;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.awt.desktop.SystemEventListener;
import java.time.Clock;

import static org.mockito.Mockito.*;

@Import({/* Security */ WebSecurityConfig.class,
        /* Mapper */DtoMappersConfiguration.class, GatewayUriBuilder.class, MapperConfiguration.class, CustomConverterConfiguration.class})
public abstract class TestWithPrincipal {

    @MockBean
    public JwtAuthenticationParser parser;

    @MockBean
    public Clock clock;

    public final User user = new User();

    public final JwtAuthenticationToken token = AuthenticationTestHelper.createMockAuthentication(Role.USER);

    @BeforeEach
    void prepare() {
        when(parser.parseToken(any())).thenReturn(token);

        user.setId(token.getId());
        user.setUsername(token.getUsername());
        user.setRole(token.getRole());
    }

}
