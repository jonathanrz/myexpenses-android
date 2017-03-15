package br.com.jonathanzanella.myexpenses.account.transactions;

import android.content.Context;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.bill.BillRepository;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;
import br.com.jonathanzanella.myexpenses.transaction.TransactionAdapter;
import lombok.Getter;

class MonthTransactionsPresenter implements LoadTransactionsCallback {
	@Getter
	private final TransactionAdapter adapter;
	private final MonthTransactionsContractView view;
	private final ReceiptRepository receiptRepository;
	private final ExpenseRepository expenseRepository;
	private final BillRepository billRepository;
	private int currentBalance;

	MonthTransactionsPresenter(Context ctx, MonthTransactionsContractView view) {
		this.view = view;
		adapter = new TransactionAdapter();
		receiptRepository = new ReceiptRepository(new RepositoryImpl<Receipt>(ctx));
		expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(ctx));
		billRepository = new BillRepository(new RepositoryImpl<Bill>(ctx), expenseRepository);
	}

	void showBalance(final Account account, final DateTime month, int balance) {
		currentBalance = balance;
		new LoadData(this).load(account, month);
	}

	@Override
	public void onTransactionsLoaded(int balance) {
		for (Transaction transaction : adapter.getTransactions()) {
			if(!transaction.credited()) {
				currentBalance += transaction.getAmount();
			} else if(!transaction.debited()) {
				currentBalance -= transaction.getAmount();
			}
		}

		view.onBalanceUpdated(currentBalance);
	}

	private class LoadData {
		private LoadTransactionsCallback callback;
		private boolean loadedBills = false;
		private boolean loadedExpenses = false;
		private boolean loadedReceipts = false;

		LoadData(LoadTransactionsCallback callback) {
			this.callback = callback;
		}

		void load(Account account, final DateTime month) {
			loadBills(account, month);
			loadExpenses(account, month);
			loadReceipts(account, month);
		}

		private void loadBills(Account account, final DateTime month) {
			loadedBills = false;

			if(account.isAccountToPayBills()) {
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... voids) {
						List<Bill> bills = billRepository.monthly(month);
						adapter.addTransactions(bills);
						return null;
					}

					@Override
					protected void onPostExecute(Void v) {
						super.onPostExecute(v);
						adapter.notifyDataSetChanged();
						loadedBills = true;
						onDataLoaded();
					}
				}.execute();
			} else {
				loadedBills = true;
			}
		}

		private void loadExpenses(final Account account, final DateTime month) {
			loadedExpenses = false;

			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... voids) {
					List<Expense> expenses = expenseRepository.accountExpenses(account, month);
					adapter.addTransactions(expenses);
					return null;
				}

				@Override
				protected void onPostExecute(Void v) {
					super.onPostExecute(v);
					adapter.notifyDataSetChanged();
					loadedExpenses = true;
					onDataLoaded();
				}
			}.execute();
		}

		private void loadReceipts(final Account account, final DateTime month) {
			loadedReceipts = false;

			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... voids) {
					adapter.addTransactions(receiptRepository.monthly(month, account));
					return null;
				}

				@Override
				protected void onPostExecute(Void aVoid) {
					super.onPostExecute(aVoid);
					adapter.notifyDataSetChanged();
					loadedReceipts = true;
					onDataLoaded();
				}
			}.execute();
		}

		private void onDataLoaded() {
			if(loadedBills && loadedExpenses && loadedReceipts)
				callback.onTransactionsLoaded(currentBalance);
		}
	}
}
