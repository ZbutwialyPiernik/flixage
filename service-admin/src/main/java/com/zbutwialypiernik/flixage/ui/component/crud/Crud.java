package com.zbutwialypiernik.flixage.ui.component.crud;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.function.ValueProvider;
import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.service.CrudService;
import com.zbutwialypiernik.flixage.service.QueryableService;
import com.zbutwialypiernik.flixage.ui.component.DeleteDialog;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.BidirectionalMapper;
import com.zbutwialypiernik.flixage.ui.component.form.Form;
import com.zbutwialypiernik.flixage.ui.component.form.dto.QueryableFormDTO;
import org.vaadin.klaudeta.PaginatedGrid;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Class representing generic, same looking cruds in admin panel.
 */
public abstract class Crud<T extends Queryable> extends Composite<VerticalLayout> implements HasSize {

    public static final Integer[] PAGE_SIZES = {10, 25, 50, 100};
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"));

    // Top Bar
    private final Select<Integer> pageSizeSelect = new Select<>(PAGE_SIZES);
    private final TextField searchField = new TextField("Search");

    // Top bar crud operations
    private final Button createButton = new Button("Create", new Icon(VaadinIcon.PLUS));
    private final Button updateButton = new Button("Update", new Icon(VaadinIcon.WRENCH));
    private final Button deleteButton = new Button("Delete", new Icon(VaadinIcon.TRASH));

    // Forms
    private DtoFormDialog<T, ?> createForm;
    private DtoFormDialog<T, ?> updateForm;
    private DeleteDialog<T> deleteDialog = new DeleteDialog<>("Are you sure you wanna delete?");

    private QueryableService<T> service;

    private final PaginatedGrid<T> grid;

    private final Class<T> domainType;

    protected Crud(Class<T> domainType) {
        this.domainType = domainType;

        grid = new PaginatedGrid<>();
        grid.setPageSize(PAGE_SIZES[0]);
        grid.setPaginatorSize(2);
        grid.setHeightByRows(true);

        createButton.addClickListener(event -> {
            createForm.clear();
            createForm.open();
        });
        updateButton.addClickListener(event -> {
            updateForm.setEntity(grid.asSingleSelect().getValue());
            updateForm.open();
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

        getContent().add(createButtonBar(), grid);
    }


    public Grid.Column<T> addColumn(ValueProvider<T, ?> valueProvider) {
        return grid.addColumn(valueProvider);
    }

    public <V extends Component> Grid.Column<T> addComponentColumn(
            ValueProvider<T, V> componentProvider) {
        return grid.addComponentColumn(componentProvider);
    }

    private Component createButtonBar() {
        // Aligned to left
        HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.setWidthFull();
        buttonBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Aligned to right
        pageSizeSelect.setLabel("Page Size");
        pageSizeSelect.setValue(PAGE_SIZES[0]);
        pageSizeSelect.addValueChangeListener(event -> grid.setPageSize(event.getValue()));
        searchField.addValueChangeListener((event) -> grid.refreshPaginator());

        HorizontalLayout leftButtons = new HorizontalLayout(
                searchField);
        leftButtons.setWidthFull();
        leftButtons.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.START);
        leftButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        HorizontalLayout rightButtons = new HorizontalLayout(
                pageSizeSelect,
                createButton,
                updateButton,
                deleteButton);

        rightButtons.setWidthFull();
        rightButtons.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        rightButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        buttonBar.add(leftButtons, rightButtons);

        return buttonBar;
    }

    public <DTO extends QueryableFormDTO> void setCreationForm(Form<DTO> form, BidirectionalMapper<T, DTO> converter) {
        createForm = new DtoFormDialog<>(domainType, form, converter);
        createForm.addSubmitListener(event -> onCreate(event.getEntity(), form.getDTO()));
    }

    public <DTO extends QueryableFormDTO> void setUpdateForm(Form<DTO> form, BidirectionalMapper<T, DTO> converter) {
        updateForm = new DtoFormDialog<>(domainType, form, converter);
        updateForm.addSubmitListener(event -> onUpdate(event.getEntity(), form.getDTO()));
    }

    public void setService(QueryableService<T> service) {
        this.service = service;
        this.grid.setDataProvider(createDataProvider());
    }

    public CrudService<T> getService() {
        return service;
    }

    private DataProvider<T, ?> createDataProvider() {
        return DataProvider.fromFilteringCallbacks(
                query -> service.findByName(searchField.getValue(), query.getOffset(), query.getLimit()).stream(),
                query -> service.countByName(searchField.getValue()));
    }

    private void onCreate(T bean, QueryableFormDTO dto) {
        T entity = service.create(bean);

        if (dto.getThumbnailResource() != null) {
            service.saveThumbnail(entity, dto.getThumbnailResource());
        }

        grid.getDataProvider().refreshItem(entity);
        grid.refreshPaginator();
        createForm.close();
    }

    private void onUpdate(T bean, QueryableFormDTO dto) {
        T entity = service.update(bean);

        if (dto.getThumbnailResource() != null) {
            service.saveThumbnail(entity, dto.getThumbnailResource());
        }

        grid.getDataProvider().refreshItem(entity);
        grid.refreshPaginator();
        updateForm.close();
    }

    private void onDelete(T bean) {
        deleteDialog.close();
        service.delete(bean);
        grid.getDataProvider().refreshAll();
        grid.refreshPaginator();
    }

}
