package de.faz.modules.query;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Stack;

import de.faz.modules.query.ModificationSelection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class ModificationSelectionTest {

    @Mock Query q;

    private ModificationSelection selection;


    @Before
    public void setUp() {
        selection = new ModificationSelection(q, null, null);
    }

    @Test
    public void remove_withoutRange_removeAllElements() {
        Stack<Query.QueryItem> itemStack = mock(Stack.class);
        when(q.getItemStack()).thenReturn(itemStack);

        selection.remove();

        verify(itemStack).removeAllElements();
    }

    @Test
    public void surroundWith_withoutRangeAndAndMethod_removeAllItemsAndAddNewChainElementWithElements() {
        ItemStackContainer itemStackContainer = createItemStackContainerWithElementCount(2);
        Stack<Query.QueryItem> itemStack = itemStackContainer.stack;

        selection.surroundWith().and();

        assertEquals(1, itemStack.size());
        verify(itemStack).removeAllElements();
        Query.QueryItem[] items = createQueryItemArrayWithCountOf(itemStackContainer);
        verify(q).and(itemStackContainer.item, Arrays.copyOf(items, items.length - 1));
    }

    @Test
    public void surroundWith_withAndMethodAndOnlyOneItem_doNotRemoveQueryElements() {
        ItemStackContainer itemStackContainer = createItemStackContainerWithElementCount(1);
        Stack<Query.QueryItem> itemStack = itemStackContainer.stack;

        selection.surroundWith().and();

        assertEquals(1, itemStack.size());
        verify(itemStack, never()).removeAllElements();
    }

    @Test
    public void surroundWith_withoutRangeAndOrMethod_removeAllItemsAndAddNewChainElementWithElements() {
        ItemStackContainer itemStackContainer = createItemStackContainerWithElementCount(2);
        Stack <Query.QueryItem> itemStack = itemStackContainer.stack;

        selection.surroundWith().or();

        assertEquals(1, itemStack.size());
        verify(itemStack).removeAllElements();
        Query.QueryItem[] items = createQueryItemArrayWithCountOf(itemStackContainer);
        verify(q).or(itemStackContainer.item, Arrays.copyOf(items, items.length - 1));
    }

    private ItemStackContainer createItemStackContainerWithElementCount(int count) {
        Stack<Query.QueryItem> itemStack = spy(new Stack<Query.QueryItem>());
        Query.QueryItem item = mock(Query.QueryItem.class);
        for(int i = 0; i++ < count;) {
            itemStack.push(item);
        }
        when(q.getItemStack()).thenReturn(itemStack);
        return new ItemStackContainer(itemStack, item, count);
    }

    private Query.QueryItem[] createQueryItemArrayWithCountOf(ItemStackContainer container) {
        Query.QueryItem[] items = new Query.QueryItem[container.count];
        for(int i = 0; i < container.count; i++) {
            items[i] = container.item;
        }

        return items;
    }

    private class ItemStackContainer {
        public final int count;
        public final Stack<Query.QueryItem> stack;
        public final Query.QueryItem item;

        private ItemStackContainer(final Stack<Query.QueryItem> stack, final Query.QueryItem item, final int count) {
            this.stack = stack;
            this.item  = item;
            this.count = count;
        }
    }
}
