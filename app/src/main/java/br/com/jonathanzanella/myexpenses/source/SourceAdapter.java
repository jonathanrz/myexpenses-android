package br.com.jonathanzanella.myexpenses.source;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.ViewHolder> {
	SourceAdapterCallback callback;

	private final SourceAdapterPresenter presenter;

	private List<Source> sources;

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_source_name)
		TextView name;

		@BindColor(R.color.color_list_odd)
		int oddColor;
		@BindColor(R.color.color_list_even)
		int evenColor;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);

			itemView.setOnClickListener(this);
		}

		private int getColor(int position) {
			position++;//Just to simplify mathematics
			if(position % 4 == 0)
				return oddColor;
			else if(position % 2 == 0)
				return evenColor;
			else if((position + 1) % 4 == 0)
				return evenColor;
			else
				return oddColor;
		}

		public void setData(Source source) {
			itemView.setBackgroundColor(getColor(getAdapterPosition()));
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
					i.putExtra(ShowSourceActivity.KEY_SOURCE_UUID, source.getUuid());
					itemView.getContext().startActivity(i);
				}
			}
		}
	}

	SourceAdapter() {
		presenter = new SourceAdapterPresenter(new SourceRepository(new RepositoryImpl<Source>(MyApplication.getContext())));
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
