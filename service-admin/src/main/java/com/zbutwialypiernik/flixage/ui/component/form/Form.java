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
import com.zbutwialypiernik.flixage.ui.component.form.dto.QueryableForm;
import org.springframework.data.util.ReflectionUtils;

/**
 * /**
 *  * Layer of abstraction over Vaadin Form, to work with {@link Crud} includes headers and footers.
 *
 * @param <F> the form type
 */
public class Form<F extends QueryableForm> extends Composite<VerticalLayout> implements HasSize  {

    public static class SubmitEvent<F extends QueryableForm> extends ComponentEvent<Form<F>> {

        private final F form;

        public SubmitEvent(Form<F> source, F form) {
            super(source, false);
            this.form = form;
        }

        public F getForm() {
            return form;
        }

    }

    public static class ClearEvent extends ComponentEvent<Form<?>> {

        public ClearEvent(Form<?> source) {
            super(source, false);
        }

    }

    protected final BeanValidationBinder<F> binder;

    private final Class<F> entityClass;

    private final FlexLayout header;
    private final FormLayout body;
    private final FlexLayout footer;

    /**
     * @param entityClass class of bean used in form, should have public non arg constructor
     */
    protected Form(Class<F> entityClass) {
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

    public Registration addSubmitListener(ComponentEventListener<SubmitEvent<F>> listener) {
        /* We need to downcast, because we can't get class from generic type */
        ComponentEventListener componentListener = event -> {
            SubmitEvent valueChangeEvent = (SubmitEvent) event;
            listener.onComponentEvent(valueChangeEvent);
        };

        return addListener(SubmitEvent.class, componentListener);
    }

    public void setDTO(F entity) {
        binder.setBean(entity);
    }

    public F getDTO() {
        return binder.getBean();
    }

    public void submit() {
        if (binder.validate().isOk()) {
            fireEvent(new SubmitEvent(this, getDTO()));
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

    public BeanValidationBinder<F> getBinder() {
        return binder;
    }

}
