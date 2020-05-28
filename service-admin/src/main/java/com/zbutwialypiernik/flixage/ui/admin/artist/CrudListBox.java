package com.zbutwialypiernik.flixage.ui.admin.artist;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.service.file.resource.ImageResource;
import com.zbutwialypiernik.flixage.ui.component.DeleteDialog;
import com.zbutwialypiernik.flixage.ui.component.crud.DtoFormDialog;

import java.util.Collection;

public class CrudListBox<T extends Queryable> extends VerticalLayout {

    public interface DataProvider<T extends Queryable> {

        T create(T entity, ImageResource imageResource);

        T update(T entity, ImageResource imageResource);

        void delete(T entity);

        Collection<T> getAll();

    }

    private final Button addButton;
    private final Button updateButton;
    private final Button deleteButton;
    private final ListBox<T> listBox;

    private final DtoFormDialog<T, ?> createFormDialog;
    private final DtoFormDialog<T, ?> updateFormDialog;
    private final DeleteDialog<T> deleteDialog;

    private final DataProvider<T> dataProvider;

    public CrudListBox(DtoFormDialog<T, ?> createFormDialog, DtoFormDialog<T, ?> updateFormDialog, DataProvider<T> dataProvider) {
        this.createFormDialog = createFormDialog;

        this.updateFormDialog = updateFormDialog;
        this.dataProvider = dataProvider;
        this.deleteDialog = new DeleteDialog<>("Are you sure you wanna delete?");
        this.listBox = new ListBox<>();

        addButton = new Button("Create", (event) -> createFormDialog.open());
        updateButton = new Button("Update", (event) -> {
            updateFormDialog.setEntity(listBox.getValue());
            updateFormDialog.open();
        });
        deleteButton = new Button("Delete", (event) -> {
            deleteDialog.setEntity(listBox.getValue());
            deleteDialog.open();
        });

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        listBox.addValueChangeListener((event) -> {
            updateButton.setEnabled(event.getValue() != null);
            deleteButton.setEnabled(event.getValue() != null);
        });

        deleteDialog.addConfirmListener((event) -> {
            dataProvider.delete(deleteDialog.getEntity());
            deleteDialog.close();
            refresh();
        });

        deleteDialog.addCloseListener((event) -> deleteDialog.close());

        createFormDialog.addSubmitListener((event) -> {
            dataProvider.create(event.getEntity(), event.getImageResource());
            createFormDialog.clear();
            createFormDialog.close();
            refresh();
        });

        updateFormDialog.addSubmitListener((event) -> {
           dataProvider.update(event.getEntity(), event.getImageResource());
            updateFormDialog.clear();
            updateFormDialog.close();
           refresh();
        });

        HorizontalLayout buttonsLayout = new HorizontalLayout(addButton, updateButton, deleteButton);
        buttonsLayout.setWidthFull();
        buttonsLayout.setAlignItems(FlexComponent.Alignment.END);

        add(buttonsLayout);
        add(listBox);
    }

    public void setRenderer(ComponentRenderer<? extends Component, T> itemRenderer) {
        listBox.setRenderer(itemRenderer);
    }

    public void refresh() {
        listBox.setItems(dataProvider.getAll());
    }

}
