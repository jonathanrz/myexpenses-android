package br.com.jonathanzanella.myexpenses.views;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
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
    @Bind(R.id.view_sync_text)
    TextView text;

    public SyncView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        inflate(getContext(), R.layout.view_sync, this);
        ButterKnife.bind(this);

        Observable<List<Source>> sources = new Server().sourceInterface().index();
        sources.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<List<Source>>() {
                    @Override
                    public void onCompleted() {
                        Log.d("SyncView", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        text.setText("onError=" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<Source> sources) {
                        StringBuilder sourcesText = new StringBuilder();
                        for (Source source : sources) {
                            sourcesText.append("name=").append(source.getName()).append("\n");
                            sourcesText.append("id=").append(source.getServerId()).append("\n");
                            sourcesText.append("createdAt=").append(source.getCreatedAt()).append("\n");
                            sourcesText.append("updatedAt=").append(source.getUpdatedAt()).append("\n");
                            sourcesText.append("\n");
                        }
                        text.setText(sourcesText.toString());
                    }
                });
    }
}
