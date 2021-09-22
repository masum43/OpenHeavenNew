package com.newandromo.dev18147.app821162.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class MyDateUtils {
    private static final String REGEX2TWO = "(?<=[^\\d])(\\d)(?=[^\\d])";
    private static final String TWO = "0$1";

    private static final String[] TIMESTAMP_FORMATS_CONFIRM = new String[]{
            "EEE, dd MMM yyyy HH:mm:ss Z",
            "EEE, dd MMM yyyy HH:mm:ss",
            "EEE, dd MMM yyyy HH:mm",
            "EEE, dd MM yyyy HH:mm",
            "EEE dd MMM yyyy HH:mm:ss",
            "EEE dd MMM yyyy HH:mm",
            "EEE MMM dd yyyy HH:mm"};

    private static final String[] TIMESTAMP_FORMATS = new String[]{
            "EEE, dd MMM yyyy HH:mm:ss Z",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ss Z",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm",
            "yyyy-MM-dd'T'kk:mm",
            "yyyy-MM-dd HH:mm:ss.SSSZ",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss Z",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mmZ",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd kk:mm",
            "yyyy/MM/dd HH:mm",
            "EEE, dd MMM yyyy HH:mm:ss",
            "EEE, dd MMM yyyy HH:mm",
            "EEE, dd MM yyyy HH:mm",
            "EEE, MM/dd/yyyy - HH:mm",
            "EEE, MMM dd, yyyy HH:mm",
            "EEE, dd MMM yy HH:mm",
            "EEE, dd MMM yyyy, HH:mm:ss Z",
            "EEE, dd MMM yyyy, HH:mm",
            "EEE,dd MMM yyyy HH:mm:ss Z",
            "EEE,dd MMM yyyy HH:mm",
            "EEE MMM dd HH:mm:ss yyyy Z",
            "EEE MMM dd HH:mm:ss yyyy",
            "EEE MMM dd HH:mm:ss Z yyyy",
            "EEE dd MMM yyyy HH:mm:ss Z",
            "EEE dd MMM yyyy HH:mm",
            "EEE MMM dd yyyy HH:mm:ss Z",
            "EEE MMM dd yyyy HH:mm",
            "MMM dd, yyyy HH:mm",
            "dd MMM yyyy HH:mm:ss",
            "dd MMM yyyy HH:mm",
            "dd MMM yyyy, HH:mm",
            "dd-MM-yyyy HH:mm:ss",
            "dd-MM-yyyy HH:mm",
            "dd/MM/yyyy HH:mm",
            "MM/dd/yyyy HH:mm",
            "dd.MM.yyyy - HH:mm",
            "dd.MM.yyyy HH:mm",
            "dd-MM-yyyy",
            "dd/MM/yyyy",
            "yyyy-MM-dd",
            "yyyy/MM/dd"};

    private static final Map<String, String> REGEX_MAP = new HashMap<String, String>() {{
        put("PT(\\d\\d)S", "00:$1");
        put("PT(\\d\\d)M", "$1:00");
        put("PT(\\d\\d)H", "$1:00:00");
        put("PT(\\d\\d)M(\\d\\d)S", "$1:$2");
        put("PT(\\d\\d)H(\\d\\d)S", "$1:00:$2");
        put("PT(\\d\\d)H(\\d\\d)M", "$1:$2:00");
        put("PT(\\d\\d)H(\\d\\d)M(\\d\\d)S", "$1:$2:$3");
    }};

    public static String formatDuration(String duration) {
        try {
            String d = duration.replaceAll(REGEX2TWO, TWO);
            String regex = getDurationFormatRegex(d);
            if (!TextUtils.isEmpty(regex)) {
                return d.replaceAll(regex, REGEX_MAP.get(regex));
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    private static String getDurationFormatRegex(String duration) {
        for (String regex : REGEX_MAP.keySet())
            if (Pattern.matches(regex, duration))
                return regex;
        return null;
    }

    public static long parseTimestampToMillis(String timestamp, boolean isYoutube) {
        for (String format : TIMESTAMP_FORMATS) {
            try {
                // SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                // sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                try {
                    if (!TextUtils.isEmpty(timestamp)) {
                        if (format.startsWith("yyyy-") && timestamp.contains("-")) {
                            String[] splitTime = TextUtils.split(timestamp, "-");
                            if (splitTime[0].trim().length() != 4) {
                                continue;
                            }
                        } else if (format.startsWith("yyyy/") && timestamp.contains("/")) {
                            String[] splitTime = TextUtils.split(timestamp, "/");
                            if (splitTime[0].trim().length() != 4) {
                                continue;
                            }
                        } else if (!isYoutube && stringEqualItemFromList(format, TIMESTAMP_FORMATS_CONFIRM)) {
                            String[] splitTime = TextUtils.split(timestamp, " ");
                            if (splitTime[3].trim().length() != 4) {
                                continue;
                            }
                        }
                    }
                } catch (Exception ignore) {
                }

                // Timber.d("format= %s", format);
                // Timber.d("timestamp= %s", timestamp);

                Date date = new SimpleDateFormat(format, Locale.US).parse(timestamp);

                try {
                    if (format.startsWith("dd/") && timestamp.contains("/")) {
                        if (date.getTime() > System.currentTimeMillis()) {
                            continue;
                        }
                    }
                } catch (Exception ignore) {
                }

                try {
                    if (date.getTime() > System.currentTimeMillis()) {
                        if (isYoutube) {
                            return date.getTime();
                        } else {
                            return 0;
                        }
                    }
                } catch (Exception ignore) {
                }

                // return DateFormat.format("yyyy-MM-dd HH:mm", date).toString();
                return date.getTime();
            } catch (Exception e) {
                // continue;
            }
        }
        return 0;
    }

    public static String parseTimestampToString(String timestamp) {
        for (String format : TIMESTAMP_FORMATS) {
            try {
                // SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                // sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                try {
                    if (!TextUtils.isEmpty(timestamp)) {
                        if (format.startsWith("yyyy-") && timestamp.contains("-")) {
                            String[] splitTime = TextUtils.split(timestamp, "-");
                            if (splitTime[0].trim().length() != 4) {
                                continue;
                            }
                        } else if (format.startsWith("yyyy/") && timestamp.contains("/")) {
                            String[] splitTime = TextUtils.split(timestamp, "/");
                            if (splitTime[0].trim().length() != 4) {
                                continue;
                            }
                        }
                    }
                } catch (Exception ignore) {
                }

                Date date = new SimpleDateFormat(format, Locale.US).parse(timestamp);

                try {
                    if (format.startsWith("dd/") && timestamp.contains("/")) {
                        if (date.getTime() > System.currentTimeMillis()) {
                            continue;
                        }
                    }
                } catch (Exception ignore) {
                }

                return flipDateTime(DateFormat.format("yyyy-MM-dd", date).toString());
            } catch (Exception e) {
                // continue;
            }
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    private static String flipDateTime(String dateString) {
        SimpleDateFormat dateFormat;
        try {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        } catch (Exception ignored) {
            try {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            } catch (Exception ignoredException) {
                try {
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                } catch (Exception ignore) {
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
                }
            }
        }

        try {
            Date convertedDate = dateFormat.parse(dateString);
            return DateFormat.format("dd MMM yyyy", convertedDate).toString();
        } catch (Exception ignore) {
        }

        return "";
    }

    private static boolean stringEqualItemFromList(String inputString, String[] items) {
        try {
            for (String item : items) {
                try {
                    if (inputString.equals(item)) {
                        return true;
                    }
                } catch (Exception ignore) {
                }
            }
        } catch (Exception ignore) {
        }
        return false;
    }
}
