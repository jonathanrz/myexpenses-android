package br.com.jonathanzanella.myexpenses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.models.Source;
import butterknife.Bind;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditSourceActivity extends BaseActivity {
	public static final String KEY_SOURCE_UUID = "KeySourceUuid";

	@Bind(R.id.act_edit_source_name)
	EditText editName;

	private Source source;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_source);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if(source != null)
			editName.setText(source.getName());
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
		if(source != null)
			outState.putString(KEY_SOURCE_UUID, source.getUuid());
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
				save();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void save() {
		if(source == null)
			source = new Source();
		source.setName(editName.getText().toString());
		source.save();

		Intent i = new Intent();
		i.putExtra(KEY_SOURCE_UUID, source.getUuid());
		setResult(RESULT_OK, i);
		finish();
	}
}
