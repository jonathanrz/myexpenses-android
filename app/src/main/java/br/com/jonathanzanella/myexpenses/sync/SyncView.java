package br.com.jonathanzanella.myexpenses.sync;

import android.content.Context;
import android.content.Intent;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jzanella on 6/5/16.
 */
public class SyncView extends BaseView {
	public SyncView(Context context) {
		super(context);
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_sync, this);
		ButterKnife.bind(this);
	}

	@OnClick(R.id.view_sync_sync_btn)
	void Sync() {
		Intent i = new Intent(getContext(), SyncService.class);
		i.putExtra(SyncService.KEY_EXECUTE_SYNC, true);
		getContext().startService(i);
	}
}
