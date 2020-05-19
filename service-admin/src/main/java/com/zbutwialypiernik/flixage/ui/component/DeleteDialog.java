package com.zbutwialypiernik.flixage.ui.component;

public class DeleteDialog<T> extends ConfirmDialog {

    private T entity;

    public DeleteDialog(String message) {
        super(message);
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

}
