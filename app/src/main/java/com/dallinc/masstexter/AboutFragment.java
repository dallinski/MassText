package com.dallinc.masstexter;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonRectangle;

/**
 * Created by dallin on 1/30/15.
 */
public class AboutFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AboutFragment newInstance(int sectionNumber) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AboutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about_fragment, container, false);
        ButtonRectangle b = (ButtonRectangle)rootView.findViewById(R.id.buttonDonate);
        ButtonRectangle b2 = (ButtonRectangle)rootView.findViewById(R.id.buttonChangeLog);
        b.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        b2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        return rootView;
    }
}
