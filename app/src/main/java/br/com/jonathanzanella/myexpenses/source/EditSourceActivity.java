package br.com.jonathanzanella.myexpenses.source;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.user.SelectUserView;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditSourceActivity extends BaseActivity implements SourceContract.View {
	public static final String KEY_SOURCE_UUID = "KeySourceUuid";

	@Bind(R.id.act_edit_source_name)
	EditText editName;
	@Bind(R.id.act_edit_source_user)
	SelectUserView selectUserView;

	private SourcePresenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_source);
		presenter = new SourcePresenter(this);
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);

		if(extras != null && extras.containsKey(KEY_SOURCE_UUID))
			presenter.loadSource(extras.getString(KEY_SOURCE_UUID));
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		presenter.viewCreated();
	}

	@Override
	public void showSource(Source source) {
		editName.setText(source.getName());
		selectUserView.setSelectedUser(source.getUserUuid());
	}

	@Override
	public void fillSource(Source source) {
		source.setName(editName.getText().toString());
		source.setUserUuid(selectUserView.getSelectedUser());
	}

	@Override
	public void finishView() {
		Intent i = new Intent();
		i.putExtra(KEY_SOURCE_UUID, presenter.getUuid());
		setResult(RESULT_OK, i);
		finish();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		String uuid = presenter.getUuid();
		if(uuid != null)
			outState.putString(KEY_SOURCE_UUID, uuid);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.save, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_save:
				presenter.save();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
