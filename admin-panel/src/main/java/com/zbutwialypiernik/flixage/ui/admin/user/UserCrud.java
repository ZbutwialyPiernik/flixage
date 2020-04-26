package com.zbutwialypiernik.flixage.ui.admin.user;

import com.google.common.base.CaseFormat;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.spring.annotation.UIScope;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.service.UserService;
import com.zbutwialypiernik.flixage.ui.admin.user.form.UserCreateForm;
import com.zbutwialypiernik.flixage.ui.admin.user.form.UserUpdateForm;
import com.zbutwialypiernik.flixage.ui.component.crud.Crud;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.OrikaMapperFactory;
import com.zbutwialypiernik.flixage.ui.component.form.Form;
import com.zbutwialypiernik.flixage.ui.component.form.FormBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class UserCrud extends Crud<User> {

    @Autowired
    public UserCrud(UserService service, OrikaMapperFactory factory) {
        super(User.class);

        setDataProvider(service);

        FormBuilder<UserCreateForm> createGenerator = new FormBuilder<>(UserCreateForm.class);
        createGenerator.setHeader("Create user");
        createGenerator.addFields(
                new FormBuilder.FormField("username", "Username"),
                new FormBuilder.FormField("password", "Password"),
                new FormBuilder.FormField("role", "Role"));

        Form<UserCreateForm> creationForm = createGenerator.build();

        FormBuilder<UserUpdateForm> updateGenerator = new FormBuilder<>(UserUpdateForm.class);
        updateGenerator.setHeader("Update user");
        updateGenerator.addFields(
                new FormBuilder.FormField("username", "Username"),
                new FormBuilder.FormField("role", "Role"),
                new FormBuilder.FormField("enabled", "Enabled"),
                new FormBuilder.FormField("expired", "Expired"),
                new FormBuilder.FormField("locked", "Locked"),
                new FormBuilder.FormField("expiredCredentials", "Expired Credentials"));

        Form<UserUpdateForm> updateForm = updateGenerator.build();

        creationForm.getBinder().withValidator(Validator.from(
                (entity) -> !service.isUsernameTaken(entity.getUsername()),
                "Username is already taken"));

        updateForm.getBinder().withValidator(Validator.from(
                (entity) -> !service.isUsernameTakenByOtherUser(entity.getId(), entity.getUsername()),
                "Username is already taken"));

        setCreationForm(creationForm, factory.createConverter());
        setUpdateForm(updateForm, factory.createConverter());

        addColumn(User::getUsername)
                .setHeader("Username");
        addColumn(user -> CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, user.getRole().name()))
                .setHeader("Role");
        addColumn(User::isEnabled)
                .setHeader("Enabled");
        addColumn(User::isExpired)
                .setHeader("Expired");
        addColumn(User::isLocked)
                .setHeader("Locked");
        addColumn(User::isExpiredCredentials)
                .setHeader("Expired Credentials");
        addColumn(user -> DATE_FORMAT.format(user.getCreationTime()))
                .setHeader("Creation Time");
    }

    public void addAvatarField(Form<User> form) {

    }

}

