package br.com.jonathanzanella.myexpenses.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.activities.ShowCreditCardActivity;
import br.com.jonathanzanella.myexpenses.model.CreditCard;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class CreditCardAdapter extends RecyclerView.Adapter<CreditCardAdapter.ViewHolder> {
	protected List<CreditCard> cards;

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_credit_card_name)
		TextView name;

		WeakReference<CreditCardAdapter> adapterWeakReference;

		public ViewHolder(View itemView, CreditCardAdapter adapter) {
			super(itemView);
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);

			itemView.setOnClickListener(this);
		}

		public void setData(CreditCard creditCard) {
			name.setText(creditCard.getName());
		}

		@Override
		public void onClick(View v) {
			CreditCard creditCard = adapterWeakReference.get().getCreditCard(getAdapterPosition());
			if(creditCard != null) {
                Intent i = new Intent(itemView.getContext(), ShowCreditCardActivity.class);
                i.putExtra(ShowCreditCardActivity.KEY_CREDIT_CARD_ID, creditCard.getId());
                itemView.getContext().startActivity(i);
			}
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_credit_card, parent, false);
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
		cards = CreditCard.all();
	}

	public void addCreditCard(@NonNull CreditCard creditCard) {
		cards.add(creditCard);
		notifyItemInserted(cards.size() - 1);
	}

	public @Nullable CreditCard getCreditCard(int position) {
		return cards != null ? cards.get(position) : null;
	}
}
