package com.zbutwialypiernik.flixage.ui.component.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.zbutwialypiernik.flixage.service.ImageResource;
import com.zbutwialypiernik.flixage.service.QueryableService;
import com.zbutwialypiernik.flixage.ui.component.form.dto.QueryableFormDTO;
import lombok.Value;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import util.ExtensionUtils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class FormBuilder<T extends QueryableFormDTO> {

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

        classToField.put(String.class, (type, field) -> new TextField(field.getLabel()));
        classToField.put(Number.class, (type, field) -> new NumberField(field.getLabel()));
        classToField.put(Boolean.class, (type, field) -> new Checkbox(field.getLabel()));
        classToField.put(Enum.class, (type, field) -> {
            Select select = new Select<>(type.getEnumConstants());
            select.setLabel(field.getLabel());
            select.setValue(type.getEnumConstants()[0]);
            return select;
        });
        //classToField.put(File.class, (type, label) -> { return new TextField(); });
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
                    .map((field) -> new FormField(field.getName(), field.getName()))
                    .collect(Collectors.toList())
            );
        }

        addThumbnailUpload(form);

        for (FormField formField : formFields) {
            Field beanField = ReflectionUtils.findField(entityClass, formField.getName());

            if (beanField == null) {
                throw new IllegalArgumentException("Property " + formField.getName() + " not found");
            }

            HasValue<?, ?> fieldInstance = null;

            for (Map.Entry<Class<?>, FieldGenerator> entry : classToField.entrySet()) {
                //Using spring utils class to autobox primitive types
                if (ClassUtils.isAssignable(entry.getKey(), beanField.getType())) {
                    fieldInstance = entry.getValue().generate(beanField.getType(), formField);
                    break;
                }
            }

            if (fieldInstance != null) {
                form.binder.forField(fieldInstance)
                        .bind(formField.getName());

                if (fieldInstance instanceof Component) {
                    form.getBody().add((Component) fieldInstance);
                }
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

    private void addThumbnailUpload(Form<T> form) {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(ExtensionUtils.ACCEPTED_IMAGE_TYPES);
        upload.setMaxFiles(1);
        upload.setMaxFileSize(QueryableService.MAX_FILE_SIZE);
        upload.addFinishedListener(event -> {
            form.getDTO().setThumbnailResource(new ImageResource() {
                @Override
                public InputStream getInputStream() {
                    return buffer.getInputStream();
                }

                @Override
                public String getName() {
                    return buffer.getFileData().getFileName();
                }

                @Override
                public String getExtension() {
                    return FilenameUtils.getExtension(buffer.getFileData().getFileName());
                }

                @Override
                public String getMimeType() {
                    return buffer.getFileData().getMimeType();
                }
            });
        });
        form.getBody().add(upload);
    }

}
