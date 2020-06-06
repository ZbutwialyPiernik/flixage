package com.zbutwialypiernik.flixage.ui.component.crud;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.shared.Registration;
import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.ui.component.ConfirmDialog;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.BidirectionalMapper;
import com.zbutwialypiernik.flixage.ui.component.form.Form;
import com.zbutwialypiernik.flixage.ui.component.form.dto.QueryableForm;
import lombok.Getter;
import org.springframework.data.util.ReflectionUtils;

import java.util.Objects;

/**
 * Wrapper over form to make generic conversion from DTO to Entity.
 * Used mainly in {@link Crud crud},
 */
public class DtoFormDialog<T extends Queryable, DTO extends QueryableForm> extends Composite<ConfirmDialog> {

    @Getter
    public class SubmitEvent extends ComponentEvent<DtoFormDialog<T, ?>> {

        private final T entity;
        private final DTO dto;

        public SubmitEvent(DtoFormDialog<T, ?> source, T entity, DTO dto) {
            super(source, false);
            this.entity = entity;
            this.dto = dto;
        }

    }

    private final Class<T> domainType;
    private final Form<DTO> form;
    private final BidirectionalMapper<T, DTO> mapper;

    private T entity;

    public DtoFormDialog(Class<T> domainType, Form<DTO> form, BidirectionalMapper<T, DTO> mapper) {
        super();
        this.domainType = domainType;
        this.form = form;
        this.mapper = mapper;

        getContent().addCloseListener(event -> getContent().close());
        getContent().addConfirmListener(event -> form.submit());
        getContent().addComponentAsFirst(form);

        clear();

        form.getContent().setPadding(false);
        form.addSubmitListener(event -> fireEvent(new SubmitEvent(this, mapper.mapFrom(event.getEntity(), entity), form.getDTO())));
    }

    public void clear() {
        setEntity(ReflectionUtils.createInstanceIfPresent(domainType.getName(), null));
    }

    public void setEntity(T entity) {
        Objects.requireNonNull(entity);
        this.entity = entity;

        DTO dto = mapper.mapTo(entity, form.getDTO());
        
        form.setDTO(dto);
    }

    public Registration addSubmitListener(ComponentEventListener<SubmitEvent> listener) {
        /* We need to downcast, because we can't get class from generic type */
        ComponentEventListener componentListener = event -> {
            SubmitEvent valueChangeEvent = (SubmitEvent) event;
            listener.onComponentEvent(valueChangeEvent);
        };

        return addListener(SubmitEvent.class, componentListener);
    }

}
