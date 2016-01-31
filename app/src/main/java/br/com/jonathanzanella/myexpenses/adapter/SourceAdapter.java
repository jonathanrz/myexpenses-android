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
import br.com.jonathanzanella.myexpenses.activities.ShowAccountActivity;
import br.com.jonathanzanella.myexpenses.model.Source;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.ViewHolder> {
	protected List<Source> sources;

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_source_name)
		TextView name;

		WeakReference<SourceAdapter> adapterWeakReference;

		public ViewHolder(View itemView, SourceAdapter adapter) {
			super(itemView);
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);

			itemView.setOnClickListener(this);
		}

		public void setData(Source source) {
			name.setText(source.getName());
		}

		@Override
		public void onClick(View v) {
			Source source = adapterWeakReference.get().getSource(getAdapterPosition());
			if(source != null) {
                Intent i = new Intent(itemView.getContext(), ShowAccountActivity.class);
                i.putExtra(ShowAccountActivity.KEY_ACCOUNT_ID, source.getId());
                itemView.getContext().startActivity(i);
			}
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_source, parent, false);
		return new ViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(sources.get(position));
	}

	@Override
	public int getItemCount() {
		return sources != null ? sources.size() : 0;
	}

	public void loadData() {
		sources = Source.all();
	}

	public void addSource(@NonNull Source source) {
		sources.add(source);
		notifyItemInserted(sources.size() - 1);
	}

	public @Nullable Source getSource(int position) {
		return sources != null ? sources.get(position) : null;
	}
}
