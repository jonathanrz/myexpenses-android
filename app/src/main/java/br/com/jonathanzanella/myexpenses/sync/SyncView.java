package br.com.jonathanzanella.myexpenses.sync;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountApi;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.bill.BillApi;
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.card.CardApi;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseApi;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.receipt.ReceiptApi;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceApi;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * Created by jzanella on 6/5/16.
 */
public class SyncView extends BaseView {
	@Bind(R.id.view_unsync_models)
	RecyclerView list;
	UnsyncModelAdapter adapter;
	List<UnsyncModelApi<? extends UnsyncModel>> apis;

	public SyncView(Context context) {
		super(context);
	}

	@Override
	protected void init() {
		apis = new ArrayList<>();
		apis.add(new SourceApi());
		apis.add(new AccountApi());
		apis.add(new BillApi());
		apis.add(new CardApi());
		apis.add(new ExpenseApi());
		apis.add(new ReceiptApi());

		inflate(getContext(), R.layout.view_sync, this);
		ButterKnife.bind(this);

		adapter = new UnsyncModelAdapter();
		final StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(adapter);
		list.setAdapter(adapter);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.addItemDecoration(headersDecoration);
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onChanged() {
				headersDecoration.invalidateHeaders();
			}
		});

		adapter.addData(Source.unsync());
		adapter.addData(Account.unsync());
		adapter.addData(Bill.unsync());
		adapter.addData(Card.unsync());
		adapter.addData(Expense.unsync());
		adapter.addData(Receipt.unsync());
		adapter.notifyDataSetChanged();

		for (final UnsyncModelApi api : apis)
			//noinspection unchecked
			api.index(new Subscriber<List<? extends UnsyncModel>>() {

				@Override
				public void onCompleted() {
					adapter.notifyDataSetChanged();
				}

				@Override
				public void onError(Throwable e) {
					e.printStackTrace();
				}

				@Override
				public void onNext(List<? extends UnsyncModel> unsyncModels) {
					adapter.addData(unsyncModels);
				}
			});
	}
}
