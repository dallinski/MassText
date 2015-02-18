package com.dallinc.masstexter.pickers;

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

    public static TimePickerFragment withCustomListener(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        TimePickerFragment pickerFragment = new TimePickerFragment();
        pickerFragment.setOnTimeSetListener(onTimeSetListener);
        return pickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        LocalTime localTime = LocalTime.now();
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), onTimeSetListener, localTime.getHourOfDay(), localTime.getMinuteOfHour(), false);
        return dialog;
    }

    private void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener listener) {
        this.onTimeSetListener = listener;
    }
}
