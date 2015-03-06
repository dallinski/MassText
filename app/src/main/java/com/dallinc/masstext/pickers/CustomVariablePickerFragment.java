package com.dallinc.masstext.pickers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dallinc.masstext.R;
import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;

/**
 * Created by dallin on 2/16/15.
 */
public class CustomVariablePickerFragment extends DialogFragment {
    private static String variableName;
    private static OnMyDialogResult mDialogResult; // the callback

    public static CustomVariablePickerFragment withCustomListener(OnMyDialogResult dialogResult, String var_name) {
        CustomVariablePickerFragment pickerFragment = new CustomVariablePickerFragment();
        mDialogResult = dialogResult;
        variableName = var_name;
        return pickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set Custom Variable");

        final FloatingLabelEditText editText = new FloatingLabelEditText(getActivity());
        editText.getInputWidget().setSingleLine();
        editText.getInputWidget().setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        editText.setLabelText(variableName);
        editText.setLabelColor(getResources().getColor(R.color.colorPrimaryDark));
        editText.setLabelTextSize(18);
        editText.setInputWidgetTextSize(26);
        editText.setInputWidgetTextColor(Color.BLACK);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setPadding(25, 0, 25, 0);
        layout.addView(editText);

        builder.setView(layout);
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if( mDialogResult != null ){
                    String inputVariable = editText.getInputWidgetText().toString();
                    if(inputVariable.length() > 0) {
                        mDialogResult.finish(inputVariable);
                    } else {
                        Toast.makeText(getActivity(), "Variable value not set", Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
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
