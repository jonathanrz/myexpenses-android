package br.com.jonathanzanella.myexpenses.helpers.builder;

import br.com.jonathanzanella.myexpenses.source.Source;

/**
 * Created by jzanella on 8/28/16.
 */

public class SourceBuilder {
	private String name = "sourceTest";

	public SourceBuilder name(String name) {
		this.name = name;
		return this;
	}

	public Source build() {
		Source source = new Source();
		source.setName(name);
		return source;
	}
}