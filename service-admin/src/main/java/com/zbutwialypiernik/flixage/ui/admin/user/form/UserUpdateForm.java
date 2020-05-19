package com.zbutwialypiernik.flixage.ui.admin.user.form;

import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.ui.component.form.dto.QueryableFormDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserUpdateForm extends QueryableFormDTO {

    @Size(min = 5, max = 32, message = "Length between 5-32")
    private String username;

    private boolean enabled;

    private boolean expired;

    private boolean locked;

    private boolean expiredCredentials;

    @Enumerated(value = EnumType.STRING)
    private Role role;

}
