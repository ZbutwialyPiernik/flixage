package com.zbutwialypiernik.flixage.ui.component.form;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.shared.Registration;
import com.zbutwialypiernik.flixage.ui.component.crud.Crud;
import org.springframework.data.util.ReflectionUtils;

/**
 * /**
 *  * Layer of abstraction over Vaadin Form, to work with {@link Crud} includes headers and footers.
 *
 * @param <T>
 */
public class Form<T> extends Composite<VerticalLayout> implements HasSize {

    public static class SubmitEvent<T> extends ComponentEvent<Form<T>> {

        private final T entity;

        public SubmitEvent(Form<T> source, T entity) {
            super(source, false);
            this.entity = entity;
        }

        public T getEntity() {
            return entity;
        }

    }

    public static class CleanEvent extends ComponentEvent<Form<?>> {

        public CleanEvent(Form<?> source) {
            super(source, false);
        }

    }

    protected final BeanValidationBinder<T> binder;

    private final Class<T> entityClass;

    private final FlexLayout header;
    private final FormLayout body;
    private final FlexLayout footer;

    /**
     *
     * @param entityClass class of bean used in form, should have public non arg constructor
     */
    protected Form(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.binder = new BeanValidationBinder<>(entityClass);
        this.header = new FlexLayout();
        this.body = new FormLayout();
        this.footer = new FlexLayout();

        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        body.setWidthFull();

        getContent().add(header, body, footer);
        clear();
    }

    public Registration addCleanListener(ComponentEventListener<CleanEvent> listener) {
        return addListener(CleanEvent.class, listener);
    }

    public Registration addSubmitListener(ComponentEventListener<SubmitEvent<T>> listener) {
        /* We need to downcast, because we can't get class from generic type */
        ComponentEventListener componentListener = event -> {
            SubmitEvent<T> valueChangeEvent = (SubmitEvent<T>) event;
            listener.onComponentEvent(valueChangeEvent);
        };

        return addListener(SubmitEvent.class, componentListener);
    }

    public void setEntity(T entity) {
        binder.setBean(entity);
    }

    public T getEntity() {
        return binder.getBean();
    }

    public void submit() {
        if (binder.isValid()) {
            fireEvent(new SubmitEvent<>(this, getEntity()));
        }
    }

    public void clear() {
        binder.setBean(ReflectionUtils.createInstanceIfPresent(entityClass.getName(), null));
    }

    public FlexLayout getHeader() {
        return header;
    }

    public FormLayout getBody() {
        return body;
    }

    public FlexLayout getFooter() {
        return footer;
    }

    public BeanValidationBinder<T> getBinder() {
        return binder;
    }

}
