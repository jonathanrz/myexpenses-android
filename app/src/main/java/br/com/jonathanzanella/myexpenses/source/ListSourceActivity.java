package br.com.jonathanzanella.myexpenses.source;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;

/**
 * Created by jzanella on 2/1/16.
 */
public class ListSourceActivity extends BaseActivity implements SourceAdapterCallback {
	public static final String KEY_SOURCE_SELECTED_UUID = "KeySourceSelectUuid";

	@Bind(R.id.act_sources_list)
	RecyclerView sources;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_source);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		SourceAdapter adapter = new SourceAdapter();
		adapter.setCallback(this);

		sources.setAdapter(adapter);
		sources.setHasFixedSize(true);
		sources.setLayoutManager(new GridLayoutManager(this, 2));
		sources.setItemAnimator(new DefaultItemAnimator());
	}

	@Override
	public void onSourceSelected(Source source) {
		Intent i = new Intent();
		i.putExtra(KEY_SOURCE_SELECTED_UUID, source.getUuid());
		setResult(RESULT_OK, i);
		finish();
	}
}
