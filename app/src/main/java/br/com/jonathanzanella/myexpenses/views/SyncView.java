package br.com.jonathanzanella.myexpenses.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.adapters.UnsyncModelAdapter;
import br.com.jonathanzanella.myexpenses.models.Source;
import br.com.jonathanzanella.myexpenses.models.UnsyncModel;
import br.com.jonathanzanella.myexpenses.server.SourceApi;
import br.com.jonathanzanella.myexpenses.server.UnsyncModelApi;
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
    List<UnsyncModelApi> apis;

    public SyncView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        apis = new ArrayList<>();
        apis.add(new SourceApi());

        inflate(getContext(), R.layout.view_sync, this);
        ButterKnife.bind(this);

        adapter = new UnsyncModelAdapter();
        final StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(adapter);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.addItemDecoration(headersDecoration);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecoration.invalidateHeaders();
            }
        });

        adapter.addData(Source.unsync());

        Subscriber<List<? extends UnsyncModel>> subscriber = new Subscriber<List<? extends UnsyncModel>>() {

            @Override
            public void onCompleted() {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onNext(List<? extends UnsyncModel> unsyncModels) {
                adapter.addData(unsyncModels);
            }
        };

        for (UnsyncModelApi api : apis)
            api.index(subscriber);
    }
}
