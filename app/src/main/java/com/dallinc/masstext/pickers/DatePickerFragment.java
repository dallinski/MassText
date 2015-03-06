package com.dallinc.masstext.pickers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.joda.time.LocalDate;

/**
 * Created by dallin on 2/16/15.
 */
public class DatePickerFragment extends DialogFragment {
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    public static DatePickerFragment withCustomListener(DatePickerDialog.OnDateSetListener onDateSetListener) {
        DatePickerFragment pickerFragment = new DatePickerFragment();
        pickerFragment.setOnDateSetListener(onDateSetListener);
        return pickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        LocalDate localDate = LocalDate.now();
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), onDateSetListener, localDate.getYear(), localDate.getMonthOfYear()-1, localDate.getDayOfMonth());
        return dialog;
    }

    private void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        this.onDateSetListener = listener;
    }
}
