package com.zbutwialypiernik.flixage.ui.component.crud.impl;

import com.google.common.base.CaseFormat;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.spring.annotation.UIScope;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.service.UserService;
import com.zbutwialypiernik.flixage.ui.component.crud.PaginatedCrud;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.MapperFactory;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.OrikaMapperFactory;
import com.zbutwialypiernik.flixage.ui.component.form.Form;
import com.zbutwialypiernik.flixage.ui.component.form.FormBuilder;
import com.zbutwialypiernik.flixage.ui.component.form.dto.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Component
@UIScope
public class UserCrud extends PaginatedCrud<User, UserForm> {

    @Autowired
    public UserCrud(UserService service, MapperFactory factory) {
        super(User.class);

        setService(service);

        FormBuilder<UserForm> formGenerator = new FormBuilder<>(UserForm.class);
        formGenerator.setHeader("Update user");
        formGenerator.addFields(
                new FormBuilder.FormField("username", "Username"),
                new FormBuilder.FormField("role", "Role"),
                new FormBuilder.FormField("enabled", "Enabled"),
                new FormBuilder.FormField("expired", "Expired"),
                new FormBuilder.FormField("locked", "Locked"),
                new FormBuilder.FormField("expiredCredentials", "Expired Credentials"));

        Form<UserForm> updateForm = formGenerator.build();

        updateForm.getBinder().withValidator(Validator.from(
                (dto) -> !service.isUsernameTakenByOtherUser(dto.getId(), dto.getUsername()),
                "Username is already taken"));

        setForm(updateForm, factory.createMapper());

        addColumn(User::getName)
                .setHeader("Name");
        addColumn(User::getUsername)
                .setHeader("Username");
        addColumn(user -> CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, user.getRole().name()))
                .setHeader("Role");
        addColumn(User::isEnabled)
                .setHeader("Enabled");
        addColumn(User::isLocked)
                .setHeader("Locked");
        addColumn(User::isExpiredCredentials)
                .setHeader("Expired Credentials");
        addColumn(user -> DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .localizedBy(getLocale())
                .withZone(ZoneId.of("UTC"))
                .format(user.getCreationTime()))
                .setHeader("Creation Time [UTC]");
    }

}

