package br.com.jonathanzanella.myexpenses.resume;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ResumeView extends BaseView {
    @BindView(R.id.view_resume_pager)
    ViewPager pager;

    public ResumeView(Context context) {
        super(context);
    }

    public ResumeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResumeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        inflate(getContext(), R.layout.view_resume, this);
        ButterKnife.bind(this);

        MonthlyPagerAdapter adapter = new MonthlyPagerAdapter(getContext(), new MonthlyPagerAdapterBuilder() {
	        @Override
	        public BaseView buildView(Context ctx, DateTime date) {
		        return new ResumeMonthlyView(ctx, date);
	        }
        });
        pager.setAdapter(adapter);
        pager.setCurrentItem(MonthlyPagerAdapter.INIT_MONTH_VISIBLE);
    }

	@Override
	public void setTabs(TabLayout tabs) {
		tabs.setupWithViewPager(pager);
		tabs.setVisibility(View.VISIBLE);
	}
}
