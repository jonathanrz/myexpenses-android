package br.com.jonathanzanella.myexpenses.models;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jzanella on 1/31/16.
 */
@Table(database = MyDatabase.class)
public class Source extends BaseModel implements UnsyncModel {
	@Column
	@PrimaryKey(autoincrement = true) @Getter
	long id;

	@Column @Getter @Setter
	String name;

	@Column @Getter @Setter @SerializedName("_id")
	String serverId;

    @Column @Getter @Setter @SerializedName("created_at")
    long createdAt;

    @Column @Getter @Setter @SerializedName("updated_at")
    long updatedAt;

	public static List<Source> all() {
		return initQuery().queryList();
	}

	private static From<Source> initQuery() {
		return SQLite.select().from(Source.class);
	}

	public static Source find(long id) {
		return initQuery().where(Source_Table.id.eq(id)).querySingle();
	}

    public static long greaterUpdatedAt() {
        Source source = initQuery().orderBy(Source_Table.updatedAt, false).limit(1).querySingle();
        if(source == null)
            return 0L;
        return source.getUpdatedAt();
    }

	@Override
	public String getData() {
		return "name=" + getName();
	}
}
