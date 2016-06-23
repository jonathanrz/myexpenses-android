package br.com.jonathanzanella.myexpenses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.models.Source;
import butterknife.Bind;

/**
 * Created by jzanella on 1/31/16.
 */
public class ShowSourceActivity extends BaseActivity {
	public static final String KEY_SOURCE_UUID = "KeySourceUuid";

	@Bind(R.id.act_show_source_name)
	TextView sourceName;

	private Source source;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_source);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setData();
	}

	private void setData() {
		sourceName.setText(source.getName());
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);
		if(extras == null)
			return;
		if(extras.containsKey(KEY_SOURCE_UUID))
			source = Source.find(extras.getString(KEY_SOURCE_UUID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_SOURCE_UUID, source.getUuid());
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(source != null) {
			source = Source.find(source.getUuid());
			setData();
		}
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
				i.putExtra(EditSourceActivity.KEY_SOURCE_UUID, source.getUuid());
				startActivity(i);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
