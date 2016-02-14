package br.com.jonathanzanella.myexpenses.adapters;

import android.content.Context;

import br.com.jonathanzanella.myexpenses.views.BaseView;

/**
 * Created by Jonathan Zanella on 08/02/16.
 */
public interface WeeklyPagerAdapterBuilder {
	BaseView buildView(Context ctx, WeeklyPagerAdapter.Period period);
}
