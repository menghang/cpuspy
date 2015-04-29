//-----------------------------------------------------------------------------
//
// (C) Rob Beane, 2015 <robbeane@gmail.com>
//
//-----------------------------------------------------------------------------

package org.axdev.cpuspy.fragments;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.axdev.cpuspy.R;
import org.axdev.cpuspy.utils.ThemeUtils;
import org.axdev.cpuspy.utils.TypefaceHelper;
import org.axdev.cpuspy.utils.TypefaceSpan;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LicenseFragment extends Fragment {

    @InjectView(R.id.card_view_supportlib) CardView mCardViewSupport;
    @InjectView(R.id.card_view_butterknife) CardView mCardViewButter;
    @InjectView(R.id.card_view_materialdialog) CardView mCardViewDialog;
    @InjectView(R.id.card_view_materialripple) CardView mCardViewRipple;
    @InjectView(R.id.card_view_snackbar) CardView mCardViewSnackbar;
    @InjectView(R.id.supportlib) TextView mSupportLib;
    @InjectView(R.id.materialdialog) TextView mMaterialDialog;
    @InjectView(R.id.snackbar) TextView mSnackbar;
    @InjectView(R.id.materialripple) TextView mMaterialRipple;
    @InjectView(R.id.butterknife) TextView mButterKnife;

    /** Inflate the license layout */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.license_layout, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setTypeface();
        this.setThemeAttributes();

        final ActionBar supportActionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (supportActionBar != null) { supportActionBar.setDisplayHomeAsUpEnabled(true); }

        // Use custom Typeface for action bar title on KitKat devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (supportActionBar != null) {
                supportActionBar.setTitle(R.string.pref_title_license);
            }
        } else {
            final SpannableString s = new SpannableString(getResources().getString(R.string.pref_title_license));
            s.setSpan(new TypefaceSpan(getActivity(), "Roboto-Medium.ttf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Update the action bar title with the TypefaceSpan instance
            if (supportActionBar != null) {
                supportActionBar.setTitle(s);
            }
        }
    }

    /** Set typeface and allow hyperlinks */
    private void setTypeface() {
        final Typeface mediumFont = TypefaceHelper.get(getActivity(), "Roboto-Medium");

        mSupportLib.setTypeface(mediumFont);
        mSupportLib.setMovementMethod(LinkMovementMethod.getInstance());
        mSupportLib.setText(Html.fromHtml(getResources().getString(R.string.pref_license_supportlib)));

        mMaterialDialog.setTypeface(mediumFont);
        mMaterialDialog.setMovementMethod(LinkMovementMethod.getInstance());
        mMaterialDialog.setText(Html.fromHtml(getResources().getString(R.string.pref_license_materialdialog)));

        mSnackbar.setTypeface(mediumFont);
        mSnackbar.setMovementMethod(LinkMovementMethod.getInstance());
        mSnackbar.setText(Html.fromHtml(getResources().getString(R.string.pref_license_snackbar)));

        mMaterialRipple.setTypeface(mediumFont);
        mMaterialRipple.setMovementMethod(LinkMovementMethod.getInstance());
        mMaterialRipple.setText(Html.fromHtml(getResources().getString(R.string.pref_license_materialripple)));

        mButterKnife.setTypeface(mediumFont);
        mButterKnife.setMovementMethod(LinkMovementMethod.getInstance());
        mButterKnife.setText(Html.fromHtml(getResources().getString(R.string.pref_license_butterknife)));
    }

    /** Set UI elements for dark and light themes */
    private void setThemeAttributes() {
        mCardViewSupport.setCardBackgroundColor(getResources().getColor(ThemeUtils.DARKTHEME ?
                R.color.card_dark_background : R.color.card_light_background));
        mCardViewButter.setCardBackgroundColor(getResources().getColor(ThemeUtils.DARKTHEME ?
                R.color.card_dark_background : R.color.card_light_background));
        mCardViewDialog.setCardBackgroundColor(getResources().getColor(ThemeUtils.DARKTHEME ?
                R.color.card_dark_background : R.color.card_light_background));
        mCardViewRipple.setCardBackgroundColor(getResources().getColor(ThemeUtils.DARKTHEME ?
                R.color.card_dark_background : R.color.card_light_background));
        mCardViewSnackbar.setCardBackgroundColor(getResources().getColor(ThemeUtils.DARKTHEME ?
                R.color.card_dark_background : R.color.card_light_background));
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}