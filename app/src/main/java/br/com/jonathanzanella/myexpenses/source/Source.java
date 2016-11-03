package br.com.jonathanzanella.myexpenses.source;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jzanella on 1/31/16.
 */
@EqualsAndHashCode(callSuper = false)
public class Source implements UnsyncModel {
    private static final SourceApi sourceApi = new SourceApi();

	@Getter @Setter
	long id;

	@Getter @Setter @Expose
	String name;

	@Getter @Setter @Expose
	String uuid;

	@Getter @Setter @Expose
	String userUuid;

	@Getter @Setter @Expose @SerializedName("_id")
	String serverId;

    @Getter @Setter @Expose @SerializedName("created_at")
    long createdAt;

    @Getter @Setter @Expose @SerializedName("updated_at")
    long updatedAt;

	@Getter @Setter
    boolean sync;

	@Override
	public boolean isSaved() {
		return id != 0;
	}

	@Override
	public String getData() {
		return "name=" + getName() +
				"\nuuid=" + getUuid();
	}

	@Override
	public void syncAndSave(UnsyncModel serverModel) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getHeader(Context ctx) {
		return ctx.getString(R.string.source);
	}

	@SuppressWarnings("unchecked")
	@Override
    public UnsyncModelApi getServerApi() {
        return sourceApi;
    }
}