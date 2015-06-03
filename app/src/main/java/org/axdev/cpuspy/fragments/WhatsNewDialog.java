//-----------------------------------------------------------------------------
//
// (C) Rob Beane, 2015 <robbeane@gmail.com>
//
//-----------------------------------------------------------------------------

package org.axdev.cpuspy.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;

import org.axdev.cpuspy.R;

public class WhatsNewDialog extends DialogFragment {

    private final String githubURL = "https://github.com/existz/cpuspy/commits/staging";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.menu_changelog)
                .customView(R.layout.changelog_layout, true)
                .negativeText(R.string.action_changelog)
                .positiveText(R.string.action_dismiss)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        final Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(githubURL));
                        startActivity(i);
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build();

        // Override dialog enter/exit animation
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        return dialog;
    }
}