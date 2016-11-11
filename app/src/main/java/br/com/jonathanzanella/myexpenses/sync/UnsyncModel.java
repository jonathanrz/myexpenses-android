package br.com.jonathanzanella.myexpenses.sync;

import android.content.Context;

/**
 * Created by jzanella on 6/6/16.
 */
public interface UnsyncModel {
	boolean isSaved();
	long getId();
	void setId(long id);
	String getServerId();
	String getUuid();
	void setServerId(String serverId);
	long getCreatedAt();
	void setCreatedAt(long createdAt);
	long getUpdatedAt();
	void setUpdatedAt(long updatedAt);
	String getData();
	void syncAndSave(UnsyncModel serverModel);
	void setSync(boolean b);

	String getHeader(Context ctx);

	UnsyncModelApi<UnsyncModel> getServerApi();


}