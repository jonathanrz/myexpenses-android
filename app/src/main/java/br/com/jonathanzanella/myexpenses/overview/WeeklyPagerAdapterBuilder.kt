package br.com.jonathanzanella.myexpenses.overview

import android.content.Context

import br.com.jonathanzanella.myexpenses.views.BaseView

interface WeeklyPagerAdapterBuilder {
    fun buildView(ctx: Context, period: WeeklyPagerAdapter.Period): BaseView
}
