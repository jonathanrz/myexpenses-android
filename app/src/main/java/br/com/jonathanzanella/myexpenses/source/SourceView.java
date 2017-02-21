package br.com.jonathanzanella.myexpenses.source;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SourceView extends BaseView {
    private static final int REQUEST_ADD_SOURCE = 1004;
    private SourceAdapter adapter;

    @Bind(R.id.view_sources_list)
    RecyclerView sources;

    public SourceView(Context context) {
        super(context);
    }

    public SourceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SourceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        inflate(getContext(), R.layout.view_sources, this);
        ButterKnife.bind(this);

        adapter = new SourceAdapter();

        sources.setAdapter(adapter);
        sources.setLayoutManager(new GridLayoutManager(getContext(), 1));
        sources.setItemAnimator(new DefaultItemAnimator());
    }

    @OnClick(R.id.view_sources_fab)
    void onFab() {
        Context ctx = getContext();
        Intent i = new Intent(getContext(), EditSourceActivity.class);
        if(ctx instanceof Activity) {
            ((Activity) ctx).startActivityForResult(i, REQUEST_ADD_SOURCE);
        } else {
            ctx.startActivity(i);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ADD_SOURCE:
                if(resultCode == Activity.RESULT_OK)
                    adapter.refreshData();
                break;
        }
    }

    @Override
    public void refreshData() {
        super.refreshData();

        adapter.refreshData();
        adapter.notifyDataSetChanged();
    }
}
