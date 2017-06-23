package br.com.jonathanzanella.myexpenses.source;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;

public class Source implements UnsyncModel {
	private long id;
	@Expose
	private String name;
	@Expose
	private String uuid;
	@Expose @SerializedName("_id")
	private String serverId;
	@Expose @SerializedName("created_at")
	private long createdAt;
	@Expose @SerializedName("updated_at")
	private long updatedAt;
	private boolean sync;

	@NonNull
	@Override
	public String getData() {
		return "name=" + name +
				"\nuuid=" + uuid +
				"\nserverId=" + serverId;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String getServerId() {
		return serverId;
	}

	@Override
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	@Override
	public long getCreatedAt() {
		return createdAt;
	}

	@Override
	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public long getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public void setUpdatedAt(long updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public boolean getSync() {
		return sync;
	}

	@Override
	public void setSync(boolean sync) {
		this.sync = sync;
	}
}