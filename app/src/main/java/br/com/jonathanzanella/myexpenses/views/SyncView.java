package br.com.jonathanzanella.myexpenses.views;

import android.content.Context;

import br.com.jonathanzanella.myexpenses.R;
import butterknife.ButterKnife;

/**
 * Created by jzanella on 6/5/16.
 */
public class SyncView extends BaseView {
    public SyncView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        inflate(getContext(), R.layout.view_sync, this);
        ButterKnife.bind(this);
    }
}
