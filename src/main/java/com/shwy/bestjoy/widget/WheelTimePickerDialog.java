package com.shwy.bestjoy.widget;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.shwy.bestjoy.R;

import java.util.Calendar;


/**
 * Created by bestjoy on 16/10/14.
 */

public class WheelTimePickerDialog extends AlertDialog implements DialogInterface.OnClickListener{
    private final TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private final Calendar mCalendar;

    private WheelNumberPicker hourPicker, minutePicker;
    private View unit_minute;

    /**
     * @param context     The context the dialog is to run in.
     * @param callBack    How the parent is notified that the date is set.
     * @param hour        The initial hour of the dialog.
     * @param minute The initial minute of the dialog.
     */
    public WheelTimePickerDialog(Context context,
                                 TimePickerDialog.OnTimeSetListener callBack,
                                 int hour, int minute) {
        this(context, 0, callBack, hour, minute);
    }


    /**
     * @param context     The context the dialog is to run in.
     * @param theme       the theme to apply to this dialog
     * @param listener    How the parent is notified that the date is set.
     * @param year        The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth  The initial day of the dialog.
     */
    public WheelTimePickerDialog(Context context, int theme, TimePickerDialog.OnTimeSetListener listener, int hour, int minute) {
        super(context, theme);

        mTimeSetListener = listener;
        mCalendar = Calendar.getInstance();

        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        final View view = inflater.inflate(R.layout.wheel_time_picker_dialog, null);
        setView(view);
        setButton(BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this);
        setButton(BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this);
        hourPicker = (WheelNumberPicker) view.findViewById(R.id.hour);
        hourPicker.setNumberValueDuration(1, 23);
        hourPicker.setCurrentValue(hour);

        minutePicker = (WheelNumberPicker) view.findViewById(R.id.minute);
        minutePicker.setNumberValueDuration(0, 59);
        minutePicker.setCurrentValue(minute);

        unit_minute = view.findViewById(R.id.unit_minute);

    }

    public void setMinutePickerVisibility(int visibility) {
        minutePicker.setVisibility(visibility);
        unit_minute.setVisibility(visibility);

    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == BUTTON_POSITIVE) {
            mTimeSetListener.onTimeSet(null, hourPicker.getCurrentValue(), minutePicker.getCurrentValue());
        }
    }
}
