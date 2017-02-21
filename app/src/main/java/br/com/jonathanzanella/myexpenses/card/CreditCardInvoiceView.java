package br.com.jonathanzanella.myexpenses.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.expense.CreditCardMonthlyAdapter;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.Bind;
import butterknife.ButterKnife;

@SuppressLint("ViewConstructor")
public class CreditCardInvoiceView extends BaseView {
	@Bind(R.id.view_credit_card_invoice_list)
	RecyclerView list;
	private CreditCardMonthlyAdapter adapter;

	public CreditCardInvoiceView(Context context, final Card creditCard, final DateTime month) {
		super(context);

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... voids) {
				adapter.loadData(creditCard, month);
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				adapter.notifyDataSetChanged();
			}
		}.execute();
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_credit_card_invoice, this);
		ButterKnife.bind(this);

		adapter = new CreditCardMonthlyAdapter(getContext());

		list.setAdapter(adapter);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
	}
}
