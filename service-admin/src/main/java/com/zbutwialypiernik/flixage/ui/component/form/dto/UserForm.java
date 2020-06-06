package com.zbutwialypiernik.flixage.ui.component.form.dto;

import com.zbutwialypiernik.flixage.entity.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserForm extends QueryableForm {

    @Size(min = 5, max = 32, message = "Length should be between 5 and 32")
    private String username;

    private boolean enabled;

    private boolean expired;

    private boolean locked;

    private boolean expiredCredentials;

    @Enumerated(value = EnumType.STRING)
    private Role role;

}
