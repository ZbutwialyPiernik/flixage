package com.zbutwialypiernik.flixage.ui.component.crud.mapper;

@FunctionalInterface
public interface MapperFactory {

     <T, D> BidirectionalMapper<T, D> createMapper();

}
