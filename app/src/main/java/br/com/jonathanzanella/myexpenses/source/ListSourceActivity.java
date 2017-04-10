package br.com.jonathanzanella.myexpenses.source;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.BindView;

public class ListSourceActivity extends BaseActivity implements SourceAdapterCallback {
	public static final String KEY_SOURCE_SELECTED_UUID = "KeySourceSelectUuid";

	@BindView(R.id.act_sources_list)
	RecyclerView sources;
	@BindView(R.id.act_sources_list_empty)
	TextView emptyListView;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_source);
		setTitle(R.string.select_source_title);
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

		emptyListView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onSourceSelected(Source source) {
		Intent i = new Intent();
		i.putExtra(KEY_SOURCE_SELECTED_UUID, source.getUuid());
		setResult(RESULT_OK, i);
		finish();
	}
}
