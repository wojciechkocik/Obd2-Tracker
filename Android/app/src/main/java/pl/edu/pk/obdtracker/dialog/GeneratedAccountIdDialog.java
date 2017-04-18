package pl.edu.pk.obdtracker.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import lombok.extern.slf4j.Slf4j;
import pl.edu.pk.obdtracker.Config;

/**
 * Created by Wojciech on 18.04.2017.
 */
@Slf4j
public class GeneratedAccountIdDialog extends DialogFragment {

    private String accountId;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final TextView textView = new TextView(getActivity());
        final SpannableString spannableMsg = new SpannableString("See your real time car data at page " + Config.WWW_APP_URL + "/pages/index.html?account=TODO");

        Linkify.addLinks(spannableMsg, Linkify.WEB_URLS);
        textView.setText(spannableMsg);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setPadding(20,20,20,20);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Your Account")
                .setView(textView)
                .setPositiveButton(android.R.string.ok, null);

        return builder.create();
    }
}
