package br.com.jonathanzanella.myexpenses.account;

import android.content.Context;
import android.util.AttributeSet;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class TransactionsView extends BaseView {
	@Bind(R.id.view_account_transactions_this_month)
	MonthTransactionsView thisMonth;
	@Bind(R.id.view_account_transactions_next_month)
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

	public void showTransactions(Account account, DateTime monthToShow) {
		int nextMonthBalance = thisMonth.showBalance(account, monthToShow, account.getBalance());
		nextMonth.showBalance(account, monthToShow.plusMonths(1), nextMonthBalance);
	}
}
