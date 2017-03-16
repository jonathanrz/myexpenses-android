package br.com.jonathanzanella.myexpenses.source;

import android.support.annotation.Nullable;

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

public class SourceApi implements UnsyncModelApi<Source> {
	private static final String LOG_TAG = SourceApi.class.getSimpleName();
	private SourceInterface sourceInterface;
    private SourceRepository sourceRepository;

    @Override
    public @Nullable List<Source> index() {
	    long lastUpdatedAt = new SourceRepository(new RepositoryImpl<Source>(MyApplication.getContext())).greaterUpdatedAt();
	    Call<List<Source>> caller = getInterface().index(lastUpdatedAt);

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
        if(StringUtils.isNotEmpty(source.getServerId()))
            caller = getInterface().update(source.getServerId(), source);
        else
            caller = getInterface().create(source);

        try {
            Response<Source> response = caller.execute();
            if(response.isSuccessful()) {
                getSourceRepository().syncAndSave(response.body());
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
	public void syncAndSave(UnsyncModel unsync) {
		if(!(unsync instanceof Source))
			throw new UnsupportedOperationException("UnsyncModel is not a Source");
		getSourceRepository().syncAndSave((Source)unsync);
	}

	@Override
    public List<Source> unsyncModels() {
        return new SourceRepository(new RepositoryImpl<Source>(MyApplication.getContext())).unsync();
    }

    @Override
    public long greaterUpdatedAt() {
        return new SourceRepository(new RepositoryImpl<Source>(MyApplication.getContext())).greaterUpdatedAt();
    }

	private SourceInterface getInterface() {
		if(sourceInterface == null)
			sourceInterface = new Server().sourceInterface();
		return sourceInterface;
	}

	private SourceRepository getSourceRepository() {
		if(sourceRepository == null)
			sourceRepository = new SourceRepository(new RepositoryImpl<Source>(MyApplication.getContext()));
		return sourceRepository;
	}
}