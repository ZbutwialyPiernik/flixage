package com.zbutwialypiernik.flixage.ui.admin.artist;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.zbutwialypiernik.flixage.ui.component.DeleteDialog;
import com.zbutwialypiernik.flixage.ui.component.crud.DtoFormDialog;

import java.util.Collection;
import java.util.stream.Stream;

public class ListPanel<T> extends VerticalLayout {

    private final Button addButton;
    private final Button removeButton;
    private final ListBox<T> listBox;

    private final DtoFormDialog<T, ?> formDialog;
    private final DeleteDialog<T> deleteDialog;

    public ListPanel(DtoFormDialog<T, ?> formDialog) {
        this.formDialog = formDialog;
        this.deleteDialog = new DeleteDialog<>("Are you sure you wanna delete?");
        this.listBox = new ListBox<>();

        addButton = new Button(new Icon(VaadinIcon.PLUS), (event) -> formDialog.open());
        removeButton = new Button(new Icon(VaadinIcon.MINUS), (event) ->
            deleteDialog.setEntity(listBox.getValue())
        );
        removeButton.setEnabled(false);


        deleteDialog.addConfirmListener((event) -> {

        });

        listBox.addValueChangeListener((event) -> removeButton.setEnabled(event.getValue() != null));

        HorizontalLayout buttonsLayout = new HorizontalLayout(addButton, removeButton);
        buttonsLayout.setWidthFull();
        buttonsLayout.setAlignItems(FlexComponent.Alignment.END);

        setWidth("300px");
        add(buttonsLayout);
        add(listBox);
    }

    public void setItems(T... items) {
        this.listBox.setItems(items);
    }

    public void setItems(Collection<T> items) {
        this.listBox.setItems(items);
        this.listBox.getDataProvider().refreshAll();
    }

    public void setItems(Stream<T> items) {
        this.listBox.setItems(items);
    }

    public void setRenderer(ComponentRenderer<? extends Component, T> itemRenderer) {
        this.listBox.setRenderer(itemRenderer);
    }

}
