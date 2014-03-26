/*
 * Copyright (c) 2013. F.A.Z. Electronic Media GmbH
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of F.A.Z. Electronic Media GmbH and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to F.A.Z. Electronic Media GmbH
 * and its suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from F.A.Z. Electronic Media GmbH.
 */
package de.faz.modules.query;

import javax.annotation.Nonnull;
import java.util.Stack;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public abstract class AbstractQueryDecorator extends Query {

	private final Query delegate;

	public AbstractQueryDecorator(@Nonnull final Query q) {
		super(null);
		delegate = q;
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	@Override
	public QueryModification modify() {
		return delegate.modify();
	}

	@Override
	public QueryItem and(final QueryItem item, final QueryItem... items) {
		return delegate.and(item, items);
	}

	@Override
	public QueryItem or(final QueryItem item, final QueryItem... items) {
		return delegate.or(item, items);
	}

	@Override
	public QueryItem not(final QueryItem item) {
		return delegate.not(item);
	}

	@Override
	public Query add(final QueryItem item) {
		return delegate.add(item);
	}

	@Override
	public Query addItemsOf(final Query query) {
		return delegate.addItemsOf(query);
	}

	@Override
	public TermQueryPart term(final Object fieldDefinition) {
		return delegate.term(fieldDefinition);
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return delegate.equals(obj);
	}

	@Override
	public boolean contains(final QueryItem item) {
		return delegate.contains(item);
	}

	@Override
	Stack<QueryItem> getItemStack() {
		return delegate.getItemStack();
	}
}
