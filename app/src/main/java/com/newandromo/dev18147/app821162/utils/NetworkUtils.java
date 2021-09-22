package com.newandromo.dev18147.app821162.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import com.newandromo.dev18147.app821162.RemoteConfig;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

public class NetworkUtils {

    private NetworkUtils() {
        // No instances
    }

    public static OkHttpClient getOkHttpClient(boolean isAcceptCookies, // for manually override
                                               long connectTimeout, long readTimeout) {
        OkHttpClient client = OkHttpSingleton.getInstance().getClient();
        if (RemoteConfig.isAcceptCookies() && isAcceptCookies) {
            // Timber.d("ACCEPTING COOKIES");
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            return client.newBuilder()
                    .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                    .readTimeout(readTimeout, TimeUnit.SECONDS)
                    .cookieJar(new JavaNetCookieJar(cookieManager))
                    .build();
        } else {
            // Timber.d("NOT ACCEPTING COOKIES");
            return client.newBuilder()
                    .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                    .readTimeout(readTimeout, TimeUnit.SECONDS)
                    .build();
        }
    }

    public static boolean isConnected(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connMgr != null) {
                NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
                if (activeInfo != null && activeInfo.isConnected()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*public static boolean isWiFiConnected(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connMgr != null) {
                NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
                if (activeInfo != null && activeInfo.isConnected()) {
                    return activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
                }
            }
        } catch (Exception e) {
            Timber.e("isWiFiConnected() error= %s", e.getMessage());
        }
        return false;
    }*/

    /**
     * Get IP address from first non-localhost interface.
     * Taken from http://stackoverflow.com/a/13007325/762442.
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    /*public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = (addr instanceof Inet4Address);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    } else {
                        return getIPAddress();
                    }
                }
            }
        } catch (Exception e) {
            Timber.e("getIPAddress(1) error= %s", e.getMessage());
        } // for now eat exceptions
        return ""; // 192.168.0.101
    }*/

    /**
     * Get Local User IP Address
     *
     * @return Local IP Address
     */
    /*private static String getIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            Timber.e("getIPAddress(2) error= %s", e.getMessage());
        }
        return ""; // 192.168.0.102
    }*/
}
