package com.zbutwialypiernik.flixage.ui.component.crud;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.service.QueryableService;
import com.zbutwialypiernik.flixage.ui.component.ComponentUtils;
import com.zbutwialypiernik.flixage.ui.component.DeleteDialog;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.BidirectionalMapper;
import com.zbutwialypiernik.flixage.ui.component.form.Form;
import com.zbutwialypiernik.flixage.ui.component.form.dto.QueryableForm;

/**
 * Class representing generic, same looking CRUDs in admin panel.
 *
 * @param <T> the queryable type
 * @param <F> the DTO type representing form
 */
public abstract class Crud<T extends Queryable, F extends QueryableForm> extends VerticalLayout {

    // Top bar crud operations
    private final Button createButton = new Button("Create", new Icon(VaadinIcon.PLUS));
    private final Button updateButton = new Button("Update", new Icon(VaadinIcon.WRENCH));
    private final Button deleteButton = new Button("Delete", new Icon(VaadinIcon.TRASH));

    // Forms
    protected DtoFormDialog<T, F> formDialog;
    protected DeleteDialog<T> deleteDialog = new DeleteDialog<>("Are you sure you wanna delete?");

    private final Grid<T> grid;

    protected final Class<T> domainType;
    protected QueryableService<T> service;

    public Crud(Class<T> domainType) {
        this.domainType = domainType;

        grid = setupGrid();

        createButton.addClickListener(event -> {
            formDialog.clear();
            formDialog.getContent().open();
        });

        updateButton.addClickListener(event -> {
            formDialog.setEntity(grid.asSingleSelect().getValue());
            formDialog.getContent().open();
        });

        deleteButton.addClickListener(event -> {
            deleteDialog.setEntity(grid.asSingleSelect().getValue());
            deleteDialog.open();
        });

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        deleteDialog.addConfirmListener(event -> onDelete(grid.asSingleSelect().getValue()));

        grid.asSingleSelect().addValueChangeListener(event -> {
            updateButton.setEnabled(event.getValue() != null);
            deleteButton.setEnabled(event.getValue() != null);
        });

        addComponentColumn(queryable -> {
            Image image = new Image();
            service.getThumbnailById(queryable.getId()).ifPresentOrElse(
                    resource -> image.setSrc(ComponentUtils.imageFromByteArray(resource)),
                    () -> image.setSrc("img/placeholder.jpg"));
            image.setHeight("64px");
            image.setWidth("64px");

            return image;
        }).setHeader("Thumbnail");
        addColumn(Queryable::getName).setHeader("Name");

        add(createButtonBar(), grid);
    }

    protected Grid<T> setupGrid() {
        return new Grid<>();
    }

    protected HorizontalLayout createButtonBar() {
        HorizontalLayout buttons = new HorizontalLayout(
                createButton,
                updateButton,
                deleteButton);

        buttons.setWidthFull();
        buttons.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        return buttons;
    }

    public void setService(QueryableService<T> service) {
        this.service = service;
    }

    public QueryableService<T> getService() {
        return service;
    }

    protected void onCreate(DtoFormDialog<T, F>.SubmitEvent event) {
        T entity = service.create(event.getEntity());

        if (event.getDto().getThumbnailResource() != null) {
            service.saveThumbnail(entity, event.getDto().getThumbnailResource());
        }

        formDialog.getContent().close();
        refresh();
    }

    protected void onUpdate(DtoFormDialog<T, F>.SubmitEvent event) {
        T entity = service.update(event.getEntity());

        if (event.getDto().getThumbnailResource() != null) {
            service.saveThumbnail(entity, event.getDto().getThumbnailResource());
        }

        formDialog.getContent().close();
        refresh();
    }

    protected void onDelete(T bean) {
        deleteDialog.close();
        service.delete(bean);

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        refresh();
    }

    public void refresh() {
        grid.getDataProvider().refreshAll();
    }

    public Grid.Column<T> addColumn(ValueProvider<T, ?> valueProvider) {
        return grid.addColumn(valueProvider);
    }

    public <V extends Component> Grid.Column<T> addComponentColumn(
            ValueProvider<T, V> componentProvider) {
        return grid.addComponentColumn(componentProvider);
    }

    public void setForm(Form<F> form, BidirectionalMapper<T, F> converter) {
        formDialog = new DtoFormDialog<>(domainType, form, converter);
        formDialog.addSubmitListener(event -> {
            if (event.getEntity().getId() == null) {
                onCreate(event);
            } else {
                onUpdate(event);
            }
        });
    }

    protected Grid<T> getGrid() {
        return grid;
    }

}