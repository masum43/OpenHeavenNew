package com.newandromo.dev18147.app821162.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/*
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.privacy.ConsentDialogListener;
import com.mopub.common.privacy.ConsentStatusChangeListener;
import com.mopub.common.privacy.PersonalInfoManager;
import com.mopub.mobileads.GooglePlayServicesAdapterConfiguration;
import com.mopub.mobileads.MoPubErrorCode;
*/

import org.jetbrains.annotations.NotNull;

import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import timber.log.Timber;

public class BaseActivity extends AppCompatActivity {
    @Nullable
  //  PersonalInfoManager mPersonalInfoManager;

    @Override
    protected void onStart() {
        super.onStart();
        try {
            Context context = getApplicationContext();
            RemoteConfig.initiateRemoteConfig(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int remoteVersionCode = (int) RemoteConfig.getAppVersionCode();
        int versionCode = BuildConfig.VERSION_CODE;

        if (BuildConfig.DEBUG)
            Timber.d("remoteVersionCode= %s, versionCode= %s", remoteVersionCode, versionCode);

        if (remoteVersionCode > versionCode) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.dialog_new_version_title));
            builder.setMessage(getString(R.string.dialog_new_version_message));
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> finish());
            builder.setPositiveButton(getString(R.string.dialog_new_version_download_button), (dialog, which) ->
                    AppUtils.openExternalBrowser(BaseActivity.this, RemoteConfig.getAppRepositoryUrl()));
            builder.setCancelable(false);
            builder.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        /*try {
            if (!MoPub.isSdkInitialized()) {
                HashMap<String, String> adMobConfig = new HashMap<>();
                adMobConfig.put("npa", "1");

                SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(RemoteConfig.getMoPubNativeIdEntryList())
                        .withMediatedNetworkConfiguration(GooglePlayServicesAdapterConfiguration.class.getName(), adMobConfig)
                        .build();

                MoPub.initializeSdk(this, sdkConfiguration, initSdkListener());

                mPersonalInfoManager = MoPub.getPersonalInformationManager();
                if (mPersonalInfoManager != null) {
                    mPersonalInfoManager.subscribeConsentStatusChangeListener(initConsentChangeListener());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_settings:
                AppUtils.openSettingsActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.cancelNotification(this);
    }

    /*private SdkInitializationListener initSdkListener() {
        return () -> {
            if (BuildConfig.DEBUG) Timber.d("MoPub SDK initialized.");
            if (mPersonalInfoManager != null && mPersonalInfoManager.shouldShowConsentDialog()) {
                mPersonalInfoManager.loadConsentDialog(initDialogLoadListener());
            }
        };
    }

    private ConsentStatusChangeListener initConsentChangeListener() {
        return (oldConsentStatus, newConsentStatus, canCollectPersonalInformation) -> {
            if (BuildConfig.DEBUG) {
                Timber.d("oldConsentStatus: %s \nnewConsentStatus: %s",
                        oldConsentStatus.name(), newConsentStatus.name());
            }
            if (mPersonalInfoManager != null && mPersonalInfoManager.shouldShowConsentDialog()) {
                mPersonalInfoManager.loadConsentDialog(initDialogLoadListener());
            }
        };
    }

    private ConsentDialogListener initDialogLoadListener() {
        return new ConsentDialogListener() {

            @Override
            public void onConsentDialogLoaded() {
                if (mPersonalInfoManager != null) {
                    mPersonalInfoManager.showConsentDialog();
                }
            }

            @Override
            public void onConsentDialogLoadFailed(@NonNull MoPubErrorCode moPubErrorCode) {
                if (BuildConfig.DEBUG) {
                    Timber.e("onConsentDialogLoadFailed() Consent dialog failed to load. errorCode=%s", moPubErrorCode);
                }
            }
        };
    }*/
}
