package com.newandromo.dev18147.app821162.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.ui.fragments.FeedbackDialogFragment;
import com.newandromo.dev18147.app821162.ui.fragments.LegalDocsFragment;
import com.newandromo.dev18147.app821162.utils.AppUtils;

import static com.newandromo.dev18147.app821162.utils.Constants.AppUrls.APP_MARKET_URL_GOOGLE;
import static com.newandromo.dev18147.app821162.utils.Constants.AppUrls.MAIL_TO;
import static com.newandromo.dev18147.app821162.utils.Constants.AppUrls.TEL;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppUtils.setAppTheme(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        TextView appVersion = findViewById(R.id.app_version);
        appVersion.setText(BuildConfig.VERSION_NAME);

        TextView developerName = findViewById(R.id.developer_name);
        developerName.setText(RemoteConfig.getDeveloperName());
        developerName.setVisibility(TextUtils.isEmpty(RemoteConfig.getDeveloperName()) ? View.GONE : View.VISIBLE);

        LinearLayout devPhone = findViewById(R.id.developer_phone);
        LinearLayout devEmail = findViewById(R.id.developer_email);
        LinearLayout devWebsite = findViewById(R.id.developer_website);

        devPhone.setVisibility(TextUtils.isEmpty(RemoteConfig.getDeveloperPhone()) ? View.GONE : View.VISIBLE);
        devEmail.setVisibility(TextUtils.isEmpty(RemoteConfig.getDeveloperEmail()) ? View.GONE : View.VISIBLE);
        devWebsite.setVisibility(TextUtils.isEmpty(RemoteConfig.getDeveloperWebsite()) ? View.GONE : View.VISIBLE);
    }

    public void onAboutItemClicked(View v) {
        switch (v.getId()) {
            case R.id.share_app:
                Bundle shareBundle = AppUtils.createShareBundle("",
                        RemoteConfig.getAppStoreUrl(),
                        "", "", "");
                AppUtils.appShare(this, getSupportFragmentManager(), shareBundle);
                break;
            case R.id.rate_app:
                openAppStore();
                break;
            case R.id.contact_developer:
                FeedbackDialogFragment dialog = new FeedbackDialogFragment();
                dialog.show(getSupportFragmentManager(), "feedback");
                break;
            case R.id.privacy_policy:
                try {
                    LegalDocsFragment legalDocsFragment = LegalDocsFragment.newInstance(true);
                    legalDocsFragment.show(getSupportFragmentManager(), "legalDocsFragment");
                } catch (Exception ignore) {
                }
                break;
            case R.id.developer_phone:
                String[] CLIENT_PHONES = TextUtils.split(RemoteConfig.getDeveloperPhone(), ";");
                AlertDialog.Builder builderPhone = new AlertDialog.Builder(AboutActivity.this);
                builderPhone.setTitle(getString(R.string.nav_drawer_mobile));
                builderPhone.setItems(CLIENT_PHONES, (dialog1, which) -> {
                    String uriString = String.format(TEL, CLIENT_PHONES[which]
                            .replace(" - TTCL", "")
                            .replace(" - Airtel", "")
                            .replace(" - Halotel", "")
                            .replace(" - Tigo", "")
                            .replace(" - WhatsApp", ""));
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(uriString)));
                });
                builderPhone.show();
                break;
            case R.id.developer_email:
                String[] CLIENT_EMAILS = TextUtils.split(RemoteConfig.getDeveloperEmail(), ";");
                AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
                builder.setTitle(getString(R.string.nav_drawer_email));
                builder.setItems(CLIENT_EMAILS, (dialog12, which) -> {
                    String uriString = String.format(MAIL_TO, CLIENT_EMAILS[which]);
                    startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(uriString)));
                });
                builder.show();
                break;
            case R.id.developer_website:
                AppUtils.openExternalBrowser(this, RemoteConfig.getDeveloperWebsite());
                break;
            default:
                break;
        }
    }

    private void openAppStore() {
        try {
            String uriString = String.format(APP_MARKET_URL_GOOGLE, getPackageName());
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriString)));
        } catch (Exception ignore) {
        }
    }
}
