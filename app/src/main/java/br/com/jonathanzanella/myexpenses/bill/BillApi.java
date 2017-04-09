package br.com.jonathanzanella.myexpenses.bill;

import android.support.annotation.WorkerThread;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.account.AccountApi;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.server.Server;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import retrofit2.Call;
import retrofit2.Response;

@WorkerThread
public class BillApi implements UnsyncModelApi<Bill> {
	private static final String LOG_TAG = AccountApi.class.getSimpleName();
	private final BillRepository billRepository;
	private BillInterface billInterface;

	public BillApi() {
		ExpenseRepository expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(MyApplication.getContext()));
		billRepository = new BillRepository(new RepositoryImpl<Bill>(MyApplication.getContext()), expenseRepository);
	}

	@Override
	public List<Bill> index() {
		Call<List<Bill>> caller = getInterface().index(billRepository.greaterUpdatedAt());

		try {
			Response<List<Bill>> response = caller.execute();
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
        Bill bill = (Bill) model;
        Call<Bill> caller;
        if(StringUtils.isNotEmpty(bill.getServerId()))
            caller = getInterface().update(bill.getServerId(), bill);
        else
            caller = getInterface().create(bill);

        try {
            Response<Bill> response = caller.execute();
            if(response.isSuccessful()) {
                billRepository.syncAndSave(response.body());
	            Log.info(LOG_TAG, "Updated: " + bill.getData());
            } else {
                Log.error(LOG_TAG, "Save request error: " + response.message() + " uuid: " + bill.getUuid());
            }
        } catch (IOException e) {
	        Log.error(LOG_TAG, "Save request error: " + e.getMessage() + " uuid: " + bill.getUuid());
            e.printStackTrace();
        }
	}

	@Override
	public void syncAndSave(UnsyncModel unsync) {
		if(!(unsync instanceof Bill))
			throw new UnsupportedOperationException("UnsyncModel is not a Bill");
		billRepository.syncAndSave((Bill)unsync);
	}

	@Override
	public List<Bill> unsyncModels() {
		return billRepository.unsync();
	}

	@Override
	public long greaterUpdatedAt() {
		return billRepository.greaterUpdatedAt();
	}

	private BillInterface getInterface() {
		if(billInterface == null)
			billInterface = new Server(MyApplication.getContext()).billInterface();
		return billInterface;
	}
}