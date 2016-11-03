package br.com.jonathanzanella.myexpenses.source;

import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.StringUtils;

import java.io.IOException;
import java.util.List;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.server.Server;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jzanella on 6/12/16.
 */
public class SourceApi implements UnsyncModelApi<Source> {
	private static final String LOG_TAG = SourceApi.class.getSimpleName();
	private SourceInterface sourceInterface;

    private SourceInterface getInterface() {
        if(sourceInterface == null)
            sourceInterface = new Server().sourceInterface();
        return sourceInterface;
    }

    @Override
    public @Nullable List<Source> index() {
        Call<List<Source>> caller = getInterface().index(new SourceRepository(new DatabaseHelper(MyApplication.getContext())).greaterUpdatedAt());

        try {
            Response<List<Source>> response = caller.execute();
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
        Source source = (Source) model;
        Call<Source> caller;
        if(StringUtils.isNotNullOrEmpty(source.getServerId()))
            caller = getInterface().update(source.getServerId(), source);
        else
            caller = getInterface().create(source);

        try {
            Response<Source> response = caller.execute();
            if(response.isSuccessful()) {
                model.syncAndSave(response.body());
	            Log.info(LOG_TAG, "Updated: " + source.getData());
            } else {
                Log.error(LOG_TAG, "Save request error: " + response.message() + " uuid: " + source.getUuid());
            }
        } catch (IOException e) {
            Log.error(LOG_TAG, "Save request error: " + e.getMessage() + " uuid: " + source.getUuid());
            e.printStackTrace();
        }
    }

    @Override
    public List<Source> unsyncModels() {
        return new SourceRepository(new DatabaseHelper(MyApplication.getContext())).unsync();
    }

    @Override
    public long greaterUpdatedAt() {
        return new SourceRepository(new DatabaseHelper(MyApplication.getContext())).greaterUpdatedAt();
    }
}