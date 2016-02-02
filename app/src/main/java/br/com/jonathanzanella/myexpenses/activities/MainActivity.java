package br.com.jonathanzanella.myexpenses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.services.CashierService;
import br.com.jonathanzanella.myexpenses.views.AccountView;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import br.com.jonathanzanella.myexpenses.views.CreditCardView;
import br.com.jonathanzanella.myexpenses.views.ReceiptView;
import br.com.jonathanzanella.myexpenses.views.ResumeView;
import br.com.jonathanzanella.myexpenses.views.SourceView;
import butterknife.Bind;

/**
 * Created by Jonathan Zanella on 25/01/16.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
	@Bind(R.id.act_main_drawer)
	DrawerLayout drawer;
	@Bind(R.id.act_main_navigation_view)
	NavigationView navigationView;
	@Bind(R.id.act_main_content)
	FrameLayout content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
				this, drawer, toolbar,
				R.string.navigation_drawer_open, R.string.navigation_drawer_close
		);

		navigationView.setNavigationItemSelectedListener(this);
		drawer.setDrawerListener(drawerToggle);
		drawerToggle.syncState();

		addViewToContent(new ResumeView(this));

		startService(new Intent(this, CashierService.class));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		for(int i = 0; i < content.getChildCount(); i++) {
			View v = content.getChildAt(i);
			if(v instanceof BaseView)
				((BaseView)v).onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		for(int i = 0; i < content.getChildCount(); i++) {
			View v = content.getChildAt(i);
			if(v instanceof BaseView)
				((BaseView)v).refreshData();
		}
	}

	private void addViewToContent(View child) {
		content.removeAllViews();
		child.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		content.addView(child);
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_resume:
				addViewToContent(new ResumeView(this));
				setTitle(R.string.app_name);
				drawer.closeDrawers();
				return true;
			case R.id.menu_accounts:
				addViewToContent(new AccountView(this));
				setTitle(R.string.accounts);
				drawer.closeDrawers();
				return true;
			case R.id.menu_sources:
				addViewToContent(new SourceView(this));
				setTitle(R.string.sources);
				drawer.closeDrawers();
				return true;
			case R.id.menu_credit_card:
				addViewToContent(new CreditCardView(this));
				setTitle(R.string.credit_card);
				drawer.closeDrawers();
				return true;
			case R.id.menu_receipts:
				addViewToContent(new ReceiptView(this));
				setTitle(R.string.recepts);
				drawer.closeDrawers();
				return true;
		}
		return false;
	}
}
