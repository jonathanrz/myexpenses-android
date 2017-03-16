package br.com.jonathanzanella.myexpenses.receipt;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.server.Server;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import retrofit2.Call;
import retrofit2.Response;

@WorkerThread
public class ReceiptApi implements UnsyncModelApi<Receipt> {
    private static final String LOG_TAG = ReceiptApi.class.getSimpleName();
    private ReceiptInterface receiptInterface;
    private ReceiptRepository receiptRepository;

    @Override
    @Nullable
    public List<Receipt> index() {
        Call<List<Receipt>> caller = getInterface().index(greaterUpdatedAt());

        try {
            Response<List<Receipt>> response = caller.execute();
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
        Receipt receipt = (Receipt) model;
        Call<Receipt> caller;
        if(StringUtils.isNotEmpty(receipt.getServerId()))
            caller = getInterface().update(receipt.getServerId(), receipt);
        else
            caller = getInterface().create(receipt);

        try {
            Response<Receipt> response = caller.execute();
            if(response.isSuccessful()) {
                getReceiptRepository().syncAndSave(response.body());
                Log.info(LOG_TAG, "Updated: " + receipt.getData());
            } else {
                Log.error(LOG_TAG, "Save request error: " + response.message() + " uuid: " + receipt.getUuid());
            }
        } catch (IOException e) {
            Log.error(LOG_TAG, "Save request error: " + e.getMessage() + " uuid: " + receipt.getUuid());
            e.printStackTrace();
        }
    }

	@Override
	public void syncAndSave(UnsyncModel unsync) {
		if(!(unsync instanceof Receipt))
			throw new UnsupportedOperationException("UnsyncModel is not a Receipt");
		getReceiptRepository().syncAndSave((Receipt)unsync);
	}

	@Override
    public List<Receipt> unsyncModels() {
        return getReceiptRepository().unsync();
    }

    @Override
    public long greaterUpdatedAt() {
        return getReceiptRepository().greaterUpdatedAt();
    }

    private ReceiptInterface getInterface() {
        if(receiptInterface == null)
            receiptInterface = new Server().receiptInterface();
        return receiptInterface;
    }

    private ReceiptRepository getReceiptRepository() {
        if(receiptRepository == null)
            receiptRepository = new ReceiptRepository(new RepositoryImpl<Receipt>(MyApplication.getContext()));
        return receiptRepository;
    }
}