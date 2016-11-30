package com.shwy.bestjoy.widget;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.shwy.bestjoy.R;

import java.util.Calendar;

/**
 * Created by bestjoy on 16/10/14.
 */

public class WheelDatePickerDialog extends AlertDialog implements DialogInterface.OnClickListener{
    private final DatePickerDialog.OnDateSetListener mDateSetListener;
    private final Calendar mCalendar;

    private WheelNumberPicker yearPicker, monthPicker, dayPicker;

    private TextView uintDayTextView;

    /**
     * @param context     The context the dialog is to run in.
     * @param callBack    How the parent is notified that the date is set.
     * @param year        The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth  The initial day of the dialog.
     */
    public WheelDatePickerDialog(Context context,
                                DatePickerDialog.OnDateSetListener callBack,
                                int year,
                                int monthOfYear,
                                int dayOfMonth) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth);
    }


    /**
     * @param context     The context the dialog is to run in.
     * @param theme       the theme to apply to this dialog
     * @param listener    How the parent is notified that the date is set.
     * @param year        The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth  The initial day of the dialog.
     */
    public WheelDatePickerDialog(Context context, int theme, DatePickerDialog.OnDateSetListener listener, int year,
                                int monthOfYear, int dayOfMonth) {
        super(context, theme);

        mDateSetListener = listener;
        mCalendar = Calendar.getInstance();

        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        final View view = inflater.inflate(R.layout.wheel_date_picker_dialog, null);
        setView(view);
        setButton(BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this);
        setButton(BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this);
        yearPicker = (WheelNumberPicker) view.findViewById(R.id.year);
        yearPicker.setNumberValueDuration(1997, 2050);
        yearPicker.setCurrentValue(year);
        monthPicker = (WheelNumberPicker) view.findViewById(R.id.month);
        monthPicker.setNumberValueDuration(1, 12);
        monthPicker.setCurrentValue(monthOfYear);
        dayPicker = (WheelNumberPicker) view.findViewById(R.id.day);
        dayPicker.setNumberValueDuration(1, 31);
        dayPicker.setCurrentValue(dayOfMonth);

        uintDayTextView = (TextView) view.findViewById(R.id.unit_day);
    }

    public void setDayPicker(boolean show) {
        dayPicker.setVisibility(show?View.VISIBLE:View.GONE);
        uintDayTextView.setVisibility(show?View.VISIBLE:View.GONE);

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == BUTTON_POSITIVE) {
            mDateSetListener.onDateSet(null, yearPicker.getCurrentValue(), monthPicker.getCurrentValue()-1, dayPicker.getCurrentValue());
        }
    }
}
