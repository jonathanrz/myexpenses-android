package br.com.jonathanzanella.myexpenses.card;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Setter;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
	private final CardRepository cardRepository;
	protected List<Card> cards;

	@Setter
	CardAdapterCallback callback;
	private ExpenseRepository expenseRepository;

	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_card_name)
		TextView name;
		@Bind(R.id.row_card_account)
		TextView account;
		@Bind(R.id.row_card_type)
		TextView type;

		public ViewHolder(View itemView) {
			super(itemView);

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
			Card card = getCreditCard(getAdapterPosition());
			if(card != null) {
				if(callback != null) {
					callback.onCard(card);
				} else {
					Intent i = new Intent(itemView.getContext(), ShowCardActivity.class);
					i.putExtra(ShowCardActivity.KEY_CREDIT_CARD_UUID, card.getUuid());
					itemView.getContext().startActivity(i);
				}
			}
		}
	}

	public CardAdapter() {
		cardRepository = new CardRepository(new RepositoryImpl<Card>(MyApplication.getContext()), getExpenseRepository());
	}

	private ExpenseRepository getExpenseRepository() {
		if(expenseRepository == null)
			expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(MyApplication.getContext()));
		return expenseRepository;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(cards.get(position));
	}

	@Override
	public int getItemCount() {
		return cards != null ? cards.size() : 0;
	}

	@WorkerThread
	public void loadData() {
		cards = cardRepository.userCards();
	}

	@Nullable
	private Card getCreditCard(int position) {
		return cards != null ? cards.get(position) : null;
	}
}
