package br.com.jonathanzanella.myexpenses.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.adapters.UnsyncModelAdapter;
import br.com.jonathanzanella.myexpenses.models.Source;
import br.com.jonathanzanella.myexpenses.server.Server;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jzanella on 6/5/16.
 */
public class SyncView extends BaseView {
    @Bind(R.id.view_unsync_models)
    RecyclerView list;
    UnsyncModelAdapter adapter;

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

        Observable<List<Source>> sources = new Server().sourceInterface().index(Source.greaterUpdatedAt());
        sources.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<List<Source>>() {
                    @Override
                    public void onCompleted() {
                        adapter.notifyDataSetChanged();
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
