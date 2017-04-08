package br.com.jonathanzanella.myexpenses.helpers;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class AdapterColorHelperTest {

	@Test
	public void generate_colors_successfully() throws Exception {
		AdapterColorHelper adapterColorHelper = new AdapterColorHelper(1, 2);

		assertThat(adapterColorHelper.getColorForGridWithTwoColor(0), is(1));
		assertThat(adapterColorHelper.getColorForGridWithTwoColor(1), is(2));
		assertThat(adapterColorHelper.getColorForGridWithTwoColor(2), is(2));
		assertThat(adapterColorHelper.getColorForGridWithTwoColor(3), is(1));
		assertThat(adapterColorHelper.getColorForGridWithTwoColor(4), is(1));
		assertThat(adapterColorHelper.getColorForGridWithTwoColor(5), is(2));
		assertThat(adapterColorHelper.getColorForGridWithTwoColor(6), is(2));
		assertThat(adapterColorHelper.getColorForGridWithTwoColor(7), is(1));

		assertThat(adapterColorHelper.getColorForLinearLayout(0), is(1));
		assertThat(adapterColorHelper.getColorForLinearLayout(1), is(2));
		assertThat(adapterColorHelper.getColorForLinearLayout(2), is(1));
		assertThat(adapterColorHelper.getColorForLinearLayout(3), is(2));
	}
}