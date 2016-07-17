package br.com.jonathanzanella.myexpenses.log;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {
	protected List<Log> logs;

	public static class ViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.row_log_indicator)
		View indicator;
		@Bind(R.id.row_log_title)
		TextView title;
		@Bind(R.id.row_log_date)
		TextView date;
		@Bind(R.id.row_log_description)
		TextView description;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);
		}

		public void setData(Log log) {
			indicator.setBackgroundColor(indicator.getContext().getResources().getColor(log.getType().getColor()));
			title.setText(log.getTitle());
			date.setText(Log.sdf.format(log.getDate().toDate()));
			description.setText(log.getDescription());
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_log, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(logs.get(position));
	}

	@Override
	public int getItemCount() {
		return logs != null ? logs.size() : 0;
	}

	public void loadData(DateTime initDate, DateTime endDate) {
		logs = Log.filter(initDate, endDate);
	}
}
