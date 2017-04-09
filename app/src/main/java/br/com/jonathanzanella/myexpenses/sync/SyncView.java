package br.com.jonathanzanella.myexpenses.sync;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SyncView extends BaseView {
	@Bind(R.id.view_sync_server_url)
	EditText serverUrlView;
	@Bind(R.id.view_sync_server_token)
	EditText serverTokenView;

	public SyncView(Context context) {
		super(context);
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_sync, this);
		ButterKnife.bind(this);
	}

	@OnClick(R.id.view_sync_sync_btn)
	void sync() {
		String serverUrl = serverUrlView.getText().toString();
		String serverToken = serverTokenView.getText().toString();

		if(StringUtils.isEmpty(serverUrl)) {
			serverTokenView.setError(getContext().getString(R.string.error_message_server_url_not_informed));
			return;
		}

		if(StringUtils.isEmpty(serverToken)) {
			serverTokenView.setError(getContext().getString(R.string.error_message_server_token_not_informed));
			return;
		}

		ServerData serverData = new ServerData(getContext());
		serverData.updateInfo(serverUrl, serverToken);
		Intent i = new Intent(getContext(), SyncService.class);
		i.putExtra(SyncService.KEY_EXECUTE_SYNC, true);
		getContext().startService(i);
	}
}
