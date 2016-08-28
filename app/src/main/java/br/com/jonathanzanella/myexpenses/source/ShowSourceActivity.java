package br.com.jonathanzanella.myexpenses.source;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;

/**
 * Created by jzanella on 1/31/16.
 */
public class ShowSourceActivity extends BaseActivity implements SourceContract.View {
	public static final String KEY_SOURCE_UUID = "KeySourceUuid";

	@Bind(R.id.act_show_source_name)
	TextView sourceName;

	private SourcePresenter presenter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_source);

		presenter = new SourcePresenter(this, new SourceRepository());
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		presenter.viewUpdated(false);
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);

		if(extras != null && extras.containsKey(KEY_SOURCE_UUID))
			presenter.loadSource(extras.getString(KEY_SOURCE_UUID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(KEY_SOURCE_UUID, presenter.getUuid());
	}

	@Override
	protected void onResume() {
		super.onResume();

		presenter.viewUpdated(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit:
				Intent i = new Intent(this, EditSourceActivity.class);
				i.putExtra(EditSourceActivity.KEY_SOURCE_UUID, presenter.getUuid());
				startActivity(i);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void showSource(Source source) {
		sourceName.setText(source.getName());
	}

	@Override
	public Source fillSource(Source source) {
		throw new InvalidMethodCallException("fillSource", "ShowSourceActivity");
	}

	@Override
	public void finishView() {
		throw new InvalidMethodCallException("finishView", "ShowSourceActivity");
	}

	@Override
	public void showError(ValidationError error) {
		throw new InvalidMethodCallException("showError", "ShowSourceActivity");
	}
}
