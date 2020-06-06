package com.zbutwialypiernik.flixage.ui.component.crud;

import com.vaadin.flow.data.provider.AbstractDataProvider;
import com.vaadin.flow.data.provider.Query;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class SingleCallbackDataProvider<T, F> extends AbstractDataProvider<T, F> {

    private final FetchCallback<T> callback;

    private transient Collection<T> cachedItems;

    public SingleCallbackDataProvider(FetchCallback<T> callback) {
        this.callback = callback;
    }

    @FunctionalInterface
    public interface FetchCallback<T> extends Serializable {

        Collection<T> fetch();

    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public Stream<T> fetch(Query<T, F> query) {
        cachedItems = callback.fetch();

        return cachedItems.stream().skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public int size(Query<T, F> query) {
        if (cachedItems == null) {
            cachedItems = callback.fetch();
        }

        return cachedItems.size();
    }


}
