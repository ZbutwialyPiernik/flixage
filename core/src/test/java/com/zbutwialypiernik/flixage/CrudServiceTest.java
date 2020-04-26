package com.zbutwialypiernik.flixage;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.zbutwialypiernik.flixage.entity.BaseEntity;
import com.zbutwialypiernik.flixage.exception.BadRequestException;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.service.CrudService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.repository.CrudRepository;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

public class CrudServiceTest {

    @InjectMocks
    private CrudService<BaseEntity> service;

    @Mock
    private CrudRepository<BaseEntity, String> repository;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private BaseEntity entity;

    private final Clock clock = Clock.fixed(Instant.parse("2020-04-25T00:00:00.00Z"), ZoneId.of("UTC"));

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        service = new CrudService<>(repository, clock);
    }

    @Test
    public void entity_without_id_does_get_created() {
        LocalDateTime creationTime = LocalDateTime.now(clock);
        entity.setId(null);

        service.create(entity);

        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any());

        Assertions.assertTrue(entity.getCreationTime().isEqual(creationTime));
        Assertions.assertTrue(entity.getLastUpdateTime().isEqual(creationTime));
    }

    @Test
    public void entity_with_id_does_not_get_not_created() {
        Mockito.when(entity.getId()).thenReturn(UUID.randomUUID().toString());

        Assertions.assertThrows(BadRequestException.class,
                () -> service.create(entity));

        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void entity_does_get_updated_when_entity_exists_in_database() {
        LocalDateTime creationTime = LocalDateTime.now(clock).minus(5, ChronoUnit.MINUTES);
        LocalDateTime updateTime = LocalDateTime.now(clock);

        String entityId = UUID.randomUUID().toString();

        BaseEntity oldEntity = Mockito.mock(BaseEntity.class, Answers.CALLS_REAL_METHODS);
        oldEntity.setId(entityId);
        oldEntity.setCreationTime(creationTime);
        oldEntity.setLastUpdateTime(creationTime);

        entity.setId(entityId);
        entity.setCreationTime(creationTime);
        entity.setLastUpdateTime(creationTime);

        Mockito.when(repository.findById(entityId)).thenReturn(Optional.of(oldEntity));

        service.update(entity);

        // Entity should get updated and saved
        Mockito.verify(repository, Mockito.times(1)).save(entity);
        Assertions.assertTrue(entity.getCreationTime().isEqual(creationTime));
        Assertions.assertTrue(entity.getLastUpdateTime().isEqual(updateTime));
    }

    @Test
    public void entity_does_not_get_updated_when_entity_does_not_exists_in_database() {
        LocalDateTime creationTime = LocalDateTime.now(clock).minus(5, ChronoUnit.MINUTES);

        String entityId = UUID.randomUUID().toString();
        entity.setId(entityId);
        entity.setCreationTime(creationTime);
        entity.setLastUpdateTime(creationTime);

        Mockito.when(repository.findById(entityId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> service.update(entity));


        // Entity shouldn't get updated or saved
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
        Assertions.assertTrue(entity.getCreationTime().isEqual(creationTime));
        Assertions.assertTrue(entity.getLastUpdateTime().isEqual(creationTime));
    }

    @Test
    public void entity_does_not_get_updated_when_entity_has_other_creation_time_than_existing_in_database() {
        LocalDateTime creationTime = LocalDateTime.now(clock);
        LocalDateTime newCreationTime = LocalDateTime.now(clock);

        String entityId = UUID.randomUUID().toString();

        BaseEntity oldEntity = Mockito.mock(BaseEntity.class, Answers.CALLS_REAL_METHODS);
        oldEntity.setId(entityId);
        oldEntity.setCreationTime(creationTime);
        oldEntity.setLastUpdateTime(creationTime);

        entity.setId(entityId);
        entity.setCreationTime(newCreationTime);
        entity.setLastUpdateTime(creationTime);

        Mockito.when(repository.findById(entityId)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalStateException.class,
                () -> service.update(entity));

        // Entity shouldn't get updated
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
        Assertions.assertTrue(entity.getCreationTime().isEqual(creationTime));
        Assertions.assertTrue(entity.getLastUpdateTime().isEqual(creationTime));
    }

    public void entity_does_get_deleted_when_does_exist_in_database() {

    }

    public void entity_does_get_deleted_when_does_not_exist_in_database() {

    }

}
