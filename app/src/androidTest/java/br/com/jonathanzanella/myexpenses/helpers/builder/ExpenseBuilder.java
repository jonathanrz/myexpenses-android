package br.com.jonathanzanella.myexpenses.helpers.builder;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.expense.Expense;

/**
 * Created by jzanella on 8/28/16.
 */

public class ExpenseBuilder {
	private String name = "expenseTest";
	private Chargeable chargeable = new AccountBuilder().build();
	private DateTime date = DateTime.now();

	public ExpenseBuilder name(String name) {
		this.name = name;
		return this;
	}

	public ExpenseBuilder chargeable(Chargeable chargeable) {
		this.chargeable = chargeable;
		return this;
	}

	public ExpenseBuilder date(DateTime date) {
		this.date = date;
		return this;
	}

	public Expense build() {
		Expense expense = new Expense();
		expense.setName(name);
		expense.setValue(1);
		expense.setChargeable(chargeable);
		expense.setDate(date);
		return expense;
	}
}