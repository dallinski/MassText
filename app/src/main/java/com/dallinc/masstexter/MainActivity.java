package com.dallinc.masstexter;

import java.util.Locale;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.dallinc.masstexter.helpers.Constants;
import com.dallinc.masstexter.helpers.DialogMaterialFragment;

import net.danlew.android.joda.JodaTimeAndroid;


public class MainActivity extends ActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    static ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            int current_tab = bundle.getInt("opened_tab", Constants.MESSAGING_FRAGMENT_POS);
            mViewPager.setCurrentItem(current_tab);
        }
    }

    public static void switchFragments(int tab_id) {
        mViewPager.setCurrentItem(tab_id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_donate) {
            Intent intent = new Intent(this, Donate.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_show_change_log) {
            openDialogFragment(new DialogMaterialFragment());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openDialogFragment(DialogFragment dialogStandardFragment) {
        if (dialogStandardFragment!=null){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment prev = fm.findFragmentByTag("changelog_dialog");
            if (prev != null) {
                ft.remove(prev);
            }

            dialogStandardFragment.show(ft,"changelog_dialog");
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // instantiate the fragment for the given page.
            switch (position) {
                case Constants.MESSAGING_FRAGMENT_POS:
                    return MessagingFragment.newInstance(position + 1);
                case Constants.TEMPLATES_FRAGMENT_POS:
                    return TemplatesFragment.newInstance(position + 1);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case Constants.MESSAGING_FRAGMENT_POS:
                    return getString(R.string.title_section1).toUpperCase(l);
                case Constants.TEMPLATES_FRAGMENT_POS:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

}
