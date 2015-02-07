package com.dallinc.masstexter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.List;

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
        final View rootView = inflater.inflate(R.layout.messaging_fragment, container, false);

        final FloatingActionsMenu composeButton = (FloatingActionsMenu) rootView.findViewById(R.id.buttonComposeMessage);
        FloatingActionButton usingTemplateButton = (FloatingActionButton) rootView.findViewById(R.id.buttonComposeUsingTemplate);
        usingTemplateButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                composeButton.collapse();
                final AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
                builder.setTitle("Select Template");
                List<Template> _templates = Template.listAll(Template.class);
                if(_templates.size() < 1) {
                    Toast.makeText(rootView.getContext(), "You do not have any templates saved!", Toast.LENGTH_LONG).show();
                    return;
                }
                final Template[] templates = _templates.toArray(new Template[_templates.size()]);
                final String[] template_titles = new String[templates.length];
                for(int i=0; i<templates.length; i++) {
                    template_titles[i] = templates[i].title;
                }
                builder.setItems(template_titles, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(rootView.getContext(), Compose.class);
                        intent.putExtra("template_id", templates[which].getId());
                        startActivity(intent);
                    }
                });
                builder.create().show();
            }
        });

        FloatingActionButton quickComposeButton = (FloatingActionButton) rootView.findViewById(R.id.buttonQuickCompose);
        quickComposeButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                composeButton.collapse();
                Intent intent = new Intent(rootView.getContext(), Compose.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
