package br.com.jonathanzanella.myexpenses.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.adapter.CreditCardMonthlyAdapter;
import br.com.jonathanzanella.myexpenses.model.Card;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan Zanella on 08/02/16.
 */
public class CreditCardInvoiceView extends BaseView {
	@Bind(R.id.view_credit_card_invoice_list)
	RecyclerView list;
	private CreditCardMonthlyAdapter adapter;

	public CreditCardInvoiceView(Context context, Card creditCard, DateTime month) {
		super(context);

		adapter.loadData(creditCard, month);
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_credit_card_invoice, this);
		ButterKnife.bind(this);

		adapter = new CreditCardMonthlyAdapter();

		list.setAdapter(adapter);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
	}
}
