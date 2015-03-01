package com.dallinc.masstexter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dallinc.masstexter.helpers.Constants;
import com.dallinc.masstexter.messaging.Compose;
import com.dallinc.masstexter.messaging.SentMessageDetails;
import com.dallinc.masstexter.models.GroupMessage;
import com.dallinc.masstexter.models.SingleMessage;
import com.dallinc.masstexter.models.Template;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.List;

/**
 * Created by dallin on 1/30/15.
 */
public class MessagingFragment extends Fragment {
    private BroadcastReceiver receiver;
    GroupMessageAdapter ca;

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
        final RelativeLayout messageVeil = (RelativeLayout) rootView.findViewById(R.id.messageVeil);
        final FloatingActionsMenu composeButton = (FloatingActionsMenu) rootView.findViewById(R.id.buttonComposeMessage);

        rootView.setFocusableInTouchMode(true);
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK && composeButton.isExpanded())
                {
                    composeButton.collapse();
                    return true;
                }
                return false;
            }
        });

        messageVeil.setVisibility(View.INVISIBLE);
        messageVeil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeButton.collapse();
            }
        });
        composeButton.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                messageVeil.setAlpha(0f);
                messageVeil.setVisibility(View.VISIBLE);
                messageVeil.animate().alpha(1f).setDuration(300).setListener(null);
            }

            @Override
            public void onMenuCollapsed() {
                messageVeil.animate().alpha(0f).setDuration(500).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        messageVeil.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

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

        final RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.sentMessagesCardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        ca = new GroupMessageAdapter();
        recList.setAdapter(ca);
        recList.smoothScrollToPosition(ca.getItemCount() - 1);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ca.addMessage(intent.getLongExtra(Constants.EXTRA_MESSAGE_ID, -1));
                recList.smoothScrollToPosition(ca.getItemCount() - 1);
            }
        };
        LocalBroadcastManager.getInstance(getActivity().getBaseContext()).registerReceiver((receiver), new IntentFilter(Constants.BROADCAST_SENT_GROUP_MESSAGE));

        return rootView;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity().getBaseContext()).unregisterReceiver(receiver);
        super.onDestroy();
    }

    public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.GroupMessageViewHolder> {

        private List<GroupMessage> objects;

        public GroupMessageAdapter() {
            this.objects = GroupMessage.listAll(GroupMessage.class);
        }

        @Override
        public int getItemCount() {
            return objects.size();
        }

        @Override
        public void onBindViewHolder(final GroupMessageViewHolder GroupMessageViewHolder, int i) {
            final GroupMessage sentMessage = objects.get(i);
            GroupMessageViewHolder.vTitle.setText(sentMessage.sentAt);
            String body = sentMessage.messageBody;
            sentMessage.buildArrayListFromString();
            for(String variable: sentMessage.variables) {
                body = body.replaceFirst("¬", variable);
            }
            GroupMessageViewHolder.vBody.setText(body);
            GroupMessageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GroupMessageViewHolder.itemView.getContext(), SentMessageDetails.class);
                    intent.putExtra("message_id", sentMessage.getId());
                    startActivity(intent);
                }
            });
            GroupMessageViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(GroupMessageViewHolder.itemView.getContext());
                    builder.setTitle("Delete Message?");
                    builder.setMessage("Do you want to delete this message from the list?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sentMessage.delete();
                            objects = GroupMessage.listAll(GroupMessage.class);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                    return false;
                }
            });
            Long totalRecipients = SingleMessage.count(SingleMessage.class, "group_message = ?", new String[]{Long.toString(sentMessage.getId())});
            GroupMessageViewHolder.vRecipientCount.setText(Long.toString(totalRecipients));
        }

        @Override
        public GroupMessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.template_card_layout, viewGroup, false);
            return new GroupMessageViewHolder(itemView);
        }

        public class GroupMessageViewHolder extends RecyclerView.ViewHolder {
            protected TextView vTitle;
            protected TextView vBody;
            protected TextView vRecipientCount;

            public GroupMessageViewHolder(View v) {
                super(v);
                vTitle =  (TextView) v.findViewById(R.id.templateCardTitle);
                vBody = (TextView)  v.findViewById(R.id.templateCardBody);
                vRecipientCount = (TextView)  v.findViewById(R.id.recipientCount);
            }
        }

        public void addMessage(long id) {
            GroupMessage newMessage = GroupMessage.findById(GroupMessage.class, id);
            objects.add(newMessage);
            notifyDataSetChanged();
        }
    }
}
