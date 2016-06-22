package br.com.jonathanzanella.myexpenses.server;

import com.raizlabs.android.dbflow.StringUtils;

import java.util.List;

import br.com.jonathanzanella.myexpenses.models.Source;
import br.com.jonathanzanella.myexpenses.models.UnsyncModel;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jzanella on 6/12/16.
 */
public class SourceApi implements UnsyncModelApi<Source> {
    SourceInterface sourceInterface;

    private SourceInterface getInterface() {
        if(sourceInterface == null)
            sourceInterface = new Server().sourceInterface();
        return sourceInterface;
    }

    @Override
    public void index(Subscriber<List<Source>> subscriber) {
        Observable<List<Source>> observable = getInterface().index(Source.greaterUpdatedAt());
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(subscriber);
    }

    @Override
    public void save(UnsyncModel model, Subscriber<Source> subscriber) {
        Source source = (Source) model;
        Observable<Source> observable;
        if(StringUtils.isNotNullOrEmpty(source.getServerId()))
            observable = getInterface().update(source.getServerId(), source);
        else
            observable = getInterface().create(source);

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(subscriber);
    }
}