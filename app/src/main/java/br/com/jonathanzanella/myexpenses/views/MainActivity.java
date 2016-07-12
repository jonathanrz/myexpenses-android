package br.com.jonathanzanella.myexpenses.views;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.AccountView;
import br.com.jonathanzanella.myexpenses.bill.BillView;
import br.com.jonathanzanella.myexpenses.card.CardView;
import br.com.jonathanzanella.myexpenses.expense.ExpenseView;
import br.com.jonathanzanella.myexpenses.log.LogsView;
import br.com.jonathanzanella.myexpenses.overview.OverviewExpensesView;
import br.com.jonathanzanella.myexpenses.receipt.ReceiptView;
import br.com.jonathanzanella.myexpenses.resume.ResumeView;
import br.com.jonathanzanella.myexpenses.source.SourceView;
import br.com.jonathanzanella.myexpenses.sync.SyncView;
import butterknife.Bind;

/**
 * Created by Jonathan Zanella on 25/01/16.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {
	@Bind(R.id.act_main_drawer)
	DrawerLayout drawer;
	@Bind(R.id.act_main_navigation_view)
	NavigationView navigationView;
	@Bind(R.id.act_main_content)
	FrameLayout content;
	@Bind(R.id.tabs)
	TabLayout tabs;

	private BaseView currentView;
	private String filter;

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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setOnQueryTextListener(this);

		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		filter = newText;
		currentView.filter(newText);
		return false;
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

	private void addViewToContent(BaseView child) {
		content.removeAllViews();
		child.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		content.addView(child);
		child.setTabs(tabs);
		child.filter(filter);
		currentView = child;
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
				addViewToContent(new CardView(this));
				setTitle(R.string.credit_card);
				drawer.closeDrawers();
				return true;
			case R.id.menu_receipts:
				addViewToContent(new ReceiptView(this));
				setTitle(R.string.receipts);
				drawer.closeDrawers();
				return true;
			case R.id.menu_expenses:
				addViewToContent(new ExpenseView(this));
				setTitle(R.string.expenses);
				drawer.closeDrawers();
				return true;
			case R.id.menu_bills:
				addViewToContent(new BillView(this));
				setTitle(R.string.bills);
				drawer.closeDrawers();
				return true;
			case R.id.menu_overview:
				addViewToContent(new OverviewExpensesView(this));
				setTitle(R.string.overview);
				drawer.closeDrawers();
				return true;
			case R.id.menu_sync:
				addViewToContent(new SyncView(this));
				setTitle(R.string.sync);
				drawer.closeDrawers();
				return true;
			case R.id.menu_log:
				addViewToContent(new LogsView(this));
				setTitle(R.string.log);
				drawer.closeDrawers();
				return true;
		}
		return false;
	}
}
