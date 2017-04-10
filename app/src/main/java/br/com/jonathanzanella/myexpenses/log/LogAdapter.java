package br.com.jonathanzanella.myexpenses.log;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import butterknife.BindView;
import butterknife.ButterKnife;

class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {
	private final LogRepository logRepository;
	private List<Log> filteredLogs;

	public static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.row_log_indicator)
		View indicator;
		@BindView(R.id.row_log_title)
		TextView title;
		@BindView(R.id.row_log_date)
		TextView date;
		@BindView(R.id.row_log_description)
		TextView description;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);
		}

		public void setData(Log log) {
			//noinspection deprecation
			indicator.setBackgroundColor(indicator.getContext().getResources().getColor(log.getLogLevel().getColor()));
			title.setText(log.getTitle());
			date.setText(log.simpleDateFormat.format(log.getDate().toDate()));
			description.setText(log.getDescription());
		}
	}

	LogAdapter(LogRepository logRepository) {
		this.logRepository = logRepository;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_log, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(filteredLogs.get(position));
	}

	@Override
	public int getItemCount() {
		return filteredLogs != null ? filteredLogs.size() : 0;
	}

	public void loadData(DateTime initDate, DateTime endDate, Log.LogLevel logLevel, String filter) {
		List<Log> logs = logRepository.filter(initDate, endDate, logLevel);

		filteredLogs = new ArrayList<>();
		if(StringUtils.isNotEmpty(filter)) {
			for (Log log : logs) {
				if(StringUtils.containsIgnoreCase(log.getTitle(), filter) ||
						StringUtils.containsIgnoreCase(log.getDescription(), filter)) {
					filteredLogs.add(log);
				}
			}
		} else {
			filteredLogs = logs;
		}
	}
}
