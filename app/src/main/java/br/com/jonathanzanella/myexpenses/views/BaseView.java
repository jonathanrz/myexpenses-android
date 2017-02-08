package br.com.jonathanzanella.myexpenses.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.UiThread;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by jonathan on 08/11/15.
 * Copyright (c) 2015. All rights reserved.
 */
@UiThread
public abstract class BaseView extends FrameLayout {
	protected String filter;

	public BaseView(Context context) {
		super(context);
		init();
	}

	public BaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public BaseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	protected abstract void init();

	public void onActivityResult(int requestCode, int resultCode, Intent data) {}

	public void refreshData() {}

	public void setTabs(TabLayout tabs) {
		tabs.setVisibility(View.GONE);
	}

	public void filter(String s) {
		filter = s;
	}
}