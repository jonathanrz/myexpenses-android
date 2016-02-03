package br.com.jonathanzanella.myexpenses.views;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.adapter.ResumePagerAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan Zanella on 03/02/16.
 */
public class ResumeView extends BaseView {
    @Bind(R.id.view_resume__tabs)
    TabLayout tabs;
    @Bind(R.id.view_resume_pager)
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

        ResumePagerAdapter adapter = new ResumePagerAdapter(getContext());
        pager.setAdapter(adapter);
        pager.setCurrentItem(ResumePagerAdapter.INIT_MONTH_VISIBLE);

        tabs.setupWithViewPager(pager);
    }
}
