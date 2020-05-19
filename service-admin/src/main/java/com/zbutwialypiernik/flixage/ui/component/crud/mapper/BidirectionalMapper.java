package com.zbutwialypiernik.flixage.ui.component.crud.mapper;

public interface BidirectionalMapper<T, D> {

    D mapTo(T t, D d);

    T mapFrom(D d, T t);

}
