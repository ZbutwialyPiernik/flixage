package com.zbutwialypiernik.flixage.ui.admin.user.form;

import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.ui.component.form.dto.QueryableFormDTO;
import com.zbutwialypiernik.flixage.validator.ValidPassword;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserCreateForm extends QueryableFormDTO {

    @Size(min = 5, max = 32, message = "Length between 5-32")
    private String username;

    @Size(min = 5, max = 128, message = "Length between 5-128")
    @ValidPassword
    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;

}
