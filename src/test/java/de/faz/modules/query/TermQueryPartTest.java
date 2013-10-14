package de.faz.modules.query;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.common.util.DateUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class TermQueryPartTest {
    private FieldDefinitionGenerator.FieldDefinition definition;
    private TermQueryPart part;

    @Before
    public void setUp() {
        definition = new FieldDefinitionGenerator.FieldDefinition("fieldName", 1);
        part = new TermQueryPart(definition);
    }

    @Test
    public void value_withString_returnsNotNull() {
        assertNotNull("the value method should return a Query.QueryItem.", part.value("test"));
    }

    @Test
    public void value_withString_returnsItemWithCorrectToString() {
        assertEquals("fieldName:test", part.value("test").toString());
    }

    @Test
    public void value_withStringAndBoost_returnsItemWithCorrectToString() {
        FieldDefinitionGenerator.FieldDefinition newDef = definition.setBoost(2);
        part = new TermQueryPart(newDef);
        assertEquals("fieldName:test^2", part.value("test").toString());
    }

    @Test
    public void value_withSpaceSeparatedValue_returnEscapedToString() {
        assertEquals("fieldName:space\\ test", part.value("space test").toString());
    }

    @Test
    public void value_withValueItem_returnsToStringWithValueItem() {
        Query.ValueItem item = new Query.ValueItem() {
            @Override
            CharSequence toCharSequence() {
                return "value_from_item";
            }
        };
        assertEquals("fieldName:value_from_item", part.value(item).toString());
    }

    @Test
    public void value_withPrefixQuantifier_returnsUnescapedQuantifier() {
        assertEquals("fieldName:*test", part.value("*test").toString());
    }

	@Test
	public void values_withOneValue_returnsQueryLikeValueMethod() {
		assertEquals("fieldName:value1", part.values("value1").toString());
	}

	@Test
	public void values_withOneValueAndOperator_returnsQueryWithoutOperator() {
		assertEquals("fieldName:value1", part.values(TermQueryPart.Operator.AND, "value1").toString());
	}

	@Test
	public void values_withSpaceSeparatedValues_returnEscapedToString() {
		assertEquals("fieldName:(space\\ value1 OR space\\ value2)", part.values("space value1", "space value2").toString());
	}

	@Test
	public void values_withQuantifiers_returnsNotEscapedQuantifier() {
		assertEquals("fieldName:(space\\ value1* OR *space\\ value2)", part.values("space value1*", "*space value2").toString());
	}

    @Test
    public void values_withValues_returnsNotNull() {
        assertNotNull("the values method should return a Query.QueryItem.", part.values("test1", "test2"));
    }

    @Test
    public void values_WithManyValues_returnsOrAssembledToString() {
        assertEquals("fieldName:(space OR test)", part.values("space", "test").toString());
    }

    @Test
    public void values_withManyValuesAndOperator_returnsOperatorAssembledToString() {
        assertEquals("fieldName:(space AND test)", part.values(TermQueryPart.Operator.AND, "space", "test").toString());
    }

    @Test
    public void range_withValues_returnsNotNull() {
        Date now = new Date();
        assertNotNull("the range method should return a Query.QueryItem instance.", part.range(now, now));
    }

    @Test
    public void range_withValues_returnsCorrectToString() {
        DateFormat format = DateUtil.getThreadLocalDateFormat();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -1);
        Date to = new Date();
        Date from = cal.getTime();
        String fromString = format.format(from);
        String toString = format.format(to);

        assertEquals("fieldName:["+ fromString +" TO "+ toString +"]", part.range(from, to).toString());
    }

	@Test
	public void range_withHours_checkUtcConversion() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));

		Calendar from = DateUtils.truncate(Calendar.getInstance(TimeZone.getTimeZone("GMT+2")), Calendar.HOUR_OF_DAY);
		from.set(Calendar.HOUR_OF_DAY, 3);
		Calendar to = DateUtils.truncate(Calendar.getInstance(TimeZone.getTimeZone("GMT+2")), Calendar.HOUR_OF_DAY);
		to.set(Calendar.HOUR_OF_DAY, 4);

		String fromString = format.format(from.getTime());
		String toString = format.format(to.getTime());

		assertEquals("fieldName:[" + fromString + " TO " + toString + "]", part.range(from, to).toString());
	}

    @Test
    public void range_WithFromAndWildcardValue_returnsCorrectToString() {
        DateFormat format = DateUtil.getThreadLocalDateFormat();
        Date from = new Date();

        assertEquals("fieldName:["+format.format(from) + " TO *]", part.range(DateOption.from(from), DateOption.WILDCARD).toString());
    }

    @Test
    public void range_withFromAndNowValue_returnsCorrectToString() {
        DateFormat format = DateUtil.getThreadLocalDateFormat();
        Date from = new Date();
        assertEquals("fieldName:["+format.format(from) + " TO NOW]", part.range(DateOption.from(from), DateOption.NOW).toString());
    }

    @Test
    public void range_withMinusGenericFromAndWildcardValue_returnsCorrectToString() {
        assertEquals("fieldName:[NOW-10DAYS TO *]", part.range(DateOption.nowMinus(10, DateOption.TimeUnit.DAYS), DateOption.WILDCARD).toString());
    }

    @Test
    public void range_withPlusGenerixFromAndWildcardValue_returnsCorrectToString() {
        assertEquals("fieldName:[NOW+10DAYS TO *]", part.range(DateOption.nowPlus(10, DateOption.TimeUnit.DAYS), DateOption.WILDCARD).toString());
    }

    @Test
    public void equals_withEqualDateRange_equalsIsTrue() {
        Query.QueryItem item = part.range(DateOption.WILDCARD, DateOption.NOW);
        Query.QueryItem item2 = part.range(DateOption.WILDCARD, DateOption.NOW);
        Assert.assertEquals(item, item2);
    }

    @Test
    public void equals_withSpecificDateRange_equalsIsTrue() {
        Date now = new Date();
        Query.QueryItem item = part.range(DateOption.from(now), DateOption.NOW);
        Query.QueryItem item2 = part.range(DateOption.from(now), DateOption.NOW);
        Assert.assertEquals(item, item2);
    }

}
