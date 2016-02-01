package br.com.jonathanzanella.myexpenses.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import br.com.jonathanzanella.myexpenses.R;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jonathan on 08/11/15.
 * Copyright (c) 2015. All rights reserved.
 */
public class BaseActivity extends AppCompatActivity {
	@Bind(R.id.toolbar)
	Toolbar toolbar;

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		ButterKnife.bind(this);

		if(toolbar != null) {
			setSupportActionBar(toolbar);
			if(!(this instanceof MainActivity))
				displayHomeAsUp();
		}

		storeBundle(savedInstanceState);
		storeBundle(getIntent().getExtras());
	}

	private void displayHomeAsUp() {
		ActionBar actionBar = getSupportActionBar();
		if(actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
			toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
	}

	protected void storeBundle(Bundle extras) {
	}
}