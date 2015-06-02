package contactpicker;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.support.v4.app.FragmentActivity;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.dallinc.masstext.R;
import com.gc.materialdesign.views.ButtonIcon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by dallin on 2/7/15.
 */
public final class ContactManager extends FragmentActivity {

    private static ArrayList<Contact> contacts = null;
    private static ArrayList<ContactGroup> groups = null;
    private LinkedHashMap<String, Contact> allContacts = new LinkedHashMap<String, Contact>();
    private ContactAdapter contactAdapter = null;
    private GroupAdapter groupAdapter = null;
    private ListView contactLV, groupLV;
    private ViewSwitcher viewSwitcher;
    Animation a1, a2;
    CheckBox check_all;
    ActionMenuItemView btnDone, btnToggle;
    RelativeLayout progressLayout;
    LinearLayout searchLayout;
    EditText myFilter;

    // Indexing fo the list
    HashMap<String, Integer> alphaIndexer;
    String[] sections;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_manager);

        ///////////// Custom progress Layout //////////////////////
        progressLayout = (RelativeLayout) findViewById(R.id.progress_layout);
        progressLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        progressLayout.setVisibility(View.GONE); // by default progress view to GONE

        // Hide the search bar until everything has loaded
        searchLayout = (LinearLayout) findViewById(R.id.search_txt_layout);
        searchLayout.setVisibility(View.INVISIBLE);

        // Init UI elements
        contactLV = (ListView) findViewById(R.id.contactList);
        groupLV = (ListView) findViewById(R.id.groupList);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        myFilter = (EditText) findViewById(R.id.search_txt);

        a1 = AnimationUtils.loadAnimation(this, R.anim.from_middle);
        a2 = AnimationUtils.loadAnimation(this, R.anim.to_middle);
        viewSwitcher.setInAnimation(a1);
        viewSwitcher.setOutAnimation(a2);

        // Add text listener to the edit text for filtering the List
        myFilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // call the filter with the current text on the editbox
                contactAdapter.getFilter().filter(s.toString());
            }
        });

        ButtonIcon clearSearchBtn = (ButtonIcon) findViewById(R.id.clear_srch_button);
        clearSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSearch();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(R.id.viewSwitcher).getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.contact_picker_toolbar, root, false);
        bar.inflateMenu(R.menu.menu_contact_picker);
        root.addView(bar, 0); // insert at top
        CheckBox selectAllCheckbox = (CheckBox) root.findViewById(R.id.action_select_all);
        selectAllCheckbox.setButtonDrawable(getResources().getDrawable(R.drawable.select_all_checkbox));
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.action_save:
                        clearSearch();
                        setSelectedContacts();
                        return true;
                    case R.id.action_toggle_views:
                        viewSwitcher.showNext();
                        return true;
                    case android.R.id.home:
                        clearSearch();
                        Intent intent = new Intent();
                        setResult(RESULT_CANCELED, intent);
                        finish();
                        return true;
                }
                return false;
            }
        });

        check_all = (CheckBox) findViewById(R.id.action_select_all);
        check_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    selectAll();
                ContactAdapter aa = (ContactAdapter) contactLV.getAdapter();
                aa.notifyDataSetChanged();
                GroupAdapter gaa = (GroupAdapter) groupLV.getAdapter();
                gaa.notifyDataSetChanged();
            }
        });
        check_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSearch();
                if (!check_all.isChecked())
                    deselectAll();
            }
        });
        btnDone = (ActionMenuItemView) findViewById(R.id.action_save);
        btnToggle = (ActionMenuItemView) findViewById(R.id.action_toggle_views);

        // disable the actionbar buttons and checkbox until the contacts have loaded
        btnDone.setEnabled(false);
        btnDone.setVisibility(View.INVISIBLE);
        check_all.setClickable(false);
        check_all.setVisibility(View.INVISIBLE);
        btnToggle.setEnabled(false);
        btnToggle.setVisibility(View.INVISIBLE);

        if (contacts == null) {
            contacts = new ArrayList<Contact>();
            groups = new ArrayList<ContactGroup>();
            // Asynchronously load all contacts
            new AsyncLoadContacts().execute();
        } else {
            contactAdapter = new ContactAdapter(this, R.id.contactList, contacts);
            contactLV.setAdapter(contactAdapter);
            groupAdapter = new GroupAdapter(this, R.id.groupList, groups);
            groupLV.setAdapter(groupAdapter);
            btnDone.setEnabled(true);
            btnDone.setVisibility(View.VISIBLE);
            check_all.setClickable(true);
            if (allSelected())
                check_all.setChecked(true);
            check_all.setVisibility(View.VISIBLE);
            btnToggle.setEnabled(true);
            btnToggle.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.VISIBLE);
        }
    }

    public boolean allSelected() {
        for (int i=0; i<contacts.size(); i++) {
            if (!contacts.get(i).isSelected())
                return false;
        }
        return true;
    }

    public void clearSearch() {
        myFilter.setText("");
    }

    public void selectAll() {
        for(int i=0; i<contacts.size(); i++)
            contacts.get(i).setSelected(true);
        for(int i=0; i<groups.size(); i++)
            groups.get(i).setSelected(true);
    }

    public void deselectAll() {
        for(int i=0; i<contacts.size(); i++)
            contacts.get(i).setSelected(false);
        for(int i=0; i<groups.size(); i++)
            groups.get(i).setSelected(false);
    }

    // set selected contacts on DONE button press
    private void setSelectedContacts() {

        ArrayList<Contact> selectedList = new ArrayList<Contact>();

        Intent intent = new Intent();

        ArrayList<Contact> contactList = contactAdapter.originalList;
        for (int i = 0; i < contactList.size(); i++) {
            Contact contact = contactList.get(i);
            if (contact.isSelected()) {
                selectedList.add(contact);
            }
            if (selectedList.size() > 0) {
//              intent.putParcelableArrayListExtra("SELECTED_CONTACTS", selectedList);
                intent.putParcelableArrayListExtra(Contact.CONTACTS_DATA, selectedList);
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED, intent);
            }
        }
        // Tip: Here you can finish this activity and on the Activty result of the calling activity you fetch the Selected contacts
        finish();
    }

    // Also on back pressed set the selected list, if nothing selected set Intent result to canceled
    @Override
    public void onBackPressed() {
        myFilter.setText("");

        ArrayList<Contact> selectedList = new ArrayList<Contact>();

        Intent intent = new Intent();

        ArrayList<Contact> contactList = contactAdapter.originalList;
        for (int i = 0; i < contactList.size(); i++) {
            Contact contact = contactList.get(i);
            if (contact.isSelected()) {
                selectedList.add(contact);
            }
            if (selectedList.size() > 0) {
//              intent.putParcelableArrayListExtra("SELECTED_CONTACTS", selectedList);
                intent.putParcelableArrayListExtra(Contact.CONTACTS_DATA, selectedList);

                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED, intent);
            }
        }

        finish();

    };

    @SuppressLint("InlinedApi")
    private void getContactsNewApi() {

        ContentResolver cr = getContentResolver();
        String selection = Data.HAS_PHONE_NUMBER + " = '1'";

        Cursor cur = cr.query(Data.CONTENT_URI, new String[] { Data.CONTACT_ID, Data.MIMETYPE, Email.ADDRESS,
                Contacts.DISPLAY_NAME, Phone.NUMBER }, selection, null, Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");

        Cursor groupCursor = cr.query(
                ContactsContract.Groups.CONTENT_URI,
                new String[]{
                        ContactsContract.Groups._ID,
                        ContactsContract.Groups.TITLE
                }, null, null, ContactsContract.Groups.TITLE + " COLLATE LOCALIZED ASC"
        );

        if (groupCursor.getCount() > 0) {
            while (groupCursor.moveToNext()) {
                String group_id = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.Groups._ID));
                String group_name = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.Groups.TITLE));
                groups.add(new ContactGroup(group_id, group_name));
            }
        }

        groupCursor.close();

        Contact contact;
        if (cur.getCount() > 0) {

            while (cur.moveToNext()) {

                String id = cur.getString(cur.getColumnIndex(Data.CONTACT_ID));

                String mimeType = cur.getString(cur.getColumnIndex(Data.MIMETYPE));

                if (allContacts.containsKey(id)) {
                    // update contact
                    contact = allContacts.get(id);
                } else {
                    contact = new Contact();
                    allContacts.put(id, contact);
                    // set photoUri
                    contact.setContactPhotoUri(getContactPhotoUri(Long.parseLong(id)));
                }

                String groupRowId = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID));
                if(groupRowId != null && mimeType.equals("vnd.android.cursor.item/group_membership")) {
                    ContactGroup cg = getGroup(groupRowId);
                    contact.addGroup(cg);
                    int count = 1111;
                    while (allContacts.get(id + count) != null) { allContacts.get(id + count).addGroup(cg); count++; }
                }

                if (mimeType.equals(StructuredName.CONTENT_ITEM_TYPE)) {
                    // set name
                    String name = cur.getString(cur.getColumnIndex(Contacts.DISPLAY_NAME));
                    contact.setContactName(name);
                    int count = 1111;
                    while (allContacts.get(id + count) != null) { allContacts.get(id + count).setContactName(name); count++; }
                }

                if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)) {
                    // set phone number
                    String before = contact.getContactNumber();
                    String after = cur.getString(cur.getColumnIndex(Phone.NUMBER));
                    contact.setContactNumber(after);
                    contact.setNumberLabel(getPhoneLabel(Long.parseLong(id), after));
                    if (before != null) {
                        Contact other = new Contact();
                        other.setSelected(contact.selected);
                        other.setContactEmail(contact.getContactEmail());
                        other.setContactName(contact.getContactName());
                        other.setContactNumber(before);
                        other.setNumberLabel(getPhoneLabel(Long.parseLong(id), before));
                        other.setContactPhoto(contact.getContactPhoto());
                        other.setContactPhotoUri(contact.getContactPhotoUri());
                        int count = 1111;
                        while (allContacts.get(id + count) != null) { count++; }
                        allContacts.put(id + count, other);
                    }
                }

            }
        }

        cur.close();
        // get contacts from hashmap
        contacts.clear();
        contacts.addAll(allContacts.values());

        // remove self contact
        for (Contact _contact : contacts) {

            if (_contact.getContactName() == null && _contact.getContactNumber() == null
                    && _contact.getContactEmail() == null) {
                contacts.remove(_contact);
                break;
            }
        }

        // remove empty groups
        ArrayList<ContactGroup> toRemove = new ArrayList<ContactGroup>();
        for(ContactGroup cg: groups) {
            if(numContactsInGroup(cg) == 0)
                toRemove.add(cg);
        }
        for(ContactGroup cg: toRemove) {
            groups.remove(cg);
        }

        contactAdapter = new ContactAdapter(this, R.id.contactList, contacts);
        contactAdapter.notifyDataSetChanged();
        groupAdapter = new GroupAdapter(this, R.id.groupList, groups);
        groupAdapter.notifyDataSetChanged();

    }

    private String getPhoneLabel(long contactId, String number) {
        ContentResolver cr = getContentResolver();
        Cursor phone_crsr = cr.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + contactId, null, null);

        while (phone_crsr.moveToNext()) {
            String phone_number = phone_crsr.getString(phone_crsr.getColumnIndex(Phone.DATA));
            if(!phone_number.equals(number)) {
                continue;
            }
            int phone_type = phone_crsr.getInt(phone_crsr.getColumnIndex(Phone.TYPE));
            switch (phone_type) {
                case Phone.TYPE_HOME:
                    phone_crsr.close();
                    return "Home";
                case Phone.TYPE_MOBILE:
                    phone_crsr.close();
                    return "Mobile";
                case Phone.TYPE_WORK:
                    phone_crsr.close();
                    return "Work";
                case Phone.TYPE_FAX_WORK:
                    phone_crsr.close();
                    return "Fax Work";
                case Phone.TYPE_FAX_HOME:
                    phone_crsr.close();
                    return "Fax Home";
                case Phone.TYPE_PAGER:
                    phone_crsr.close();
                    return "Pager";
                case Phone.TYPE_OTHER:
                    phone_crsr.close();
                    return "Other";
                case Phone.TYPE_CALLBACK:
                    phone_crsr.close();
                    return "Callback";
                case Phone.TYPE_CAR:
                    phone_crsr.close();
                    return "Car";
                case Phone.TYPE_CUSTOM:
                    String label = phone_crsr.getString(phone_crsr.getColumnIndex(Phone.LABEL));
                    phone_crsr.close();
                    return label;
                default:
                    phone_crsr.close();
                    return "Unknown type";
            }
        }
        phone_crsr.close();
        return "error";
    }

    // Get contact photo URI for contactId
    public Uri getContactPhotoUri(long contactId) {
        Uri photoUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        photoUri = Uri.withAppendedPath(photoUri, Contacts.Photo.CONTENT_DIRECTORY);
        return photoUri;
    }

    private class AsyncLoadContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Obtain contacts
            getContactsNewApi();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // set contact adapter
            contactLV.setAdapter(contactAdapter);
            // set group adapter
            groupLV.setAdapter(groupAdapter);

            // set the progress to GONE
            progressLayout.setVisibility(View.GONE);

            // enable the actionbar buttons and checkbox
            btnDone.setEnabled(true);
            btnDone.setVisibility(View.VISIBLE);
            check_all.setClickable(true);
            check_all.setVisibility(View.VISIBLE);
            btnToggle.setEnabled(true);
            btnToggle.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.VISIBLE);
        }

    }

    // Contact adapter
    public class ContactAdapter extends ArrayAdapter<Contact> implements SectionIndexer {

        private ArrayList<Contact> contactList;
        private ArrayList<Contact> originalList;
        private ContactFilter filter;

        public ContactAdapter(Context context, int textViewResourceId, ArrayList<Contact> items) {
            super(context, textViewResourceId, items);

            this.contactList = new ArrayList<Contact>();
            this.originalList = new ArrayList<Contact>();

            this.contactList.addAll(items);
            this.originalList.addAll(items);

            // indexing
            alphaIndexer = new HashMap<String, Integer>();
            int size = contactList.size();

            for (int x = 0; x < size; x++) {
                String s = contactList.get(x).getContactName();

                if(s!=null && !TextUtils.isEmpty(s))
                {
                    // get the first letter of the store
                    String ch = s.substring(0, 1);
                    // convert to uppercase otherwise lowercase a -z will be sorted
                    // after upper A-Z
                    ch = ch.toUpperCase();

                    // prevent duplicates
                    if(!alphaIndexer.containsKey(ch))
                        alphaIndexer.put(ch, x);
                }
            }

            Set<String> sectionLetters = alphaIndexer.keySet();

            // create a list from the set to sort
            ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);

            Collections.sort(sectionList);

            sections = new String[sectionList.size()];

            sectionList.toArray(sections);
        }

        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new ContactFilter();
            }
            return filter;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.contact_item, null);
            }
            final Contact contact = contactList.get(position);
            if (contact != null) {
                TextView name = (TextView) view.findViewById(R.id.name);
                ImageView thumb = (ImageView) view.findViewById(R.id.thumb);
                TextView number = (TextView) view.findViewById(R.id.number);
                TextView numberLabel = (TextView) view.findViewById(R.id.numberLabel);

                thumb.setImageURI(contact.getContactPhotoUri());

                if (thumb.getDrawable() == null) {
                    thumb.setBackgroundResource(R.drawable.ic_action_person);
                    thumb.setImageResource(R.drawable.image_border);
                }

                final CheckBox nameCheckBox = (CheckBox) view.findViewById(R.id.checkBox);

                name.setText(contact.getContactName());

                // set number label
                if (contact.getNumberLabel() == null)
                    numberLabel.setText("");
                else
                    numberLabel.setText(contact.getNumberLabel() + ": ");

                number.setText(contact.getContactNumber());

                nameCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        contact.setSelected(nameCheckBox.isChecked());

                        if (!isChecked)
                            check_all.setChecked(false);

                        for (ContactGroup cg : groups)
                            cg.selected = allContactsInGroupAreSelected(cg);

                        GroupAdapter gaa = (GroupAdapter) groupLV.getAdapter();
                        gaa.notifyDataSetChanged();
                    }
                });

                nameCheckBox.setChecked(check_all.isChecked() || contact.isSelected());
            }

            return view;
        }

        @Override
        public int getPositionForSection(int section) {
            if(section > sections.length - 1) {
                section = sections.length - 1;
            }
            return alphaIndexer.get(sections[section]);
        }

        @Override
        public int getSectionForPosition(int position) {
            for(int i=0; i<sections.length; i++){
                if(alphaIndexer.get(sections[i]) > position)
                    return i;
            }
            return 0;
        }

        @Override
        public Object[] getSections() {
            return sections;
        }

        // Contacts filter
        private class ContactFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                if (constraint != null && constraint.toString().length() > 0) {
                    ArrayList<Contact> filteredItems = new ArrayList<Contact>();

                    for (int i = 0, l = originalList.size(); i < l; i++) {
                        Contact contact = originalList.get(i);
                        if (contact.toString().toLowerCase().contains(constraint))
                            filteredItems.add(contact);
                    }
                    result.count = filteredItems.size();
                    result.values = filteredItems;
                } else {
                    synchronized (this) {
                        result.values = originalList;
                        result.count = originalList.size();
                    }
                }
                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                contactList = (ArrayList<Contact>) results.values;
                notifyDataSetChanged();
                clear();
                for (int i = 0, l = contactList.size(); i < l; i++)
                    add(contactList.get(i));
                notifyDataSetInvalidated();
            }
        }

    }

    // Group adapter
    public class GroupAdapter extends ArrayAdapter<ContactGroup> {

        private ArrayList<ContactGroup> groupList;

        public GroupAdapter(Context context, int textViewResourceId, ArrayList<ContactGroup> items) {
            super(context, textViewResourceId, items);

            this.groupList = new ArrayList<ContactGroup>();
            this.groupList.addAll(items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.contact_item, null);
            }
            final ContactGroup group = groupList.get(position);
            if (group != null) {
                TextView name = (TextView) view.findViewById(R.id.name);
                ImageView thumb = (ImageView) view.findViewById(R.id.thumb);
                TextView number = (TextView) view.findViewById(R.id.number);
                TextView numberLabel = (TextView) view.findViewById(R.id.numberLabel);
                thumb.setBackgroundResource(R.drawable.ic_action_group);
                final CheckBox groupCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
                name.setText(group.getGroupName());
                number.setText("" + numContactsInGroup(group));
                numberLabel.setText("# of contacts:");

                groupCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        assert (groupCheckBox.isChecked() == isChecked);
                        group.setSelected(isChecked);

                        if (!isChecked)
                            check_all.setChecked(false);

                        ContactAdapter aa = (ContactAdapter) contactLV.getAdapter();
                        aa.notifyDataSetChanged();
                        GroupAdapter gaa = (GroupAdapter) groupLV.getAdapter();
                        gaa.notifyDataSetChanged();
                    }
                });

                groupCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectAllContactsInGroup(group, groupCheckBox.isChecked());

                        for(ContactGroup cg: groups) {
                            if(cg.getGroupId() == group.getGroupId())
                                continue;
                            cg.selected = allContactsInGroupAreSelected(cg);
                        }

                        ContactAdapter aa = (ContactAdapter) contactLV.getAdapter();
                        aa.notifyDataSetChanged();
                        GroupAdapter gaa = (GroupAdapter) groupLV.getAdapter();
                        gaa.notifyDataSetChanged();
                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        groupCheckBox.setChecked(!groupCheckBox.isChecked());
                        selectAllContactsInGroup(group, groupCheckBox.isChecked());

                        for(ContactGroup cg: groups) {
                            if(cg.getGroupId() == group.getGroupId())
                                continue;
                            cg.selected = allContactsInGroupAreSelected(cg);
                        }

                        ContactAdapter aa = (ContactAdapter) contactLV.getAdapter();
                        aa.notifyDataSetChanged();
                        GroupAdapter gaa = (GroupAdapter) groupLV.getAdapter();
                        gaa.notifyDataSetChanged();
                    }
                });

                groupCheckBox.setChecked(check_all.isChecked() || group.isSelected());
            }

            return view;
        }

    }

    public void selectAllContactsInGroup(ContactGroup group, boolean value) {
        for(Contact c: contacts)
            if(c.isInGroup(group))
                c.setSelected(value);
    }

    public boolean allContactsInGroupAreSelected(ContactGroup group) {
        for(Contact c: contacts)
            if(c.isInGroup(group) && !c.isSelected())
                return false;
        return true;
    }

    public int numContactsInGroup(ContactGroup group) {
        int count = 0;
        for(Contact c: contacts)
            if(c.isInGroup(group))
                count++;
        return count;
    }

    public void toggleCheckbox(View view) {
        final CheckBox nameCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
        nameCheckBox.setChecked(!nameCheckBox.isChecked());
    }

    public ContactGroup getGroup(String id) {
        for(ContactGroup cg: groups) {
            if(cg.getGroupId().equals(id))
                return cg;
        }
        return new ContactGroup("unknown id", "unknown group name");
    }

}
