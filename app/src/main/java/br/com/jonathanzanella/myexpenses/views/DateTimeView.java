package br.com.jonathanzanella.myexpenses.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Getter;
import lombok.Setter;

public class DateTimeView extends BaseView {
	public interface Listener {
		void onDateTimeChanged(DateTime currentTime);
	}

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
	public static final SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
	@Bind(R.id.view_datetime_date)
	EditText date;
	@Bind(R.id.view_datetime_hour)
	EditText hour;

	@Setter
	private Listener listener;

	@Getter
	private DateTime currentTime;

	public DateTimeView(Context context) {
		super(context);
	}

	public DateTimeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DateTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_datetime, this);
		ButterKnife.bind(this);
	}

	public void setDate(DateTime currentTime) {
		this.currentTime = currentTime;
		onDateChanged();
	}

	private void onDateChanged() {
		date.setText(dateFormat.format(currentTime.toDate()));
		hour.setText(hourFormat.format(currentTime.toDate()));
		if(listener != null)
			listener.onDateTimeChanged(currentTime);
	}

	@OnClick(R.id.view_datetime_date)
	void onDate() {
		new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				currentTime = currentTime.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth);
				onDateChanged();
			}
		}, currentTime.getYear(), currentTime.getMonthOfYear() - 1, currentTime.getDayOfMonth()).show();
	}

	@OnClick(R.id.view_datetime_hour)
	void onHour() {
		new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				currentTime = currentTime.withHourOfDay(hourOfDay).withMinuteOfHour(minute);
				onDateChanged();
			}
		}, currentTime.getHourOfDay(), currentTime.getMinuteOfHour(), true).show();
	}
}