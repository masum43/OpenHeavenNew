package com.newandromo.dev18147.app821162.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Locale;

import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.RemoteConfig;
import timber.log.Timber;

public class FeedbackDialogFragment extends DialogFragment {
    private View mView;
    private Spinner mSubject;
    private TextInputLayout mInputLayoutMessage;
    private EditText mMessage;
    private TextView mDeviceDetails;

    public FeedbackDialogFragment() {
    }

    @SuppressLint("InflateParams")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dialog_contact_developer, null);
        mSubject = mView.findViewById(R.id.subject_spinner);
        mInputLayoutMessage = mView.findViewById(R.id.input_layout_massage);
        mMessage = mView.findViewById(R.id.input_message);
        mDeviceDetails = mView.findViewById(R.id.device_details);

        if (getActivity() != null)
            try {
                PackageManager manager = getActivity().getPackageManager();
                PackageInfo packageInfo = manager.getPackageInfo(getActivity().getPackageName(), 0);
                String mDetails = getString(R.string.dialog_item_device_model) + ": " + Build.MODEL + " (" + Build.PRODUCT + ")\n" + // Model
                        getString(R.string.dialog_item_device_manufacturer) + ": " + Build.MANUFACTURER + "\n" + // Manufacturer
                        getString(R.string.dialog_item_device_customization) + ": " + Build.BRAND + "\n" + // Brand/Customization
                        getString(R.string.dialog_item_device_os) + ": v" + Build.VERSION.RELEASE + " (" + Build.VERSION.INCREMENTAL + ")\n" + // OS
                        getString(R.string.dialog_item_app_version) + ": " + packageInfo.versionName;

                mDeviceDetails.setText(mDetails);
            } catch (Exception e) {
                Timber.e("onCreate() error= %s", e.getMessage());
            }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getActivity() == null) return super.onCreateDialog(savedInstanceState);

        mMessage.addTextChangedListener(new MyTextWatcher(mMessage));

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mView);
        builder.setTitle(getString(R.string.nav_drawer_feedback));
        builder.setPositiveButton(R.string.button_send,
                (dialog, which) -> {
                    if (!validateMessage()) {
                        return;
                    }
                    send();
                });
        builder.setNegativeButton(android.R.string.cancel, null);

        return builder.create();
    }

    private boolean validateMessage() {
        if (mMessage.getText().toString().trim().isEmpty()) {
            mInputLayoutMessage.setError(getString(R.string.error_enter_message));
            requestFocus(mMessage);
            return false;
        } else {
            mInputLayoutMessage.setErrorEnabled(false);
        }
        return true;
    }

    private void send() {
        if (getActivity() != null)
            try {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                String[] recipients = new String[]{RemoteConfig.getDeveloperEmail()};
                if (!TextUtils.isEmpty(RemoteConfig.getClientEmail())) {
                    String[] CLIENT_EMAILS = TextUtils.split(RemoteConfig.getClientEmail(), ";");
                    recipients = new String[]{RemoteConfig.getDeveloperEmail(), CLIENT_EMAILS[0]};
                }
                String subject = getString(R.string.app_name) + " app support";
                emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT,
                        "Subject: " + mSubject.getSelectedItem().toString() +
                                "\n\nMessage:\n" + mMessage.getText().toString() + // Enter description
                                "\n\n" + mDeviceDetails.getText().toString());
                emailIntent.setType("plain/text"); // This is an incorrect MIME, but Gmail is one of the only apps that responds to it
                final PackageManager pm = getActivity().getPackageManager();
                final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
                ResolveInfo best = null;
                int count = matches.size();
                for (int i = 0; i < count; i++) {
                    if (matches.get(i).activityInfo.packageName.endsWith(".gm") ||
                            matches.get(i).activityInfo.name.toLowerCase(Locale.ENGLISH).contains("gmail"))
                        best = matches.get(i);
                }
                if (best != null)
                    emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(emailIntent);
            } catch (Exception e) {
                Toast.makeText(getActivity(), getString(R.string.error_failed_gmail_launch), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
    }

    private void requestFocus(View view) {
        if (view.requestFocus() && getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    private class MyTextWatcher implements TextWatcher {
        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            if (view.getId() == R.id.input_message) {
                validateMessage();
            }
        }
    }
}
