package com.newandromo.dev18147.app821162.ui.enums;

import android.graphics.Color;

import com.newandromo.dev18147.app821162.R;

public enum CustomSocial {
    FACEBOOK(R.drawable.ic_facebook_white_24dp, Color.parseColor("#3b5998"), R.string.social_facebook, "com.facebook.katana"),
    TWITTER(R.drawable.ic_twitter_white_24dp, Color.parseColor("#1da1f2"), R.string.social_twitter, "com.twitter.android"),
    WHATSAPP(R.drawable.ic_whatsapp_white_24dp, Color.parseColor("#25d366"), R.string.social_whatsapp, "com.whatsapp"),
    SMS(R.drawable.ic_android_messages_white_24dp, Color.parseColor("#0084ff"), R.string.social_sms, ""),
    ANDROID(R.drawable.ic_share_white_24dp, Color.parseColor("#CC000000"), R.string.more, "");

    int icon;
    int iconColor;
    int title;
    String packageName;

    CustomSocial(int icon, int iconColor, int title, String packageName) {
        this.icon = icon;
        this.iconColor = iconColor;
        this.title = title;
        this.packageName = packageName;
    }

    public int getIcon() {
        return icon;
    }

    public int getIconColor() {
        return iconColor;
    }

    public int getTitle() {
        return title;
    }

    public String getPackageName() {
        return packageName;
    }
}
