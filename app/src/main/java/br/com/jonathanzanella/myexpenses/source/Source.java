package br.com.jonathanzanella.myexpenses.source;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import lombok.Getter;
import lombok.Setter;

import static br.com.jonathanzanella.myexpenses.log.Log.warning;

/**
 * Created by jzanella on 1/31/16.
 */
@Table(database = MyDatabase.class)
public class Source extends BaseModel implements UnsyncModel {
    private static final SourceApi sourceApi = new SourceApi();

	@Column
	@PrimaryKey(autoincrement = true)
	long id;

	@Column @NotNull
	@Getter @Setter @Expose
	String name;

	@Column @Unique @NotNull
	@Getter @Setter @Expose
	String uuid;

	@Column @NotNull @Getter @Setter @Expose
	String userUuid;

	@Column @Unique
	@Getter @Setter @Expose @SerializedName("_id")
	String serverId;

    @Column @Getter @Setter @Expose @SerializedName("created_at")
    long createdAt;

    @Column @Getter @Setter @Expose @SerializedName("updated_at")
    long updatedAt;

	@Column
    boolean sync;

	private static From<Source> initQuery() {
		return SQLite.select().from(Source.class);
	}

	public static List<Source> all() {
		return initQuery().queryList();
	}

	public static List<Source> user() {
		return initQuery().where(Source_Table.userUuid.is(Environment.CURRENT_USER_UUID)).queryList();
	}

    public static List<Source> unsync() {
        return initQuery().where(Source_Table.sync.eq(false)).queryList();
    }

	public static Source find(String uuid) {
		return initQuery().where(Source_Table.uuid.eq(uuid)).querySingle();
	}

    public static long greaterUpdatedAt() {
        Source source = initQuery().orderBy(Source_Table.updatedAt, false).limit(1).querySingle();
        if(source == null)
            return 0L;
        return source.getUpdatedAt();
    }

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
	public String getHeader(Context ctx) {
		return ctx.getString(R.string.source);
	}

	@SuppressWarnings("unchecked")
	@Override
    public UnsyncModelApi getServerApi() {
        return sourceApi;
    }

	@Override
	public void save() {
		if(id == 0 && uuid == null)
			uuid = UUID.randomUUID().toString();
		sync = false;
		super.save();
	}

	@Override
	public void syncAndSave() {
		Source source = Source.find(uuid);
		if(source != null && source.id != id) {
			if(source.getUpdatedAt() != getUpdatedAt())
				warning("Source overwritten", getData());
			id = source.id;
		}
		save();
		sync = true;
		super.save();
	}
}