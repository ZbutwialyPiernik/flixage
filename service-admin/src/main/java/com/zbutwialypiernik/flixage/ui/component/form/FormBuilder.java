package com.zbutwialypiernik.flixage.ui.component.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.zbutwialypiernik.flixage.service.resource.image.ImageResource;
import com.zbutwialypiernik.flixage.service.resource.track.AudioResource;
import com.zbutwialypiernik.flixage.ui.component.form.dto.QueryableForm;
import lombok.Value;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class FormBuilder<T extends QueryableForm> {

    @FunctionalInterface
    public interface FieldGenerator {

        HasValue<?, ?> generate(Class<?> beanType, FormField field);

    }

    @Value
    public static class FormField {

        String name;
        String label;

    }

    private final HashMap<Class<?>, FieldGenerator> classToField = new HashMap<>();
    //To avoid duplications of properties and keep order provided by user
    private final LinkedHashSet<FormField> formFields = new LinkedHashSet<>();
    private final Class<T> entityClass;

    private String header;

    public FormBuilder(Class<T> entityClass) {
        this.entityClass = entityClass;

        classToField.put(String.class, (type, field) -> new TextField());
        classToField.put(Number.class, (type, field) -> new NumberField());
        classToField.put(Boolean.class, (type, field) -> new Checkbox());
        classToField.put(Enum.class, (type, field) -> {
            Select select = new Select<>(type.getEnumConstants());
            select.setValue(type.getEnumConstants()[0]);
            return select;
        });
        classToField.put(ImageResource.class, (type, field) ->
            new FormUpload<ImageResource>(ImageResource.MAX_FILE_SIZE, ImageResource.ACCEPTED_TYPES) {
                @Override
                protected ImageResource transform(InputStream inputStream, FileData fileData) throws IOException {
                    return new ImageResource(inputStream.readAllBytes(),
                            fileData.getFileName(),
                            fileData.getMimeType());
                }
            }
        );
        classToField.put(AudioResource.class, (type, field) ->
            new FormUpload<AudioResource>(AudioResource.MAX_FILE_SIZE, AudioResource.ACCEPTED_TYPES) {
                @Override
                protected AudioResource transform(InputStream inputStream, FileData fileData) throws IOException {
                    return new AudioResource(inputStream.readAllBytes(),
                            fileData.getFileName(),
                            fileData.getMimeType());
                }
            }
        );

        addFields(
                new FormField("thumbnailResource", "Thumbnail"),
                new FormField("name", "Name")
        );
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void addFields(FormField... propertyNames) {
        Collections.addAll(formFields, propertyNames);
    }

    public void clear() {
        formFields.clear();
    }

    public Form<T> build() {
        Form<T> form = new Form<>(entityClass);

        // If user didn't provide any field, we're taking every single one by default.
        if (formFields.isEmpty()) {
            formFields.addAll(Arrays.stream(entityClass.getDeclaredFields())
                    .map(field -> new FormField(field.getName(), field.getName()))
                    .collect(Collectors.toList())
            );
        }

        for (FormField formField : formFields) {
            if (formField.getName().equalsIgnoreCase("id")) {
                throw new IllegalStateException("Id is not editable");
            }

            Field beanField = ReflectionUtils.findField(entityClass, formField.getName());

            if (beanField == null) {
                throw new IllegalStateException("Property " + formField.getName() + " not found");
            }

            HasValue<?, ?> fieldInstance = null;

            for (Map.Entry<Class<?>, FieldGenerator> entry : classToField.entrySet()) {
                // Using spring utils class to autobox primitive types
                if (ClassUtils.isAssignable(entry.getKey(), beanField.getType())) {
                    fieldInstance = entry.getValue().generate(beanField.getType(), formField);
                    break;
                }
            }

            if (fieldInstance != null) {
                var fieldBinder = form.binder.forField(fieldInstance);

                if (fieldInstance instanceof Component) {
                    var column = new VerticalLayout();
                    column.setPadding(false);
                    var label = new Label();
                    column.add((Component) fieldInstance, label);

                    fieldBinder.withValidationStatusHandler(statusChange -> {
                        label.setVisible(statusChange.isError());
                        label.setText(statusChange.getMessage().orElse("Error"));
                    });

                    form.getBody().addFormItem(column, formField.getLabel());
                }

                fieldBinder.bind(formField.getName());
            } else {
                throw new IllegalStateException("Field type not found for property: " + formField);
            }
        }

        if (header != null && !header.isEmpty()) {
            Label text = new Label(header);
            text.getElement().getStyle().set("font-weight", "bold");
            text.getElement().getStyle().set("font-size", "2em");
            form.getHeader().add();
        }

        return form;
    }

}
