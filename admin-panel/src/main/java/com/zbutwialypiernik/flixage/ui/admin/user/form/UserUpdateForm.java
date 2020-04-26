package com.zbutwialypiernik.flixage.ui.admin.user.form;

import com.zbutwialypiernik.flixage.entity.Role;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
public class UserUpdateForm {

    private String id;

    @Size(min = 5, max = 32, message = "Length between 5-32")
    private String username;

    private boolean enabled = true;

    private boolean expired = false;

    private boolean locked = false;

    private boolean expiredCredentials = false;

    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;

}
