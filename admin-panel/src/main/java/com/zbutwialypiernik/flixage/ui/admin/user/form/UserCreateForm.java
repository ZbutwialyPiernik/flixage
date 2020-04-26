package com.zbutwialypiernik.flixage.ui.admin.user.form;

import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.validator.password.ValidPassword;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserCreateForm {

    private String id;

    @Size(min = 5, max = 32, message = "Length between 5-32")
    private String username;

    @Size(min = 5, max = 128, message = "Length between 5-128")
    @ValidPassword
    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;

}
