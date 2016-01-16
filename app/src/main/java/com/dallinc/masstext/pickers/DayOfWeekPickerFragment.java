package com.dallinc.masstext.pickers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.dallinc.masstext.R;

/**
 * Created by dallin on 2/16/15.
 */
public class DayOfWeekPickerFragment extends DialogFragment {
    private static OnMyDialogResult mDialogResult; // the callback
    private final String[] getDaysOfTheWeek(Context c) {
            return new String[] {
                c.getString(R.string.var_monday),
                c.getString(R.string.var_tuesday),
                c.getString(R.string.var_wednesday),
                c.getString(R.string.var_thursday),
                c.getString(R.string.var_friday),
                c.getString(R.string.var_saturday),
                c.getString(R.string.var_sunday)
            };
    };

    public static DayOfWeekPickerFragment withCustomListener(OnMyDialogResult dialogResult) {
        DayOfWeekPickerFragment pickerFragment = new DayOfWeekPickerFragment();
        mDialogResult = dialogResult;
        return pickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_set_day_of_week);

        builder.setItems(getDaysOfTheWeek(builder.getContext()), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int n) {
                if (mDialogResult != null) {
                    mDialogResult.finish(getDaysOfTheWeek(builder.getContext())[n]);
                }
                dialogInterface.dismiss();
                return;
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    public interface OnMyDialogResult{
        void finish(String result);
    }
}
