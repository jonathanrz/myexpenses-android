package br.com.jonathanzanella.myexpenses.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzanella on 11/10/16.
 */

public class Where {
	private class Query {
		Fields field;
		String operation;

		Query(Fields field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return field.toString() + " " + operation + " ?";
		}
	}
	private List<Query> queries;
	private List<String> values;

	public Where(Fields field) {
		queries = new ArrayList<>();
		values = new ArrayList<>();
		queries.add(new Query(field));
	}

	private void setLastQueryOperation(String operation) {
		Query query = queries.get(queries.size() - 1);
		query.operation = operation;
	}

	public Where eq(String s) {
		isExpectingFieldDefinition();
		setLastQueryOperation("=");
		values.add(s);
		return this;
	}

	public Where eq(Long l) {
		isExpectingFieldDefinition();
		setLastQueryOperation("=");
		values.add(String.valueOf(l));
		return this;
	}

	public Where eq(Boolean b) {
		isExpectingFieldDefinition();
		setLastQueryOperation("=");
		values.add(String.valueOf(b ? 1 : 0));
		return this;
	}

	public Where lessThanOrEq(Long l) {
		isExpectingFieldDefinition();
		setLastQueryOperation("<=");
		values.add(String.valueOf(l));
		return this;
	}

	public Where greaterThanOrEq(Long l) {
		isExpectingFieldDefinition();
		setLastQueryOperation(">=");
		values.add(String.valueOf(l));
		return this;
	}

	public Where and(Fields field) {
		fieldsAndValueMatch();
		queries.add(new Query(field));
		return this;
	}

	public Select query() {
		fieldsAndValueMatch();
		StringBuilder query = new StringBuilder();
		for (int i = 0; i < queries.size(); i++) {
			query = query.append(queries.get(i).toString());
			if(i != queries.size() - 1) {
				query = query.append(" and ");
			}
		}
		return new Select(query.toString(), values.toArray(new String[]{}));
	}

	private void fieldsAndValueMatch() {
		if(queries.size() != values.size())
			throw new UnsupportedOperationException("The value for the last field was not setted");
	}

	private void isExpectingFieldDefinition() {
		if(queries.size() - 1 != values.size())
			throw new UnsupportedOperationException("More fields than values added");
	}
}