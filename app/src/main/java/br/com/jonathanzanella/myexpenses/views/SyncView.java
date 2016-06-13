package br.com.jonathanzanella.myexpenses.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.adapters.UnsyncModelAdapter;
import br.com.jonathanzanella.myexpenses.models.Source;
import br.com.jonathanzanella.myexpenses.server.SourceApi;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * Created by jzanella on 6/5/16.
 */
public class SyncView extends BaseView {
    @Bind(R.id.view_unsync_models)
    RecyclerView list;
    UnsyncModelAdapter adapter;
    SourceApi sourceApi;

    public SyncView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        inflate(getContext(), R.layout.view_sync, this);
        ButterKnife.bind(this);

        adapter = new UnsyncModelAdapter();
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter.addData(Source.unsync());

        sourceApi = new SourceApi();

        sourceApi.index(new Subscriber<List<Source>>() {
                @Override
                public void onCompleted() {
                    adapter.notifyDataSetChanged();

                    Log.i("UnsyncModelAdapter", "index finished");
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onNext(List<Source> sources) {
                    adapter.addData(sources);
                }
            });
    }
}
