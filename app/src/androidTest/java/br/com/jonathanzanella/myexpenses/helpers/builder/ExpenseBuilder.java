package br.com.jonathanzanella.myexpenses.helpers.builder;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.expense.Expense;

public class ExpenseBuilder {
	private String name = "a";
	private Chargeable chargeable = new AccountBuilder().build();
	private DateTime date = DateTime.now();
	private Bill bill = null;
	private boolean sync = false;
	private int value = 100;

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

	public ExpenseBuilder bill(Bill bill) {
		this.bill = bill;
		return this;
	}

	public ExpenseBuilder sync(boolean sync) {
		this.sync = sync;
		return this;
	}

	public ExpenseBuilder value(int value) {
		this.value = value;
		return this;
	}

	public Expense build() {
		Expense expense = new Expense();
		expense.setName(name);
		expense.setChargeable(chargeable);
		expense.setDate(date);
		expense.setBill(bill);
		expense.setSync(sync);
		expense.setValue(value);
		return expense;
	}
}