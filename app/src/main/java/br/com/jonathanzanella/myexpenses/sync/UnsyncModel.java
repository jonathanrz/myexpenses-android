package br.com.jonathanzanella.myexpenses.sync;

import br.com.jonathanzanella.myexpenses.database.ModelRepository;

/**
 * Created by jzanella on 6/6/16.
 */
public interface UnsyncModel {
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

	void setSync(boolean b);
}