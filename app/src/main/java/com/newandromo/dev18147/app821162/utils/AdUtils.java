package com.newandromo.dev18147.app821162.utils;

import com.google.android.gms.ads.AdRequest;

/*import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;*/

public class AdUtils {
/*    public static final String ADMOB_BANNER = "ca-app-pub-5472985520179388/1079169146";
    public static final String ADMOB_BANNER_ENTRY_LIST = "ca-app-pub-5472985520179388/1079169146";
    public static final String ADMOB_BANNER_ENTRY_DETAIL = "ca-app-pub-5472985520179388/1079169146";
    public static final String ADMOB_INTERSTITIAL = "ca-app-pub-5472985520179388~4226957108";
    public static final String ADMOB_INTERSTITIAL_ENTRY_LIST = "ca-app-pub-5472985520179388/5565209060";

    public static final String FACEBOOK_BANNER_ENTRY_LIST = "452278452048297_573662903243184";
    public static final String FACEBOOK_BANNER_ENTRY_DETAIL = "452278452048297_573662903243184";
    public static final String FACEBOOK_BANNER_YOUTUBE = "452278452048297_573662903243184";
    public static final String FACEBOOK_INTERSTITIAL_ENTRY_LIST = "452278452048297_573662733243201";
    public static final String FACEBOOK_HS_NATIVE_ENTRY_DETAIL = "452278452048297_573662973243177";*/

/*    public static final String MOPUB_BANNER_ENTRY_LIST = "";
    public static final String MOPUB_BANNER_ENTRY_DETAIL = "";
    public static final String MOPUB_INTERSTITIAL_ENTRY_LIST = "";
    public static final String MOPUB_NATIVE_ENTRY_LIST = "585d519d615b428d93cfabb81eb38333";
    public static final String MOPUB_NATIVE_ENTRY_LIST_TINY = "f2a21099ba4e4efcb57eb365dbc64f63";*/

/*    private static final String GOOGLE_BANNER_ID = "ca-app-pub-5472985520179388/1079169146";
    private static final String GOOGLE_INTERSTITIAL_ID = "ca-app-pub-5472985520179388~4226957108";*/
    // private static final String GOOGLE_NATIVE_ID = "ca-app-pub-3940256099942544/2247696110";

    private AdUtils() {
    } // No instantiation




    private static String getErrorReason(int errorCode) {
        String errorReason = "";
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "Internal error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "Invalid request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "No fill";
                break;
        }
        return errorReason;
    }
}
