package br.com.jonathanzanella.myexpenses.log;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jzanella on 7/11/16.
 */
public class LogsView extends BaseView {
	@Bind(R.id.view_logs_list)
	RecyclerView logs;

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

		LogAdapter adapter = new LogAdapter();
		adapter.loadData();

		logs.setAdapter(adapter);
		logs.setLayoutManager(new LinearLayoutManager(getContext()));
	}
}