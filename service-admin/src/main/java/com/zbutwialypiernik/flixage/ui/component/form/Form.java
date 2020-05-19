package com.zbutwialypiernik.flixage.ui.component.form;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.shared.Registration;
import com.zbutwialypiernik.flixage.ui.component.crud.Crud;
import com.zbutwialypiernik.flixage.ui.component.form.dto.QueryableFormDTO;
import org.springframework.data.util.ReflectionUtils;

/**
 * /**
 *  * Layer of abstraction over Vaadin Form, to work with {@link Crud} includes headers and footers.
 *
 * @param <DTO>
 */
public class Form<DTO extends QueryableFormDTO> extends Composite<VerticalLayout> implements HasSize {

    public static class SubmitEvent<DTO extends QueryableFormDTO> extends ComponentEvent<Form<DTO>> {

        private final DTO entity;

        public SubmitEvent(Form<DTO> source, DTO entity) {
            super(source, false);
            this.entity = entity;
        }

        public DTO getEntity() {
            return entity;
        }

    }

    public static class ClearEvent extends ComponentEvent<Form<?>> {

        public ClearEvent(Form<?> source) {
            super(source, false);
        }

    }

    protected final BeanValidationBinder<DTO> binder;

    private final Class<DTO> entityClass;

    private final FlexLayout header;
    private final FormLayout body;
    private final FlexLayout footer;

    /**
     *
     * @param entityClass class of bean used in form, should have public non arg constructor
     */
    protected Form(Class<DTO> entityClass) {
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

    public Registration addCleanListener(ComponentEventListener<ClearEvent> listener) {
        return addListener(ClearEvent.class, listener);
    }

    public Registration addSubmitListener(ComponentEventListener<SubmitEvent<DTO>> listener) {
        /* We need to downcast, because we can't get class from generic type */
        ComponentEventListener componentListener = event -> {
            SubmitEvent<DTO> valueChangeEvent = (SubmitEvent<DTO>) event;
            listener.onComponentEvent(valueChangeEvent);
        };

        return addListener(SubmitEvent.class, componentListener);
    }

    public void setDTO(DTO entity) {
        binder.setBean(entity);
    }

    public DTO getDTO() {
        return binder.getBean();
    }

    public void submit() {
        if (binder.isValid()) {
            fireEvent(new SubmitEvent<>(this, getDTO()));
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

    public BeanValidationBinder<DTO> getBinder() {
        return binder;
    }

}
