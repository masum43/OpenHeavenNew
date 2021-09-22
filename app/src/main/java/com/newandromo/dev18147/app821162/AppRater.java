package com.newandromo.dev18147.app821162;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.newandromo.dev18147.app821162.R;

public class AppRater {
    private final static String APP_TITLE = "Open Heaven Devotionals";
    private final static String APP_PNAME = "com.newandromo.amblesseddigital.openheaven";



    public static void app_launched(Context mContext) {

        SharedPreferences preferences = mContext.getSharedPreferences("DIALOG",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(preferences.getInt("count",1) == 2){
            if(!preferences.getBoolean("rated",false)) {
                showRateDialog(mContext, editor);
            }
        }

    }

    public static void showRateDialog(final Context mContext, SharedPreferences.Editor editor) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle("Rate Us");
        alert.setIcon(R.drawable.splash_icon);
        alert.setCancelable(false);
        alert.setMessage("If you enjoy this app, would you mind taking a moment to rate it? It won't take more than a minute. Thanks for your support!");
        alert.setPositiveButton("Rate it",new Dialog.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
                editor.putBoolean("rated",true);
                editor.apply();
                String url = "https://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                mContext.startActivity(i);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setNeutralButton("Remind me Later",(dialog, which) -> {
            dialog.dismiss();
        });
        alert.show();
    }
}