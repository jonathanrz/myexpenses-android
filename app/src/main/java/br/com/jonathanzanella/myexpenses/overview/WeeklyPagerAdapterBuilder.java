package br.com.jonathanzanella.myexpenses.overview;

import android.content.Context;

import br.com.jonathanzanella.myexpenses.views.BaseView;

interface WeeklyPagerAdapterBuilder {
	BaseView buildView(Context ctx, WeeklyPagerAdapter.Period period);
}
