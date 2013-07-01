package de.faz.modules.query;

import de.faz.modules.query.ModificationSelection;
import de.faz.modules.query.QueryModification;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class QueryModificationTest {

    @Mock Query q;

    private QueryModification modification;

    @Before
    public void setUp() {
        modification = new QueryModification(q);
    }

    @Test
    public void all_returnsSelectorWithEmptyRange() {
        ModificationSelection modificationSelection = modification.all();
        assertNull("the created modificationSelection should have no lower limit", modificationSelection.getLowerLimit());
        assertNull("the created modificationSelection should have no upper limit", modificationSelection.getUpperLimit());
    }

    @Test
    public void last_returnsSelectorWithRangeWhichHasUpperLimit() {
        ModificationSelection modificationSelection = modification.last();
        assertNull("the created modificationSelection should have no range with lower limit", modificationSelection.getLowerLimit());
        assertEquals(0, (int) modificationSelection.getUpperLimit());
    }

    @Test
    public void first_returnsSelectorWithLowerLimitRange() {
        ModificationSelection modificationSelection = modification.first();
        assertNull("the created modificationSelection should have no range with an upper limit", modificationSelection.getUpperLimit());
        assertEquals(0, (int) modificationSelection.getLowerLimit());
    }

    @Test
    public void range_withRange_returnsSelectorWithExpectedRange() {
        ModificationSelection modificationSelection = modification.range(1,2);
        assertEquals(1, (int) modificationSelection.getLowerLimit());
        assertEquals(2, (int) modificationSelection.getUpperLimit());
    }

    @Test(expected = IllegalArgumentException.class)
    public void range_WithNegativeLowerLimit_throwsIllegalArgumentException() {
        modification.range(-1, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void range_withUpperLimitIsLowerThanLowerLimit_throwsIllegalArgumentException() {
        modification.range(3, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void item_withNegativeIndex_throwsIllegalArgumentException() {
        modification.item(-1);
    }

    @Test
    public void item_withIndex_returnsSelectorWithRangWhereUpperLimitEqualsLowerLimit() {
        ModificationSelection modificationSelection = modification.item(0);
        assertEquals(0, (int) modificationSelection.getLowerLimit());
        assertEquals(0, (int) modificationSelection.getUpperLimit());
    }

    @Test(expected = IllegalArgumentException.class)
    public void last_withNegativeNumber_throwsIllegalArgumentException() {
        modification.last(-1);
    }

    @Test
    public void last_withNumber_returnsSelectorWhereUpperLimitOfRangeIsSet() {
        ModificationSelection modificationSelection = modification.last(3);
        assertNull("the lower limit of the created modificationSelection should be null", modificationSelection.getLowerLimit());
        assertEquals(3, (int) modificationSelection.getUpperLimit());
    }

    @Test(expected = IllegalArgumentException.class)
    public void first_withNegativeNumber_throwsIllegalArgumentException() {
        modification.first(-1);
    }

    @Test
    public void first_withNumber_returnsSelectorWhereLowerLimitOfRangeIsSet() {
        ModificationSelection modificationSelection = modification.first(3);
        assertNull("the upper limit of the created modificationSelection should be null", modificationSelection.getUpperLimit());
        assertEquals(3, (int) modificationSelection.getLowerLimit());
    }
}
