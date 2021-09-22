package com.newandromo.dev18147.app821162.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.y20k.transistor.RadioPlayerService;
import org.y20k.transistor.Station;
import org.y20k.transistor.TransistorKeys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class RadioPlayerFragment extends Fragment {
    /* Main class variables */
    private ImageButton mPlaybackButton;
    private int mStationID;
    private int mStationIDCurrent;
    private int mStationIDLast;
    private boolean mPlayback;
    private RadioPlayerService mPlayerService;
    private Station mStation;


    /* Constructor (default) */
    public RadioPlayerFragment() {
    }

    public static RadioPlayerFragment newInstance(int stationId, Station station) {
        RadioPlayerFragment fragment = new RadioPlayerFragment();
        Bundle args = new Bundle();
        args.putInt(TransistorKeys.STATION_ID, stationId);
        args.putSerializable(TransistorKeys.STATION, station);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        // get data from arguments
        if (getArguments() != null) {
            // get station id from arguments
            if (getArguments().containsKey(TransistorKeys.STATION_ID)) {
                mStationID = getArguments().getInt(TransistorKeys.STATION_ID, -1);
            }

            if (getArguments().containsKey(TransistorKeys.STATION)) {
                mStation = (Station) getArguments().getSerializable(TransistorKeys.STATION);
            }
        }

        // load playback state from preferences
        loadPlaybackState(getActivity());

        if (mStationID == -1) {
            // set station ID
            mStationID = mStationIDCurrent;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // initiate playback service
        mPlayerService = new RadioPlayerService();

        // inflate rootview from xml
        View rootView = inflater.inflate(R.layout.fragment_radio_player, container, false);

        // find views for station name and image and playback indicator
        ImageView stationImageView = rootView.findViewById(R.id.station_icon);
        TextView stationNameView = rootView.findViewById(R.id.station_name);

        // set station image
        try {
            Picasso.get()
                    .load(mStation.getStationImage())
                    .placeholder(R.drawable.radio_tower)
                    .error(R.drawable.radio_tower)
                    .fit().centerCrop()
                    .into(stationImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set text view to station name
        stationNameView.setText(mStation.getStationName());

        ImageButton share = rootView.findViewById(R.id.player_share_button);
        share.setOnClickListener(v -> {
            try {
                String stationSite = mStation.getStationSite();
                String streamUri = mStation.getStreamUri();

                final String subject = getString(R.string.radio_listen_to, mStation.getStationName());

                Bundle shareBundle = AppUtils.createShareBundle(
                        subject,
                        stationSite,
                        mStation.getStationImage(),
                        streamUri, getString(R.string.scheme_radio));
                AppUtils.contentShare(getActivity(), getChildFragmentManager(), shareBundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // construct image button
        mPlaybackButton = rootView.findViewById(R.id.player_playback_button);

        // set listener to playback button
        mPlaybackButton.setOnClickListener(view -> {

            // playback stopped or new station - start playback
            if (!mPlayback || mStationID != mStationIDCurrent) {

                // set playback true
                mPlayback = true;
                // rotate playback button
                changeVisualState(getActivity());
                // start player
                mPlayerService.startActionPlay(getActivity(), mStationID, mStation);
                if (BuildConfig.DEBUG) Timber.v("Starting player service.");

            }
            // playback active - stop playback
            else {
                // set playback false
                mPlayback = false;
                // rotate playback button
                changeVisualState(getActivity());
                // stop player
                if (BuildConfig.DEBUG) Timber.v("Stopping player service.");
                mPlayerService.startActionStop(getActivity());
            }

            // save state of playback in settings store
            savePlaybackState(getActivity());
        });

        // broadcast receiver: player service stopped playback
        BroadcastReceiver playbackStoppedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // set playback false
                mPlayback = false;
                // rotate playback button
                changeVisualState(context);
                // save state of playback to settings
                savePlaybackState(context);
            }
        };
        IntentFilter intentFilter = new IntentFilter(TransistorKeys.ACTION_PLAYBACK_STOPPED);
        if (getActivity() != null)
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(playbackStoppedReceiver, intentFilter);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            if (getActivity() != null) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        try {
            inflater.inflate(R.menu.menu_radio_player, menu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NotNull Menu menu) {
        MenuItem onAirOffAir = menu.findItem(R.id.menu_onair_offair);
        if (getActivity() != null && onAirOffAir != null) {
            try {
                Context context = getActivity();
                int offAir = ContextCompat.getColor(context, R.color.grey_400);
                int onAir = ContextCompat.getColor(context, R.color.green_400);
                boolean radioOnAir = (mPlayback && mStationID == mStationIDCurrent);

                Drawable searchItemIcon = DrawableCompat.wrap(onAirOffAir.getIcon());
                DrawableCompat.setTint(searchItemIcon, radioOnAir ? onAir : offAir);
                onAirOffAir.setIcon(searchItemIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();

        // set up button symbol and playback indicator
        setVisualState();
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    /* Animate button and then set visual state */
    private void changeVisualState(Context context) {
        // get rotate animation from xml
        Animation rotate;
        if (mPlayback) {
            // if playback has been started get start animation
            rotate = AnimationUtils.loadAnimation(context, R.anim.rotate_clockwise_slow);
        } else {
            // if playback has been stopped get stop animation
            rotate = AnimationUtils.loadAnimation(context, R.anim.rotate_counterclockwise_fast);
        }

        // attach listener for animation end
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // set up button symbol and playback indicator afterwards
                setVisualState();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // start animation of button
        mPlaybackButton.startAnimation(rotate);
    }

    /* Set button symbol and playback indicator */
    private void setVisualState() {
        if (getActivity() != null)
            getActivity().invalidateOptionsMenu();
        // this station is running
        if (mPlayback && mStationID == mStationIDCurrent) {
            // change playback button image to stop
            mPlaybackButton.setImageResource(R.drawable.ic_stop_white_36dp);
        }
        // playback stopped
        else {
            // change playback button image to play
            mPlaybackButton.setImageResource(R.drawable.ic_play_arrow_white_36dp);
        }
    }

    /* Save station name and ID and playback state to SharedPreferences */
    private void savePlaybackState(Context context) {
        // playback started
        if (mPlayback) {
            mStationIDLast = mStationIDCurrent;
            mStationIDCurrent = mStationID;

        }
        // playback stopped
        else {
            mStationIDLast = mStationIDCurrent;
        }

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(TransistorKeys.STATION_ID_CURRENT, mStationIDCurrent);
        editor.putInt(TransistorKeys.STATION_ID_LAST, mStationIDLast);
        editor.putBoolean(TransistorKeys.PLAYBACK, mPlayback);
        editor.apply();
    }

    /* Loads playback state from preferences */
    private void loadPlaybackState(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        mStationIDCurrent = settings.getInt(TransistorKeys.STATION_ID_CURRENT, -1);
        mStationIDLast = settings.getInt(TransistorKeys.STATION_ID_LAST, -1);
        mPlayback = settings.getBoolean(TransistorKeys.PLAYBACK, false);
    }
}
