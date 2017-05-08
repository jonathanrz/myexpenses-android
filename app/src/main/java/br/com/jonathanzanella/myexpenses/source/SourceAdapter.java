package br.com.jonathanzanella.myexpenses.source;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.helpers.AdapterColorHelper;
import butterknife.Bind;
import butterknife.ButterKnife;

class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.ViewHolder> {
	SourceAdapterCallback callback;

	private final SourceAdapterPresenter presenter;

	private List<Source> sources;

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_source_name)
		TextView name;

		private final AdapterColorHelper adapterColorHelper;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);

			int oddColor = ContextCompat.getColor(itemView.getContext(), R.color.color_list_odd);
			int evenColor = ContextCompat.getColor(itemView.getContext(), R.color.color_list_even);
			adapterColorHelper = new AdapterColorHelper(oddColor, evenColor);

			itemView.setOnClickListener(this);
		}

		public void setData(Source source) {
			itemView.setBackgroundColor(adapterColorHelper.getColorForGridWithTwoColumns(getAdapterPosition()));
			name.setText(source.getName());
		}

		@Override
		public void onClick(View v) {
			Source source = getSource(getAdapterPosition());
			if (source != null) {
				if(callback != null) {
					callback.onSourceSelected(source);
				} else {
					Intent i = new Intent(itemView.getContext(), ShowSourceActivity.class);
					i.putExtra(ShowSourceActivity.Companion.getKEY_SOURCE_UUID(), source.getUuid());
					itemView.getContext().startActivity(i);
				}
			}
		}
	}

	SourceAdapter(Context ctx) {
		presenter = new SourceAdapterPresenter(new SourceRepository(new RepositoryImpl<Source>(ctx)));
		sources = presenter.getSources(false);
	}

	void refreshData() {
		sources = presenter.getSources(true);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_source, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(sources.get(position));
	}

	@Override
	public int getItemCount() {
		return sources.size();
	}

	@Nullable
	private Source getSource(int position) {
		return sources != null ? sources.get(position) : null;
	}

	public void setCallback(SourceAdapterCallback callback) {
		this.callback = callback;
	}
}
