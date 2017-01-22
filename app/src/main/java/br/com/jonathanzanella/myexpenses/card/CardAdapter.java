package br.com.jonathanzanella.myexpenses.card;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Setter;

/**
 * Created by Jonathan Zanella onCard 26/01/16.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
	protected List<Card> cards;
	private CardAdapterPresenter presenter;

	@Setter
	CardAdapterCallback callback;

	static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_card_name)
		TextView name;
		@Bind(R.id.row_card_account)
		TextView account;
		@Bind(R.id.row_card_type)
		TextView type;

		WeakReference<CardAdapter> adapterWeakReference;

		public ViewHolder(View itemView, CardAdapter adapter) {
			super(itemView);
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);

			itemView.setOnClickListener(this);
		}

		@UiThread
		public void setData(final Card card) {
			new AsyncTask<Void, Void, Account>() {

				@Override
				protected Account doInBackground(Void... voids) {
					return card.getAccount();
				}

				@Override
				protected void onPostExecute(Account a) {
					super.onPostExecute(a);
					account.setText(a.getName());
				}
			}.execute();

			name.setText(card.getName());

			switch (card.getType()) {
				case CREDIT:
					type.setText(R.string.credit);
					break;
				case DEBIT:
					type.setText(R.string.debit);
					break;
			}
		}

		@Override
		public void onClick(View v) {
			CardAdapter adapter = adapterWeakReference.get();
			Card card = adapter.getCreditCard(getAdapterPosition());
			if(card != null) {
				if(adapter.callback != null) {
					adapter.callback.onCard(card);
				} else {
					Intent i = new Intent(itemView.getContext(), ShowCardActivity.class);
					i.putExtra(ShowCardActivity.KEY_CREDIT_CARD_UUID, card.getUuid());
					itemView.getContext().startActivity(i);
				}
			}
		}
	}

	public CardAdapter() {
		presenter = new CardAdapterPresenter(this, new CardRepository());
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card, parent, false);
		return new ViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(cards.get(position));
	}

	@Override
	public int getItemCount() {
		return cards != null ? cards.size() : 0;
	}

	public void loadData() {
		cards = Card.user();
	}

	void addCreditCard(@NonNull Card card) {
		presenter.addCard(card);
		cards = presenter.getCards(false);
	}

	@Nullable
	private Card getCreditCard(int position) {
		return cards != null ? cards.get(position) : null;
	}
}
