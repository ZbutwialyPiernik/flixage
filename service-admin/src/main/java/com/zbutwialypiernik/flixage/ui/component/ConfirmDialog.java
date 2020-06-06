package com.zbutwialypiernik.flixage.ui.component;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;

public class ConfirmDialog extends Dialog {

    public static class ConfirmEvent extends ComponentEvent<ConfirmDialog> {

        public ConfirmEvent(ConfirmDialog source) {
            super(source, false);
        }
    }

    public static class CloseEvent extends ComponentEvent<ConfirmDialog> {

        public CloseEvent(ConfirmDialog source) {
            super(source, false);
        }

    }

    public ConfirmDialog(Component... components) {
        Button confirmButton = new Button("Confirm");
        Button closeButton = new Button("Close");

        closeButton.addClickListener(event -> fireEvent(new CloseEvent(this)));
        confirmButton.addClickListener(event -> fireEvent(new ConfirmEvent(this)));

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.add(closeButton, confirmButton);

        add(components);
        add(buttonsLayout);
    }

    public ConfirmDialog(String message) {
        this(new Text(message));
    }

    public ConfirmDialog(ComponentEventListener<ConfirmEvent> confirmListener, ComponentEventListener<CloseEvent> closeListener, Component... components) {
        this(components);
        addConfirmListener(confirmListener);
        addCloseListener(closeListener);
    }

    public Registration addConfirmListener(ComponentEventListener<ConfirmEvent> listener) {
        return addListener(ConfirmEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

}
