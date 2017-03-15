package br.com.jonathanzanella.myexpenses.source;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.user.SelectUserView;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;

public class EditSourceActivity extends BaseActivity implements SourceContract.EditView {
	public static final String KEY_SOURCE_UUID = "KeySourceUuid";

	@Bind(R.id.act_edit_source_name)
	EditText editName;
	@Bind(R.id.act_edit_source_user)
	SelectUserView selectUserView;

	private SourcePresenter presenter = new SourcePresenter(new SourceRepository(new RepositoryImpl<Source>(this)));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_source);
	}

	@Override
	protected void storeBundle(final Bundle extras) {
		super.storeBundle(extras);

		if(extras != null && extras.containsKey(KEY_SOURCE_UUID))
			presenter.loadSource(extras.getString(KEY_SOURCE_UUID));
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		presenter.viewUpdated();
	}

	@Override
	protected void onStart() {
		super.onStart();
		presenter.attachView(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		presenter.detachView();
	}

	@Override
	public void showSource(Source source) {
		editName.setText(source.getName());
		selectUserView.setSelectedUser(source.getUserUuid());
	}

	@Override
	public Source fillSource(Source source) {
		source.setName(editName.getText().toString());
		source.setUserUuid(selectUserView.getSelectedUser());
		return source;
	}

	@Override
	public void finishView() {
		Intent i = new Intent();
		i.putExtra(KEY_SOURCE_UUID, presenter.getUuid());
		setResult(RESULT_OK, i);
		finish();
	}

	@Override
	public void showError(ValidationError error) {
		switch (error) {
			case NAME:
				editName.setError(getString(error.getMessage()));
				break;
			default:
				Log.error(this.getClass().getName(), "Validation unrecognized, field:" + error);
		}
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
