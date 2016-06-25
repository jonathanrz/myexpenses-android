package br.com.jonathanzanella.myexpenses.overview;

import android.content.Context;

import br.com.jonathanzanella.myexpenses.views.BaseView;

/**
 * Created by Jonathan Zanella on 08/02/16.
 */
interface WeeklyPagerAdapterBuilder {
	BaseView buildView(Context ctx, WeeklyPagerAdapter.Period period);
}
