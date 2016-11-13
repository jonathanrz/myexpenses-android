package br.com.jonathanzanella.myexpenses.database;

/**
 * Created by jzanella on 11/10/16.
 */

public enum Fields {
	ID("id"),
	NAME("name"),
	UUID("uuid"),
	USER_UUID("userUuid"),
	SERVER_ID("serverId"),
	CREATED_AT("createdAt"),
	UPDATED_AT("updatedAt"),
	AMOUNT("amount"),
	DUE_DATE("due_date"),
	INIT_DATE("init_date"),
	END_DATE("end_date"),
	SYNC("sync");

	private String name;

	Fields(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}