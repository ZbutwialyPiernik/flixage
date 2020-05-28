package com.zbutwialypiernik.flixage.config.mapping;

import java.beans.PropertyEditorSupport;

public class CaseInsensitiveEnumEditor<T extends Enum<T>> extends PropertyEditorSupport {
    private final Class<T> type;
    private final String[] enumNames;

    public CaseInsensitiveEnumEditor(Class<T> type) {
        this.type = type;
        var values = type.getEnumConstants();
        if (values == null) {
            throw new IllegalArgumentException("Unsupported " + type);
        }
        this.enumNames = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            this.enumNames[i] = values[i].name();
        }
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null || text.isEmpty()) {
            setValue(null);
            return;
        }
        for (String n : enumNames) {
            if (n.equalsIgnoreCase(text)) {
                var newValue = Enum.valueOf(type, n);
                setValue(newValue);
                return;
            }
        }
        throw new IllegalArgumentException("No enum constant " + type.getCanonicalName() + " equals ignore case " + text);
    }

}
