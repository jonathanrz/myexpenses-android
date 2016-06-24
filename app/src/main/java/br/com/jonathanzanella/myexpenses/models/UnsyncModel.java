package br.com.jonathanzanella.myexpenses.models;

import android.content.Context;

import br.com.jonathanzanella.myexpenses.server.UnsyncModelApi;

/**
 * Created by jzanella on 6/6/16.
 */
public interface UnsyncModel {
	boolean isSaved();
	String getServerId();
	void setServerId(String serverId);
	long getCreatedAt();
	void setCreatedAt(long createdAt);
	long getUpdatedAt();
	void setUpdatedAt(long updatedAt);
	String getData();
	void syncAndSave();

	String getHeader(Context ctx);

	UnsyncModelApi<UnsyncModel> getServerApi();
}