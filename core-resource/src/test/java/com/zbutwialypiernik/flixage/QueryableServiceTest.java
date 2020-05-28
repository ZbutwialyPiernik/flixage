package com.zbutwialypiernik.flixage;

import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.exception.BadRequestException;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.repository.QueryableRepository;
import com.zbutwialypiernik.flixage.service.QueryableService;
import com.zbutwialypiernik.flixage.service.file.ThumbnailFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QueryableServiceTest {

    @InjectMocks
    private QueryableService<Queryable> service;

    @Mock
    private QueryableRepository<Queryable> repository;

    @Mock
    private ThumbnailFileService thumbnailService;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private Queryable entity;

    private final Clock clock = Clock.fixed(Instant.parse("2020-04-25T00:00:00.00Z"), ZoneId.of("UTC"));

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        service = new QueryableService<>(repository, thumbnailService, clock);
    }

    @Test
    public void entity_without_id_does_get_created() {
        Instant creationTime = Instant.now(clock);
        entity.setId(null);

        service.create(entity);

        verify(repository, times(1)).save(any());

        assertEquals(entity.getCreationTime(), creationTime);
        assertEquals(entity.getLastUpdateTime(), creationTime);
    }

    @Test
    public void entity_with_id_does_not_get_not_created() {
        when(entity.getId()).thenReturn(UUID.randomUUID().toString());

        assertThrows(BadRequestException.class,
                () -> service.create(entity));

        verify(repository, never()).save(any());
    }

    @Test
    public void entity_does_get_updated_when_entity_exists_in_database() {
        Instant creationTime = Instant.now(clock).minus(new Random().nextInt(), ChronoUnit.MINUTES);
        Instant updateTime = Instant.now(clock);

        String entityId = UUID.randomUUID().toString();

        Queryable oldEntity = mock(Queryable.class, Answers.CALLS_REAL_METHODS);
        oldEntity.setId(entityId);
        oldEntity.setCreationTime(creationTime);
        oldEntity.setLastUpdateTime(creationTime);

        entity.setId(entityId);
        entity.setCreationTime(creationTime);
        entity.setLastUpdateTime(creationTime);

        when(repository.findById(entityId)).thenReturn(Optional.of(oldEntity));

        service.update(entity);

        // Entity should get updated and saved
        verify(repository, times(1)).save(entity);
        assertEquals(entity.getCreationTime(),creationTime);
        assertEquals(entity.getLastUpdateTime(),updateTime);
    }

    @Test
    public void entity_does_not_get_updated_when_entity_does_not_exists_in_database() {
        Instant creationTime = Instant.now(clock).minus(new Random().nextInt(), ChronoUnit.MINUTES);

        String entityId = UUID.randomUUID().toString();
        entity.setId(entityId);
        entity.setCreationTime(creationTime);
        entity.setLastUpdateTime(creationTime);

        when(repository.findById(entityId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(entity));


        // Entity shouldn't get updated or saved
        verify(repository, never()).save(any());
        assertEquals(entity.getCreationTime(), creationTime);
        assertEquals(entity.getLastUpdateTime(), creationTime);
    }

    public void entity_does_get_deleted_when_does_exist_in_database() {
        String entityId = UUID.randomUUID().toString();
        entity.setId(entityId);

        Queryable oldEntity = mock(Queryable.class, Answers.CALLS_REAL_METHODS);
        oldEntity.setId(entityId);

        when(repository.findById(entityId)).thenReturn(Optional.of(oldEntity));

        service.delete(entity);

        verify(repository, times(1)).delete(entity);
    }

    public void entity_does_get_deleted_when_does_not_exist_in_database() {
        String entityId = UUID.randomUUID().toString();
        entity.setId(entityId);

        when(repository.findById(entityId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.delete(entity));
    }

}
