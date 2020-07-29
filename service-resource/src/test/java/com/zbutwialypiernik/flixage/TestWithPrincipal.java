package com.zbutwialypiernik.flixage;

import com.zbutwialypiernik.flixage.config.GatewayUriFactory;
import com.zbutwialypiernik.flixage.config.MapperConfiguration;
import com.zbutwialypiernik.flixage.config.WebSecurityConfig;
import com.zbutwialypiernik.flixage.config.ZoneConfiguration;
import com.zbutwialypiernik.flixage.dto.mapper.DtoMappersConfiguration;
import com.zbutwialypiernik.flixage.dto.mapper.converter.CustomConverterConfiguration;
import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationParser;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationToken;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@Import({
        /* Security */ WebSecurityConfig.class, ZoneConfiguration.class,
        /* Mapper */ DtoMappersConfiguration.class, GatewayUriFactory.class, MapperConfiguration.class, CustomConverterConfiguration.class})
public abstract class TestWithPrincipal {

    @MockBean
    public JwtAuthenticationParser parser;

    public final User user = new User();

    public final User otherUser = new User();

    public final JwtAuthenticationToken token = AuthenticationTestHelper.createMockAuthentication(Role.USER);

    @BeforeEach
    void prepare() {
        when(parser.parseToken(any())).thenReturn(token);

        user.setId(token.getId());
        user.setUsername(token.getUsername());
        user.setRole(token.getRole());

        otherUser.setId("some other id");
        otherUser.setUsername("user#" + otherUser.getId());
        otherUser.setRole(Role.USER);
    }

}
