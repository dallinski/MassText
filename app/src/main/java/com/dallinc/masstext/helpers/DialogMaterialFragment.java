package com.dallinc.masstext.helpers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import com.dallinc.masstext.R;

import it.gmariotti.changelibs.library.view.ChangeLogListView;

/**
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class DialogMaterialFragment extends DialogFragment {

    public DialogMaterialFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ChangeLogListView chgList=(ChangeLogListView)layoutInflater.inflate(R.layout.changelog, null);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.change_log_title)
                .setView(chgList)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();
    }
}
