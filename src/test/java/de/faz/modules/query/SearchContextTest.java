package de.faz.modules.query;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.management.Query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class SearchContextTest {

    @Mock QueryExecutor executor;

    @Mock FieldDefinitionGenerator generator;

    private DefaultSearchContext context;

    @Before
    public void setUp() {
        context = new DefaultSearchContext(executor, generator);
    }

    @Test
    public void createQuery_createsNewInstance() {
        assertNotNull(context.createQuery());
    }

    @Test
    public void createPreparedQuery_createsNewInstance() {
        assertNotNull(context.createPreparedQuery());
    }

    @Test(expected = IllegalArgumentException.class)
    public void execute_withoutQuery_throwsIllegalArgumentException() {
        context.execute(null);
    }

    @Test
    public void execute_withQuery_callsExecutor() {
        Query q = mock(Query.class);
        context.execute(q);
        verify(executor).execute(Matchers.eq(q), Matchers.any(SearchSettings.class));
    }

    @Test
    public void execute_withQueryAndSettings_callsExecutor() {
        Query q = mock(Query.class);
        SearchSettings settings = mock(SearchSettings.class);
        context.execute(q, settings);
        verify(executor).execute(q, settings);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createFieldDefinitionFor_withoutClass_throwsIllegalArgumentException() {
        context.createFieldDefinitionFor(null);
    }

    @Test
    public void createFieldDefinitionFor_WithClass_callsGenerator() {
        context.createFieldDefinitionFor(TestMapping.class);
        verify(generator).createFieldDefinition(TestMapping.class);
    }
}
