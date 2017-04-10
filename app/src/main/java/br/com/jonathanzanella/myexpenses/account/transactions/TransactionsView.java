package br.com.jonathanzanella.myexpenses.account.transactions;

import android.content.Context;
import android.util.AttributeSet;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionsView extends BaseView {
	@BindView(R.id.view_account_transactions_this_month)
	MonthTransactionsView thisMonth;
	@BindView(R.id.view_account_transactions_next_month)
	MonthTransactionsView nextMonth;

	public TransactionsView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TransactionsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	protected void init() {
		inflate(getContext(), R.layout.view_account_transactions, this);
		ButterKnife.bind(this);
	}

	public void showTransactions(final Account account, final DateTime monthToShow) {
		thisMonth.setLoadTransactionsCallback(new LoadTransactionsCallback() {
			@Override
			public void onTransactionsLoaded(int balance) {
				nextMonth.showBalance(account, monthToShow.plusMonths(1), balance);
			}
		});

		thisMonth.showBalance(account, monthToShow, account.getBalance());
	}
}
