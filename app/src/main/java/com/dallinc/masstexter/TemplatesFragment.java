package com.dallinc.masstexter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

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

        FloatingActionButton clickButton = (FloatingActionButton) rootView.findViewById(R.id.buttonCreateTemplate);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Stub: Create a new template", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
