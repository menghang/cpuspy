package org.axdev.cpuspy.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.color.CircleView;

import org.axdev.cpuspy.R;
import org.axdev.cpuspy.activity.ThemedActivity;
import org.axdev.cpuspy.utils.TypefaceHelper;
import org.axdev.cpuspy.utils.TypefaceSpan;
import org.axdev.cpuspy.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class DeveloperFragment extends Fragment implements AdapterView.OnItemClickListener {

    private int primaryColor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.developer_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Context mContext = getActivity();
        final Resources res = getResources();
        final ActionBar mActionBar = ((AppCompatActivity) mContext).getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setElevation(0);

        /** Use custom Typeface for action bar title on KitKat devices */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActionBar.setTitle(res.getString(R.string.pref_about_developer));
        } else {
            final SpannableString s = new SpannableString(res.getString(R.string.pref_about_developer));
            s.setSpan(new TypefaceSpan(mContext, TypefaceHelper.MEDIUM_FONT), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Update the action bar title with the TypefaceSpan instance
            mActionBar.setTitle(s);
        }

        final CircleImageView imageView = ButterKnife.findById(view, R.id.profile_image);
        final Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.profile, null);
        imageView.setImageDrawable(drawable);
        imageView.setBorderColor(ContextCompat.getColor(mContext, ThemedActivity.mIsDarkTheme ?
                android.R.color.white : android.R.color.black));

        final ThemedActivity act = ((ThemedActivity) mContext);
        final int colorPrimary = act.primaryColor();
        final int colorAccent = act.accentColor();
        primaryColor = colorPrimary == 0 ? ContextCompat.getColor(mContext, R.color.primary) : colorPrimary;
        int accentColor = colorAccent == 0 ? ContextCompat.getColor(mContext, R.color.accent) : colorAccent;

        final View mHeader = ButterKnife.findById(view, R.id.developer_header);
        mHeader.setBackgroundColor(primaryColor);

        final View mDivider = ButterKnife.findById(view, R.id.viewDivider);
        mDivider.setBackgroundColor(act.primaryColorDark());

        final TextView contactTitle = ButterKnife.findById(view, R.id.developer_contact_title);
        final Typeface robotoMedium = TypefaceHelper.mediumTypeface(mContext);
        contactTitle.setTypeface(robotoMedium);
        contactTitle.setTextColor(accentColor);

        final ListView mListView = ButterKnife.findById(getActivity(), R.id.developer_list);
        final List<String[]> developerList = new ArrayList<>();
        developerList.add(new String[]{res.getString(R.string.email_developer), res.getString(R.string.email_developer_summary)});
        developerList.add(new String[]{res.getString(R.string.view_gplus), res.getString(R.string.view_gplus_summary)});
        developerList.add(new String[]{res.getString(R.string.menu_donate), res.getString(R.string.donate_summary)});
        mListView.setAdapter(new ArrayAdapter<String[]>(
                mContext,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                developerList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // Must always return just a View.
                final View view = super.getView(position, convertView, parent);

                // If you look at the android.R.layout.simple_list_item_2 source, you'll see
                // it's a TwoLineListItem with 2 TextViews - mText1 and mText2.
                //TwoLineListItem listItem = (TwoLineListItem) view;
                final String[] entry = developerList.get(position);
                final TextView mText1 = ButterKnife.findById(view, android.R.id.text1);
                final TextView mText2 = ButterKnife.findById(view, android.R.id.text2);
                mText1.setText(entry[0]);
                mText2.setText(entry[1]);
                mText2.setTextColor(ContextCompat.getColor(mContext, ThemedActivity.mIsDarkTheme ?
                        R.color.secondary_text_color_dark : R.color.secondary_text_color_light));
                return view;
            }
        });

        Utils.setDynamicHeight(mListView);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Activity mContext = getActivity();
        switch (position) {
            case 0: // Email Developer
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "robbeane@gmail.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "CPUSpy Material");
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.email_developer)));
                break;
            case 1: // Google Plus
                Utils.openChromeTab(mContext, "https://plus.google.com/+RobBeane", primaryColor);
                break;
            case 2: // Donate
                Utils.openChromeTab(mContext, "https://goo.gl/X2sA4D", primaryColor);
                break;
        }
    }
}
