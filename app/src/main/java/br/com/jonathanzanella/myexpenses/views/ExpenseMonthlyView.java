package br.com.jonathanzanella.myexpenses.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.activities.EditExpenseActivity;
import br.com.jonathanzanella.myexpenses.adapter.ExpenseAdapter;
import br.com.jonathanzanella.myexpenses.model.Expense;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class ExpenseMonthlyView extends BaseView {
    private static final int REQUEST_ADD_EXPENSE = 1006;
    private ExpenseAdapter adapter;

    @Bind(R.id.view_expenses_monthly_list)
    RecyclerView sources;

    public ExpenseMonthlyView(Context context) {
        super(context);
    }

    public ExpenseMonthlyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpenseMonthlyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        inflate(getContext(), R.layout.view_monthly_expenses, this);
        ButterKnife.bind(this);

        adapter = new ExpenseAdapter();
        adapter.loadData();

        sources.setAdapter(adapter);
        sources.setLayoutManager(new GridLayoutManager(getContext(), 1));
        sources.setItemAnimator(new DefaultItemAnimator());
    }

    @OnClick(R.id.view_expenses_fab)
    void onFab() {
        Context ctx = getContext();
        Intent i = new Intent(getContext(), EditExpenseActivity.class);
        if(ctx instanceof Activity) {
            ((Activity) ctx).startActivityForResult(i, REQUEST_ADD_EXPENSE);
        } else {
            ctx.startActivity(i);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ADD_EXPENSE:
                if(resultCode == Activity.RESULT_OK) {
                    Expense e = Expense.find(data.getLongExtra(EditExpenseActivity.KEY_EXPENSE_ID, 0L));
                    if(e != null)
                        adapter.addExpense(e);
                }
                break;
        }
    }

    @Override
    public void refreshData() {
        super.refreshData();

        adapter.loadData();
        adapter.notifyDataSetChanged();
    }
}
