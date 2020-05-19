package flixage;

import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.service.CrudService;
import com.zbutwialypiernik.flixage.service.QueryableService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class QueryableServiceTest {

    private QueryableService<Queryable> service;

    @Mock
    private CrudService<Queryable> crudService;

    @Mock
    private Queryable queryable;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void entity_gets_created() {

    }

}
