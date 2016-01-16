package com.dallinc.masstext;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dallinc.masstext.helpers.Constants;
import com.dallinc.masstext.models.Template;
import com.dallinc.masstext.templates.EditTemplate;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dallin on 1/30/15.
 */
public class TemplatesFragment extends Fragment {
    private BroadcastReceiver receiver;
    TemplateAdapter ca;
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
        final View rootView = inflater.inflate(R.layout.templates_fragment, container, false);
        rootView.setFocusableInTouchMode(true);
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK)
                {
                    MainActivity.switchFragments(0);
                }
                return true;
            }
        });

        FloatingActionButton clickButton = (FloatingActionButton) rootView.findViewById(R.id.buttonCreateTemplate);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext(), EditTemplate.class);
                startActivity(intent);
            }
        });

        RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.templateCardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        ca = new TemplateAdapter(Template.listAll(Template.class));
        recList.setAdapter(ca);

        ca.updateTemplates();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ca.updateTemplates();
            }
        };
        LocalBroadcastManager.getInstance(getActivity().getBaseContext()).registerReceiver((receiver), new IntentFilter(Constants.BROADCAST_RELOAD_TEMPLATES));

        return rootView;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity().getBaseContext()).unregisterReceiver(receiver);
        super.onDestroy();
    }

    public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder> {

        private List<Template> objects;

        public TemplateAdapter(List<Template> objects) {
            this.objects = objects;
        }

        @Override
        public int getItemCount() {
            return objects.size();
        }

        @Override
        public void onBindViewHolder(final TemplateViewHolder TemplateViewHolder, int i) {
            final Template template = objects.get(i);
            TemplateViewHolder.vTitle.setText(template.title);
            String body = template.body;
            template.buildArrayListFromString();
            for(String variable: template.variables) {
                body = body.replaceFirst("¬", variable);
            }
            TemplateViewHolder.vBody.setText(body);
            TemplateViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TemplateViewHolder.itemView.getContext(), EditTemplate.class);
                    intent.putExtra("template_id", template.getId());
                    startActivity(intent);
                }
            });
            TemplateViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(TemplateViewHolder.itemView.getContext());
                    builder.setTitle(R.string.delete_template);
                    builder.setMessage(getString(R.string.want_to_delete_template) + " \"" + template.title + "\"?");
                    builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            template.delete();
                            objects = Template.listAll(Template.class);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                    return false;
                }
            });
        }

        @Override
        public TemplateViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.template_card_layout, viewGroup, false);
            return new TemplateViewHolder(itemView);
        }

        public class TemplateViewHolder extends RecyclerView.ViewHolder {
            protected TextView vTitle;
            protected TextView vBody;

            public TemplateViewHolder(View v) {
                super(v);
                vTitle =  (TextView) v.findViewById(R.id.templateCardTitle);
                vBody = (TextView)  v.findViewById(R.id.templateCardBody);
                ImageView iv = (ImageView) v.findViewById(R.id.recipientIcon);
                iv.setVisibility(View.GONE); // don't show the user icon on template cards
            }
        }

        public void updateTemplates() {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
            boolean hasSeenExample = prefs.getBoolean(Constants.HAS_SEEN_EXAMPLE_TEMPLATE, false);
            if(!hasSeenExample) {
                Template example1 = getExample1();
                example1.save();
                objects.add(example1);
                Template example2 = getExample2();
                example2.save();
                objects.add(example2);
                Template example3 = getExample3();
                example3.save();
                objects.add(example3);
                Template example4 = getExample4();
                example4.save();
                objects.add(example4);
                prefs.edit().putBoolean(Constants.HAS_SEEN_EXAMPLE_TEMPLATE, true).commit();
                notifyDataSetChanged();
            }
        }
    }

    public final String EXAMPLE_TEMPLATE_1_TITLE = "Template Instructions";
    public final String EXAMPLE_TEMPLATE_1_BODY = "Templates are really useful, but only if you know how to use them to their fullest.\n\nTemplates are mostly useful when you are sending the same basic message every day/week/month/etc.\n\nTemplates are saved so that you don't have to type up a new message each time.";
    public final ArrayList<String> EXAMPLE_TEMPLATE_1_VARIABLES() {
        return new ArrayList<String>();
    }
    public final Template getExample1() {
        return new Template(EXAMPLE_TEMPLATE_1_TITLE, EXAMPLE_TEMPLATE_1_BODY, EXAMPLE_TEMPLATE_1_VARIABLES());
    }
    public final String EXAMPLE_TEMPLATE_2_TITLE = "How to use variables 1";
    public final String EXAMPLE_TEMPLATE_2_BODY = "When you are editing a template (and your cursor is in the body field), a button will appear in the top right portion of the screen next to the save button.\n\nThat button is used to insert variables.\n\nPlace your cursor where you would like a variable, then hit the button and select a variable.\n\nVariables look like this (¬) once inserted.";
    public final ArrayList<String> EXAMPLE_TEMPLATE_2_VARIABLES() {
        ArrayList<String> vars = new ArrayList<String>();
        vars.add(getString(R.string.var_date));
        return vars;
    }
    public final Template getExample2() {
        return new Template(EXAMPLE_TEMPLATE_2_TITLE, EXAMPLE_TEMPLATE_2_BODY, EXAMPLE_TEMPLATE_2_VARIABLES());
    }
    public final String EXAMPLE_TEMPLATE_3_TITLE = "How to use variables 2";
    public final String EXAMPLE_TEMPLATE_3_BODY = "You will \"fill in the blank\" when you send a message using that template.\n\nFor example: if I were to send a message using this template, I would be prompted to select a date to go here (¬) before I could send the message.";
    public final ArrayList<String> EXAMPLE_TEMPLATE_3_VARIABLES() {
        ArrayList<String> vars = new ArrayList<String>();
        vars.add(getString(R.string.var_date));
        return vars;
    }
    public final Template getExample3() {
        return new Template(EXAMPLE_TEMPLATE_3_TITLE, EXAMPLE_TEMPLATE_3_BODY, EXAMPLE_TEMPLATE_3_VARIABLES());
    }
    public final String EXAMPLE_TEMPLATE_4_TITLE = "Basketball Group";
    public final String EXAMPLE_TEMPLATE_4_BODY = "Hey ¬, we're going to be playing basketball on ¬ at ¬. See you there at ¬!";
    public final ArrayList<String> EXAMPLE_TEMPLATE_4_VARIABLES() {
        ArrayList<String> vars = new ArrayList<String>();
        vars.add(getString(R.string.var_first_name));
        vars.add(getString(R.string.var_day_of_week));
        vars.add(getString(R.string.var_location));
        vars.add(getString(R.string.var_time));
        return vars;
    }
    public final Template getExample4() {
        return new Template(EXAMPLE_TEMPLATE_4_TITLE, EXAMPLE_TEMPLATE_4_BODY, EXAMPLE_TEMPLATE_4_VARIABLES());
    }
}
