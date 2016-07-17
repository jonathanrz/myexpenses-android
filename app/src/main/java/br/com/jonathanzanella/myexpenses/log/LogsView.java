package br.com.jonathanzanella.myexpenses.log;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import br.com.jonathanzanella.myexpenses.views.DateTimeView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jzanella on 7/11/16.
 */
public class LogsView extends BaseView implements DateTimeView.Listener {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
	@Bind(R.id.view_logs_list)
	RecyclerView logs;
	@Bind(R.id.view_log_init_time)
	DateTimeView initTime;
	@Bind(R.id.view_log_end_time)
	DateTimeView endTime;
	private LogAdapter adapter;

	public LogsView(Context context) {
		super(context);
	}

	public LogsView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LogsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_logs, this);
		ButterKnife.bind(this);

		adapter = new LogAdapter();

		logs.setAdapter(adapter);
		logs.setLayoutManager(new LinearLayoutManager(getContext()));

		initTime.setDate(DateTime.now().minusHours(1));
		initTime.setListener(this);
		endTime.setDate(DateTime.now());
		endTime.setListener(this);

		adapter.loadData(initTime.getCurrentTime(), endTime.getCurrentTime());
	}

	@Override
	public void onDateTimeChanged(DateTime currentTime) {
		adapter.loadData(initTime.getCurrentTime(), endTime.getCurrentTime());
		adapter.notifyDataSetChanged();
	}
}