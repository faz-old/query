package de.faz.modules.query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class QueryExecutorTest {
    @Mock Query query;
    @Mock SearchSettings settings;

    @Mock(answer = Answers.CALLS_REAL_METHODS) private QueryExecutor executor;

    @Before
    public void setUp() {
        doAnswer(new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        }).when(executor).executeQuery(any(Query.class), any(SearchSettings.class));
    }

    @Test
    public void execute_withQueryAndSettings_callExecuteQuery() {
        executor.execute(query, settings);
        verify(executor).executeQuery(query, settings);
    }

}
