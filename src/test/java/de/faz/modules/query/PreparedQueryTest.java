package de.faz.modules.query;

import de.faz.modules.query.fields.FieldDefinitionGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class PreparedQueryTest {

    TestMapping fieldDefinition;
    private PreparedQuery q;

    @Before
    public void setUp() {
        QueryExecutor executor = mock(QueryExecutor.class);
        DefaultSearchContext context = new MockedSearchContext(executor, new FieldDefinitionGenerator());
        q = context.createPreparedQuery();

        fieldDefinition = context.createFieldDefinitionFor(TestMapping.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void prepare_withoutValue_throwsInvalidQueryException() {
        q.param(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void prepare_withEmptyValue_throwsInvalidQueryException() {
        q.param("");
    }

    @Test
    public void prepare_withValue_returnsTipOfMissingParameterValue() {
        q.add(q.term(fieldDefinition.getField1()).value(q.param("testParam")));
        assertEquals("field1:<unset parameter 'testParam'>", q.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setParamValue_withoutParamName_throwsInvalidQueryException() {
        q.setParamValue(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setParamValue_withEmptyParamName_throwsInvalidQueryException() {
        q.setParamValue("", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setParamValue_withoutValue_throwsInvalidQueryException() {
        q.setParamValue("name", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setParamValue_withoutEmptyValue_throwsInvalidQueryException() {
        q.setParamValue("name", "");
    }

    @Test
    public void toString_WithParamAndValue_returnsReplacedValue() {
        q.add(q.term(fieldDefinition.getField1()).value(q.param("param")));
        q.setParamValue("param", "value");

        assertEquals("field1:value", q.toString());
    }

    @Test
    public void toString_WithParamAndValueInExpression_returnsReplacedValue() {
        q.add(
            q.and(
                q.term(fieldDefinition.getField1()).value(q.param("param1")),
                q.term(fieldDefinition.getField2()).value(q.param("param2"))
            )
        );
        q.setParamValue("param1", "value1");
        q.setParamValue("param2", "value2");

        assertEquals("(field1:value1 AND field2:value2)", q.toString());
    }

    @Test
    public void reset_withParamValueSet_clearParameterValue() {
        q.add(q.term(fieldDefinition.getField1()).value(q.param("param")));
        q.setParamValue("param", "value");
        q.reset();
        assertEquals("field1:<unset parameter 'param'>", q.toString());
    }
}
