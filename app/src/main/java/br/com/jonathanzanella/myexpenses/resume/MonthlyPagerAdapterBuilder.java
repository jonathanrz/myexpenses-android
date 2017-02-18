package br.com.jonathanzanella.myexpenses.resume;

import android.content.Context;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.views.BaseView;

public interface MonthlyPagerAdapterBuilder {
	BaseView buildView(Context ctx, DateTime date);
}
