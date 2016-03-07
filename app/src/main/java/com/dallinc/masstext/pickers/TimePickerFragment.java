package com.dallinc.masstext.pickers;

import android.app.TimePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.joda.time.LocalTime;

/**
 * Created by dallin on 2/16/15.
 */
public class TimePickerFragment extends DialogFragment {
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    private boolean is24HourFormat = false;

    public static TimePickerFragment withCustomListener(TimePickerDialog.OnTimeSetListener onTimeSetListener, boolean is24HourFormat) {
        TimePickerFragment pickerFragment = new TimePickerFragment();
        pickerFragment.setOnTimeSetListener(onTimeSetListener, is24HourFormat);
        return pickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        LocalTime localTime = LocalTime.now();
        TimePickerDialog dialog = new TimePickerDialog(getActivity(),
                                                        onTimeSetListener,
                                                        localTime.getHourOfDay(),
                                                        localTime.getMinuteOfHour(),
                                                        this.is24HourFormat);
        return dialog;
    }

    private void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener listener, boolean is24HourFormat) {
        this.onTimeSetListener = listener;
        this.is24HourFormat = is24HourFormat;
    }
}
