package de.faz.modules.query;

import de.faz.modules.query.util.SequenceSeparatedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
class ItemChain extends QueryItem {
	private final String separator;
	private final QueryItem[] items;

	public ItemChain(final QueryItem... items) {
		this.separator = " ";
		this.items = items;
	}

	public ItemChain(final String separator, final QueryItem... items) {
		this.separator = separator;
		List<QueryItem> itemList = new ArrayList<>(Arrays.asList(items));
		itemList.removeAll(Collections.singleton(null));
		this.items = itemList.toArray(new QueryItem[0]);
	}

	@Override
	public CharSequence toCharSequence() {
		StringBuffer sb = new StringBuffer();
		SequenceSeparatedList<QueryItem> itemList = new SequenceSeparatedList<>(Arrays.asList(items), separator);
		sb.append('(').append(itemList.toString()).append(')');
		return sb;
	}

	@Override
	public int hashCode() {
		int result = separator.hashCode();
		result = 31 * result + Arrays.hashCode(items);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ItemChain) {
			ItemChain chain = (ItemChain) obj;
			boolean containsAllItems = true;
			List<QueryItem> itemList = Arrays.asList(chain.items);
			for (QueryItem item : items) {
				containsAllItems &= itemList.contains(item);
			}
			return containsAllItems && separator.equals(chain.separator);
		}
		return super.equals(obj);
	}

	@Override
	public boolean contains(final QueryItem item) {
		boolean found = false;
		for (int i = 0, size = items.length; i < size && !found; i++) {
			QueryItem child = items[i];
			if (child.equals(item)) {
				found = true;
			} else {
				found = child.contains(item);
			}
		}
		return found;
	}
}
