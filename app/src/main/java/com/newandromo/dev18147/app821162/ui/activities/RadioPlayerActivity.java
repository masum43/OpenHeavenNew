package com.newandromo.dev18147.app821162.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.ThemedSpinnerAdapter;
import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.y20k.transistor.Station;
import org.y20k.transistor.TransistorKeys;

import java.util.ArrayList;
import java.util.List;

import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.ui.fragments.RadioPlayerFragment;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import timber.log.Timber;

import static com.newandromo.dev18147.app821162.utils.Constants.Const.EMPTY_STRING;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.HTTP;

public class RadioPlayerActivity extends BaseActivity {
    private int mStationId = -1;
    private String[] mStationNames = new String[0];
    private List<Station> mStationList = new ArrayList<>();
    private boolean mGoHome = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Dark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_player);

        mStationList.clear();
        try {
            String radioData = RemoteConfig.getOnlineRadioData();

            /*if (!TextUtils.isEmpty(radioData) && !radioData.contains("\"")) {
                radioData = radioData.replace("radio:", "\"radio\":")
                        .replace("name:", "\"name\":\"")
                        .replace(",url:", "\",\"url\":\"")
                        .replace("},", "\"},")
                        .replace("}]}", "\"}]}");
            }*/

            JSONObject jsonObject = new JSONObject(radioData);
            JSONArray jsonArray = jsonObject.getJSONArray("radio");

            mStationNames = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObj = jsonArray.getJSONObject(i);

                mStationNames[i] = jObj.getString("name");

                String stationName = jObj.getString("name");
                String streamUrl = jObj.getString("streamUrl");
                String stationSite = jObj.getString("stationSite");
                String stationImage = jObj.getString("stationImage");

                mStationList.add(new Station(streamUrl, stationName, stationSite, stationImage));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (savedInstanceState == null) {
            Intent intent = getIntent();

            if (intent != null) {
                if (intent.getAction() != null) {
                    String action = intent.getAction();

                    if (BuildConfig.DEBUG) Timber.d("onCreate() getAction()= %s", action);

                    if (action.equals(Intent.ACTION_VIEW) && intent.getData() != null) {
                        Uri deepLink = intent.getData();
                        String radioDeepLink = deepLink.toString();

                        if (!TextUtils.isEmpty(radioDeepLink) && (mStationList != null && !mStationList.isEmpty())) {
                            String scheme = getString(R.string.scheme_radio) + "://";
                            radioDeepLink = radioDeepLink.replace(scheme, HTTP);

                            for (int i = 0; i < mStationList.size(); i++) {
                                String streamUrl = mStationList.get(i).getStreamUri();

                                if (radioDeepLink.equalsIgnoreCase(streamUrl)) {
                                    mStationId = i;
                                    mGoHome = true;
                                    break;
                                }
                            }
                        }
                    } else if (action.equals(TransistorKeys.ACTION_SHOW_PLAYER)) {
                        if (intent.hasExtra(TransistorKeys.EXTRA_STATION_ID)) {
                            mStationId = intent.getIntExtra(TransistorKeys.EXTRA_STATION_ID, -1);
                        }
                    }
                }
            }
        } else {
            mStationId = savedInstanceState.getInt(TransistorKeys.STATION_ID, -1);
            mGoHome = savedInstanceState.getBoolean("goHome");
        }

        if (mStationId == -1) {
            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            mStationId = sPref.getInt(TransistorKeys.STATION_ID_CURRENT, -1);
        }

        if (mStationId == -1) {
            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            mStationId = sPref.getInt(TransistorKeys.STATION_ID_LAST, -1);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            // Setup spinner
            Spinner spinner = findViewById(R.id.spinner);
            if (spinner != null) {
                spinner.setAdapter(new MySpinnerAdapter(toolbar.getContext(), mStationNames));

                if (mStationId == -1) mStationId = 0;

                spinner.setSelection(mStationId);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // When the given dropdown item is selected, show its contents in the
                        // container view.
                        mStationId = position;

                        Station station = mStationList.get(mStationId);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, RadioPlayerFragment.newInstance(mStationId, station))
                                .commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        }

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState, @NotNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(TransistorKeys.STATION_ID, mStationId);
        outState.putBoolean("goHome", mGoHome);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mGoHome) {
            goHome();
        }
        AppUtils.onActivityEnterExit(this);
    }

    private void goHome() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setClass(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private static class MySpinnerAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final Helper mDropDownHelper;

        MySpinnerAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new Helper(context);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = view.findViewById(android.R.id.text1);
            try {
                textView.setText(getItem(position).replace("WEB:", EMPTY_STRING));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return view;
        }

        @Override
        public Resources.Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Resources.Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }
}
