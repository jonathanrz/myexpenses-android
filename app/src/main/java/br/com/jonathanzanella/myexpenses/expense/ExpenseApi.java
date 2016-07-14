package br.com.jonathanzanella.myexpenses.expense;

import com.raizlabs.android.dbflow.StringUtils;

import java.util.List;

import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.server.Server;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jzanella on 6/12/16.
 */
public class ExpenseApi implements UnsyncModelApi<Expense> {
    ExpenseInterface expenseInterface;

    private ExpenseInterface getInterface() {
        if(expenseInterface == null)
            expenseInterface = new Server().expenseInterface();
        return expenseInterface;
    }

    @Override
    public void index(Subscriber<List<Expense>> subscriber) {
        Observable<List<Expense>> observable = getInterface().index(Expense.greaterUpdatedAt());
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(subscriber);
    }

    @Override
    public void save(UnsyncModel model, Subscriber<Expense> subscriber) {
        Expense expense = (Expense) model;
        Observable<Expense> observable;
        if(StringUtils.isNotNullOrEmpty(expense.getServerId()))
            observable = getInterface().update(expense.getServerId(), expense);
        else
            observable = getInterface().create(expense);

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(subscriber);
    }

    @Override
    public List<Expense> unsyncModels() {
        return Expense.unsync();
    }

    @Override
    public long greaterUpdatedAt() {
        return Expense.greaterUpdatedAt();
    }
}