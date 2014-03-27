package de.faz.modules.query;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Andreas Kaubisch <a.kaubisch@faz.de>
 * @since $rev$
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemChainTest extends TestCase {

	@Mock QueryItem queryItem;

	@Test
	public void hashCode_setsOnlyOneItemToSet() {
		ItemChain chain1 = new ItemChain(queryItem);
		ItemChain chain2 = new ItemChain(queryItem);
		Set<ItemChain> chainSet = new HashSet<>();
		chainSet.add(chain1);
		chainSet.add(chain2);
		assertEquals(1, chainSet.size());
	}
}
