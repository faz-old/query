package de.faz.modules.query;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class QueryTest {
    TestMapping fieldDefinition;
    SearchContext context;
    private Query q;

    @Before
    public void setUp() {
        QueryExecutor executor = mock(QueryExecutor.class);
        context = new DefaultSearchContext(executor);
        q = context.createQuery();
        fieldDefinition = context.createFieldDefinitionFor(TestMapping.class);
    }

    @Test
    public void term_withValues_returnsFlattendQuery() {
        q.term(fieldDefinition.getField1(), "fieldValue");
        assertEquals("field1:fieldValue", q.toString());
    }

    @Test(expected = InvalidQueryException.class)
    public void term_withMissingKey_throwsInvalidQueryException() {
        q.term(null, "fieldValue");
    }

    @Test(expected = InvalidQueryException.class)
    public void term_withUnpreparedMapping_throwsQueryException() {
        TestMapping mapping = new TestMapping();
        q.term(mapping.getField1(), "fieldValue");
    }

    @Test
    public void term_withMultipleCalls_returnsConcatenatedValue() {
        q
            .term(fieldDefinition.getField1(), "value1")
            .term(fieldDefinition.getField2(), "value2");

        assertEquals("(field1:value1 field2:value2)", q.toString());
    }

    @Test
    public void term_withBoostedValue_returnsBoostedValue() {
        q.term(fieldDefinition.getBoostedField1(), "boostedValue");
        assertEquals("field1:boostedValue^2", q.toString());
    }

    @Test
    public void term_withWhitespaceValue_returnsQueryWithEscapedValue() {
        q.term(fieldDefinition.getField1(), "value 1");
        assertEquals("field1:value 1", q.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void and_withoutItem_throwsInvalidQueryException() {
        q.and((Query)null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void and_withOnlyOneItem_throwsInvalidQueryException() {
        q.and(
            q.term(fieldDefinition.getField1(), "fieldValue")
        );
    }

    @Test
    public void and_withManyItems_returnsAndString() {
        q
            .and(
                q.term(fieldDefinition.getField1(), "value1"),
                q.term(fieldDefinition.getField2(), "value2")
            );
        assertEquals("(field1:value1 AND field2:value2)", q.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void or_withOnlyOneItem_throwsInvalidQueryException() {
        q.or(
            q.term(fieldDefinition.getField1(), "value")
        );
    }

    @Test
    public void or_WithManyItems_returnsOrString() {
        q.or(
            q.term(fieldDefinition.getField1(), "value1"),
            q.term(fieldDefinition.getField2(), "value2")
        );

        assertEquals("(field1:value1 OR field2:value2)", q.toString());
    }

    @Test
    public void not_withoutValue_returnsNull() {
        assertNull("not should return null when the argument is null", q.not((Query)null));
    }

    @Test
    public void not_WithValue_returnsNotString() {
        q.not(q.term(fieldDefinition.getField1(), "fieldValue"));
        assertEquals("NOT (field1:fieldValue)", q.toString());
    }

    @Test
    public void not_withValueSurroundedByBrackets_returnNotValueWithouAdditionalBrackets() {
        q.not(
            q.and(
                q.term(fieldDefinition.getField1(), "value1"),
                q.term(fieldDefinition.getField2(), "value2")
            )
        );
        assertEquals("NOT (field1:value1 AND field2:value2)", q.toString());
    }

    @Test
    public void add_withQueryItem_addItemToQuery() {
        Query.QueryItem item = new Query.QueryItem() {
            @Override
            CharSequence toCharSequence() {
                return "query item";
            }

            @Override
            public boolean contains(final Query.QueryItem item) {
                return false;
            }
        };
        assertEquals("query item", q.add(item).toString());
    }

    @Test
    public void term_withFieldDefinition_returnsNewTermQueryPart() {
        assertNotNull("the 'term' method should return a new TermQueryPart instance.", q.term(fieldDefinition.getField1()));
    }

    @Test
    public void and_withQueryItemValues_returnNewQueryItemWithEmbeddedItems() {
        Query.QueryItem item = q.and(
                q.term(fieldDefinition.getField1()).value("field1"),
                q.term(fieldDefinition.getField2()).value("field2")
        );
        assertEquals("(field1:field1 AND field2:field2)", item.toString());
    }

    @Test
    public void or_withQueryItemValues_returnNewQueryItemWithEmbeddedItems() {
        Query.QueryItem item = q.or(
                q.term(fieldDefinition.getField1()).value("field1"),
                q.term(fieldDefinition.getField2()).value("field2")
        );
        assertEquals("(field1:field1 OR field2:field2)", item.toString());
    }

    @Test
    public void not_withQueryItem_returnNewQueryItemWithEmbeddedItem() {
        Query.QueryItem item = q.not(
                q.term(fieldDefinition.getField1()).value("field1")
        );
        assertEquals("NOT (field1:field1)", item.toString());
    }

    @Test
    public void equals_withEqualQueries_equalsIsTrue() {
        q.add(
            q.and(
                    q.term(fieldDefinition.getField1()).value("field1"),
                    q.term(fieldDefinition.getField2()).value("field2")
            )
        );
        Query q2 = context.createQuery();
        q2.add(
            q2.and(
                q2.term(fieldDefinition.getField1()).value("field1"),
                q2.term(fieldDefinition.getField2()).value("field2")
            )
        );
        assertEquals(q, q2);
    }


    @Test
    public void contains_withExistingQueryItem_returnsTrue() {
        q.add(
            q.term(fieldDefinition.getField1()).value("field1")
        );
        assertTrue(q.contains(q.term(fieldDefinition.getField1()).value("field1")));
    }

    @Test
    public void contains_withExistingNestedQueryItem_returnsTrue() {
        q.add(
            q.and(
                q.term(fieldDefinition.getField1()).value("field1"),
                q.term(fieldDefinition.getField2()).value("field2")
            )
        );
        assertTrue(q.contains(q.term(fieldDefinition.getField1()).value("field1")));
    }

    @Test
    public void contains_withExistingDeeplyNestedQueryItem_returnsTrue() {
        q.add(
            q.and(
                q.or(
                    q.term(fieldDefinition.getField1()).value("field1"),
                    q.term(fieldDefinition.getField2()).value("field2")
                ),
                q.term(fieldDefinition.getField1()).value("field3")
            )
        );
        assertTrue(q.contains(q.term(fieldDefinition.getField1()).value("field1")));
    }
    
    @Test
    public void equalsOperatorItem_withEqualItems_equalsIsTrue() {
        Query.QueryItem item1 = q.not(
                q.term(fieldDefinition.getField1()).value("field1")
        );
        Query.QueryItem item2 = q.not(
                q.term(fieldDefinition.getField1()).value("field1")
        );
        assertEquals(item1, item2);
    }

    @Test
    public void equalsOperatorItem_withDifferentItems_equalsIsFalse() {
        Query.QueryItem item1 = q.not(
                q.term(fieldDefinition.getField1()).value("field1")
        );
        Query.QueryItem item2 = q.not(
                q.term(fieldDefinition.getField1()).value("field2")
        );
        assertNotEquals(item1, item2);
    }

    @Test
    public void equalsItemChain_withEqualOrderedItems_equalsIsTrue() {
        Query.QueryItem item1 = q.and(
            q.term(fieldDefinition.getField1()).value("field1"),
            q.term(fieldDefinition.getField2()).value("field2")
        );

        Query.QueryItem item2 = q.and(
            q.term(fieldDefinition.getField1()).value("field1"),
            q.term(fieldDefinition.getField2()).value("field2")
        );

        assertEquals(item1, item2);
    }

    @Test
    public void equalsItemChain_withEqualUnOrderedItems_equalsIsTrue() {
        Query.QueryItem item1 = q.and(
            q.term(fieldDefinition.getField1()).value("field1"),
            q.term(fieldDefinition.getField2()).value("field2")
        );

        Query.QueryItem item2 = q.and(
            q.term(fieldDefinition.getField2()).value("field2"),
            q.term(fieldDefinition.getField1()).value("field1")
        );

        assertEquals(item1, item2);
    }

    @Test
    public void equalsItemChain_withDifferentItems_equalsIsFalse() {
        Query.QueryItem item1 = q.and(
            q.term(fieldDefinition.getField1()).value("field1"),
            q.term(fieldDefinition.getField2()).value("field2")
        );

        Query.QueryItem item2 = q.and(
            q.term(fieldDefinition.getField1()).value("field2"),
            q.term(fieldDefinition.getField2()).value("field3")
        );

        assertNotEquals(item1, item2);
    }
}