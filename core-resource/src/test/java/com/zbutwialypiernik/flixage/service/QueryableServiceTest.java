package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.repository.QueryableRepository;
import com.zbutwialypiernik.flixage.service.resource.image.ImageFileService;
import com.zbutwialypiernik.flixage.service.resource.image.ImageResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Generic test for every queryable service using queryable stub, some of methods in queryable
 * services are just calling repository layer without any additional logic, so testing them in unit test
 * totally unnecessary, because mocking repository layer in this case equals to mock whole method
*/
public class QueryableServiceTest {

    @InjectMocks
    private QueryableService<Queryable> service;

    @Mock
    private QueryableRepository<Queryable> repository;

    @Mock
    private ImageFileService thumbnailService;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private Queryable entity;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        service = new QueryableService<>(repository, thumbnailService);
    }

    @Test
    public void entity_without_id_does_get_created() {
        Mockito.when(repository.save(entity)).thenReturn(entity);
        entity = service.create(entity);

        verify(repository, times(1)).save(any());
    }

    @Test
    public void entity_does_get_updated_when_entity_exists_in_database() {
        String entityId = UUID.randomUUID().toString();

        Queryable oldEntity = mock(Queryable.class, Answers.CALLS_REAL_METHODS);
        oldEntity.setId(entityId);

        entity.setId(entityId);

        when(repository.findById(entityId)).thenReturn(Optional.of(oldEntity));
        when(repository.save(entity)).thenReturn(entity);

        entity = service.update(entity);

        verify(repository, times(1)).save(entity);
    }

    @Test
    public void entity_does_not_get_updated_when_entity_does_not_exists_in_database() {
        String entityId = UUID.randomUUID().toString();
        entity.setId(entityId);

        when(repository.findById(entityId)).thenReturn(Optional.empty());
        when(repository.save(entity)).thenReturn(entity);
        assertThrows(ResourceNotFoundException.class,
                () -> service.update(entity));

        verify(repository, never()).save(any());
    }

    @Test
    public void entity_does_get_deleted_when_does_exist_in_database() {
        String entityId = UUID.randomUUID().toString();
        entity.setId(entityId);

        Queryable oldEntity = mock(Queryable.class, Answers.CALLS_REAL_METHODS);
        oldEntity.setId(entityId);

        when(repository.findById(entityId)).thenReturn(Optional.of(oldEntity));
        when(repository.existsById(entityId)).thenReturn(true);

        service.delete(entity);

        verify(repository, times(1)).deleteById(entity.getId());
    }

    @Test
    public void entity_does_not_get_deleted_when_does_not_exist_in_database() {
        String entityId = UUID.randomUUID().toString();
        entity.setId(entityId);

        when(repository.findById(entityId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.delete(entity));
    }

    @Test
    public void can_save_thumbnail_when_entity_does_exists_in_database() {
        String entityId = UUID.randomUUID().toString();
        entity.setId(entityId);

        when(repository.existsById(entityId)).thenReturn(true);
        when(repository.findById(entityId)).thenReturn(Optional.of(entity));

        final var resource = Mockito.mock(ImageResource.class);

        service.saveThumbnail(entity, resource);

        verify(thumbnailService, times(1)).save(any(), eq(resource));
        assertNotNull(entity.getThumbnail());
    }

    @Test
    public void cannot_save_thumbnail_when_entity_does_not_exists_in_database() {
        String entityId = UUID.randomUUID().toString();
        entity.setId(entityId);

        when(repository.existsById(entityId)).thenReturn(false);

        final var resource = Mockito.mock(ImageResource.class);

        assertThrows(ResourceNotFoundException.class,
                () -> service.saveThumbnail(entity, resource));

        verify(thumbnailService, times(0)).save(any(), eq(resource));
        assertNull(entity.getThumbnail());
    }

}
