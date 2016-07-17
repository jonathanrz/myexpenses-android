package br.com.jonathanzanella.myexpenses.sync;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jzanella on 6/5/16.
 */
public class SyncView extends BaseView {
	@Bind(R.id.view_unsync_models)
	RecyclerView list;

	public SyncView(Context context) {
		super(context);
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_sync, this);
		ButterKnife.bind(this);
	}
}
