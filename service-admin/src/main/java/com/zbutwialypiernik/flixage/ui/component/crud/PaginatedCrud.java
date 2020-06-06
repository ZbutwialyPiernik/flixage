package com.zbutwialypiernik.flixage.ui.component.crud;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.service.QueryableService;
import com.zbutwialypiernik.flixage.ui.component.form.dto.QueryableForm;
import org.vaadin.klaudeta.PaginatedGrid;

/**
 * Class representing generic, same looking CRUDs in admin panel with pagination.
 *
 * @param <T> Domain type
 */
public class PaginatedCrud<T extends Queryable, DTO extends QueryableForm> extends Crud<T, DTO> {

    protected static final Integer[] PAGE_SIZES = {10, 25, 50, 100};

    // Top Bar
    private Select<Integer> pageSizeSelect;
    private TextField searchField;

    protected PaginatedCrud(Class<T> domainType) {
        super(domainType);
    }

    @Override
    protected Grid<T> setupGrid() {
        var grid = new PaginatedGrid<T>();
        grid.setPageSize(PAGE_SIZES[0]);
        grid.setPaginatorSize(2);
        grid.setHeightByRows(true);

        return grid;
    }

    @Override
    protected HorizontalLayout createButtonBar() {
        // Aligned to left
        HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.setWidthFull();
        buttonBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Aligned to right
        pageSizeSelect = new Select<>(PAGE_SIZES);
        pageSizeSelect.setLabel("Page Size");
        pageSizeSelect.setValue(PAGE_SIZES[0]);
        pageSizeSelect.addValueChangeListener(event -> getGrid().setPageSize(event.getValue()));

        searchField = new TextField("Search");
        searchField.addValueChangeListener((event) -> getGrid().refreshPaginator());

        HorizontalLayout leftButtons = new HorizontalLayout(
                searchField);
        leftButtons.setWidthFull();
        leftButtons.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.START);
        leftButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        HorizontalLayout rightButtons = super.createButtonBar();
        rightButtons.addComponentAsFirst(pageSizeSelect);

        rightButtons.setWidthFull();
        rightButtons.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        rightButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        buttonBar.add(leftButtons, rightButtons);

        return buttonBar;
    }

    @Override
    public void refresh() {
        super.refresh();
        getGrid().refreshPaginator();
    }

    @Override
    public void setService(QueryableService<T> service) {
        super.setService(service);

        getGrid().setDataProvider(
                DataProvider.fromFilteringCallbacks(
                query -> service.findByName(searchField.getValue(), query.getOffset(), query.getLimit()).stream(),
                query -> service.countByName(searchField.getValue()))
        );
    }

    @Override
    protected PaginatedGrid<T> getGrid() {
        return (PaginatedGrid<T>) super.getGrid();
    }

}