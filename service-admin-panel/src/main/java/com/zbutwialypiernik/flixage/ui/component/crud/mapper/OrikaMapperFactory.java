package com.zbutwialypiernik.flixage.ui.component.crud.mapper;

import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Component;

@Component
public class OrikaMapperFactory implements MapperFactory {

    private final MapperFacade facade;

    public OrikaMapperFactory(MapperFacade facade) {
        this.facade = facade;
    }

    public <T, D> BidirectionalMapper<T, D> createConverter() {
        return new BidirectionalMapper<>() {
            @Override
            public D mapTo(T t, D d) {
                facade.map(t, d);
                return d;
            }

            @Override
            public T mapFrom(D d, T t) {
                facade.map(d, t);
                return t;
            }
        };
    }


}
