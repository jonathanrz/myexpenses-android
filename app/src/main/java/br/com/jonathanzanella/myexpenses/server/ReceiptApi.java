package br.com.jonathanzanella.myexpenses.server;

import com.raizlabs.android.dbflow.StringUtils;

import java.util.List;

import br.com.jonathanzanella.myexpenses.models.Receipt;
import br.com.jonathanzanella.myexpenses.models.UnsyncModel;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jzanella on 6/12/16.
 */
public class ReceiptApi implements UnsyncModelApi<Receipt> {
    ReceiptInterface receiptInterface;

    private ReceiptInterface getInterface() {
        if(receiptInterface == null)
            receiptInterface = new Server().receiptInterface();
        return receiptInterface;
    }

    @Override
    public void index(Subscriber<List<Receipt>> subscriber) {
        Observable<List<Receipt>> observable = getInterface().index(Receipt.greaterUpdatedAt());
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(subscriber);
    }

    @Override
    public void save(UnsyncModel model, Subscriber<Receipt> subscriber) {
        Receipt receipt = (Receipt) model;
        Observable<Receipt> observable;
        if(StringUtils.isNotNullOrEmpty(receipt.getServerId()))
            observable = getInterface().update(receipt.getServerId(), receipt);
        else
            observable = getInterface().create(receipt);

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(subscriber);
    }
}