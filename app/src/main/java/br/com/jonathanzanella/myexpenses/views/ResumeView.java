package br.com.jonathanzanella.myexpenses.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.adapter.AccountAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jzanella on 2/2/16.
 */
public class ResumeView extends BaseView {
	@Bind(R.id.view_resume_accounts)
	RecyclerView accounts;

	private AccountAdapter accountAdapter;

	public ResumeView(Context context) {
		super(context);
	}

	public ResumeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ResumeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_resume, this);
		ButterKnife.bind(this);

		accountAdapter = new AccountAdapter();
		accountAdapter.setSimplified(true);
		accountAdapter.loadData();

		accounts.setAdapter(accountAdapter);
		accounts.setLayoutManager(new GridLayoutManager(getContext(), 3));
	}

	@Override
	public void refreshData() {
		super.refreshData();
		accountAdapter.loadData();
		accountAdapter.notifyDataSetChanged();
	}
}
