package br.com.jonathanzanella.myexpenses.database;

import lombok.Data;

/**
 * Created by jzanella on 11/12/16.
 */

@Data
public class Select {
	String where;
	String [] parameters;

	Select(String where, String [] parameters) {
		this.where = where;
		this.parameters = parameters;
	}
}