package com.dallinc.masstext.pickers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.dallinc.masstext.helpers.Constants;
import com.dallinc.masstext.R;

/**
 * Created by dallin on 2/16/15.
 */
public class DayOfWeekPickerFragment extends DialogFragment {
    private static OnMyDialogResult mDialogResult; // the callback

    public static DayOfWeekPickerFragment withCustomListener(OnMyDialogResult dialogResult) {
        DayOfWeekPickerFragment pickerFragment = new DayOfWeekPickerFragment();
        mDialogResult = dialogResult;
        return pickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_set_day_of_week);

        builder.setItems(Constants.DAYS_OF_THE_WEEK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int n) {
                if (mDialogResult != null) {
                    mDialogResult.finish(Constants.DAYS_OF_THE_WEEK[n]);
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
