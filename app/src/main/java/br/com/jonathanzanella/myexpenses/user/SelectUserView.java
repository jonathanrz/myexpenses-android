package br.com.jonathanzanella.myexpenses.user;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SelectUserView extends BaseView {
	@Bind(R.id.view_select_user_jonathan)
	RadioButton userJonathan;
	@Bind(R.id.view_select_user_thainara)
	RadioButton userThainara;

	public SelectUserView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SelectUserView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_select_user, this);
		ButterKnife.bind(this);

		if(Environment.CURRENT_USER.equals("Thainara"))
			userThainara.setChecked(true);
		else
			userJonathan.setChecked(true);
	}

	public void setSelectedUser(String userUuid) {
		if(userUuid.equals(Users.THAINARA))
			userThainara.setChecked(true);
		else
			userJonathan.setChecked(true);
	}

	public String getSelectedUser() {
		if(userThainara.isChecked())
			return Users.THAINARA;
		return Users.JONATHAN;
	}
}
