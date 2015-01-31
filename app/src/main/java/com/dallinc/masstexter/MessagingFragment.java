package com.dallinc.masstexter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

/**
 * Created by dallin on 1/30/15.
 */
public class MessagingFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MessagingFragment newInstance(int sectionNumber) {
        MessagingFragment fragment = new MessagingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MessagingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.messaging_fragment, container, false);

        final FloatingActionsMenu composeButton = (FloatingActionsMenu) rootView.findViewById(R.id.buttonComposeMessage);
        FloatingActionButton usingTemplateButton = (FloatingActionButton) rootView.findViewById(R.id.buttonComposeUsingTemplate);
        usingTemplateButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                composeButton.collapse();
                Toast.makeText(v.getContext(), "Stub: Create message using Template", Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton quickComposeButton = (FloatingActionButton) rootView.findViewById(R.id.buttonQuickCompose);
        quickComposeButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                composeButton.collapse();
                Toast.makeText(v.getContext(), "Stub: Create message (quick compose)", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
