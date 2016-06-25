package br.com.jonathanzanella.myexpenses.card;

import android.util.Log;

import com.raizlabs.android.dbflow.StringUtils;

import java.util.List;

import br.com.jonathanzanella.myexpenses.server.Server;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jzanella on 6/12/16.
 */
public class CardApi implements UnsyncModelApi<Card> {
    CardInterface cardInterface;

    private CardInterface getInterface() {
        if(cardInterface == null)
            cardInterface = new Server().cardInterface();
        return cardInterface;
    }

    @Override
    public void index(Subscriber<List<Card>> subscriber) {
        Observable<List<Card>> observable = getInterface().index(Card.greaterUpdatedAt());
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(subscriber);
    }

    @Override
    public void save(UnsyncModel model, Subscriber<Card> subscriber) {
        Card card = (Card) model;
        Observable<Card> observable;
        if(StringUtils.isNotNullOrEmpty(card.getServerId()))
            observable = getInterface().update(card.getServerId(), card);
        else
            observable = getInterface().create(card);

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(subscriber);
    }
}