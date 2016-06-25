package br.com.jonathanzanella.myexpenses.overview;

import android.content.Context;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.views.BaseView;

/**
 * Created by Jonathan Zanella on 08/02/16.
 */
public interface MonthlyPagerAdapterBuilder {
	BaseView buildView(Context ctx, DateTime date);
}
