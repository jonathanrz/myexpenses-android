package br.com.jonathanzanella.myexpenses.source;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class Source implements UnsyncModel {
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
	public String getData() {
		return "name=" + getName() +
				"\nuuid=" + getUuid();
	}
}