package de.faz.modules.query;

import de.faz.modules.query.util.SequenceSeparatedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
		Object[] values = new Object[items.length + 1];
		System.arraycopy(items, 0, values, 0, items.length);
		values[values.length-1] = separator;
		return Objects.hash(values);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ItemChain) {
			ItemChain chain = (ItemChain) obj;
			return Objects.equals(separator, chain.separator)
				&& Arrays.asList(items).containsAll(Arrays.asList(chain.items));
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
