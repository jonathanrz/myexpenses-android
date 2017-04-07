package br.com.jonathanzanella.myexpenses.helpers;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class AdapterColorHelperTest {

	@Test
	public void generate_colors_successfully() throws Exception {
		AdapterColorHelper adapterColorHelper = new AdapterColorHelper(1, 2);

		assertThat(adapterColorHelper.getColor(0), is(1));
		assertThat(adapterColorHelper.getColor(1), is(2));
		assertThat(adapterColorHelper.getColor(2), is(2));
		assertThat(adapterColorHelper.getColor(3), is(1));
		assertThat(adapterColorHelper.getColor(4), is(1));
		assertThat(adapterColorHelper.getColor(5), is(2));
		assertThat(adapterColorHelper.getColor(6), is(2));
		assertThat(adapterColorHelper.getColor(7), is(1));
	}
}