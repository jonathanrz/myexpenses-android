package br.com.jonathanzanella.myexpenses.expense;

import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.StringUtils;

import java.io.IOException;
import java.util.List;

import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.server.Server;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jzanella on 6/12/16.
 */
public class ExpenseApi implements UnsyncModelApi<Expense> {
    private static final String LOG_TAG = ExpenseApi.class.getSimpleName();
    ExpenseInterface expenseInterface;

    private ExpenseInterface getInterface() {
        if(expenseInterface == null)
            expenseInterface = new Server().expenseInterface();
        return expenseInterface;
    }

    @Override
    public @Nullable List<Expense> index() {
        Call<List<Expense>> caller = getInterface().index(Expense.greaterUpdatedAt());

        try {
            Response<List<Expense>> response = caller.execute();
            if(response.isSuccessful()) {
                return response.body();
            } else {
                Log.error(LOG_TAG, "Index request error: " + response.message());
            }
        } catch (IOException e) {
            Log.error(LOG_TAG, "Index request error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void save(UnsyncModel model) {
        Expense expense = (Expense) model;
        Call<Expense> caller;
        if(StringUtils.isNotNullOrEmpty(expense.getServerId()))
            caller = getInterface().update(expense.getServerId(), expense);
        else
            caller = getInterface().create(expense);

        try {
            Response<Expense> response = caller.execute();
            if(response.isSuccessful()) {
                model.syncAndSave(response.body());
                Log.info(LOG_TAG, "Updated: " + expense.getData());
            } else {
                Log.error(LOG_TAG, "Save request error: " + response.message() + " uuid: " + expense.getUuid());
            }
        } catch (IOException e) {
            Log.error(LOG_TAG, "Save request error: " + e.getMessage() + " uuid: " + expense.getUuid());
            e.printStackTrace();
        }
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