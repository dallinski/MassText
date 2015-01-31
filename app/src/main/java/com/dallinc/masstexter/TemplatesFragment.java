package com.dallinc.masstexter;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonFloat;

/**
 * Created by dallin on 1/30/15.
 */
public class TemplatesFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TemplatesFragment newInstance(int sectionNumber) {
        TemplatesFragment fragment = new TemplatesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public TemplatesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.templates_fragment, container, false);
        ButtonFloat b = (ButtonFloat)rootView.findViewById(R.id.buttonAddTemplate);
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = rootView.getContext().getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        b.setBackgroundColor(color);
        return rootView;
    }
}
