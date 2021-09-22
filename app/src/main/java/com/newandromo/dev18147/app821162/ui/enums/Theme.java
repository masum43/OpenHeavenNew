package com.newandromo.dev18147.app821162.ui.enums;

import com.newandromo.dev18147.app821162.R;

public enum Theme {
    NIGHT(R.color.app_primary_material_night, R.color.app_accent_material_night,
            R.style.Nav_AppTheme_Night, R.style.AppTheme_Night),

    DARK(R.color.app_primary_material_dark, R.color.app_accent_material_dark,
            R.style.Nav_AppTheme_Dark, R.style.AppTheme_Dark),

    BLACK(R.color.grey_850, R.color.app_accent_material_black,
            R.style.Nav_AppTheme_Black, R.style.AppTheme_Black),

    LIGHT(R.color.grey_100, R.color.app_accent_material_light,
            R.style.Nav_AppTheme_Light, R.style.AppTheme_Light),

    TOMATO(R.color.app_primary_material_tomato, R.color.app_accent_material_tomato,
            R.style.Nav_AppTheme_Tomato, R.style.AppTheme_Tomato),

    RED(R.color.app_primary_material_red, R.color.app_accent_material_red,
            R.style.Nav_AppTheme_Red, R.style.AppTheme_Red),

    FLAMINGO(R.color.app_primary_material_flamingo, R.color.app_accent_material_flamingo,
            R.style.Nav_AppTheme_Flamingo, R.style.AppTheme_Flamingo),

    PINK(R.color.app_primary_material_pink, R.color.app_accent_material_pink,
            R.style.Nav_AppTheme_Pink, R.style.AppTheme_Pink),

    PURPLE(R.color.app_primary_material_purple, R.color.app_accent_material_purple,
            R.style.Nav_AppTheme_Purple, R.style.AppTheme_Purple),

    DEEP_PURPLE(R.color.app_primary_material_deep_purple, R.color.app_accent_material_deep_purple,
            R.style.Nav_AppTheme_Deep_Purple, R.style.AppTheme_Deep_Purple),

    INDIGO(R.color.app_primary_material_indigo, R.color.app_accent_material_indigo,
            R.style.Nav_AppTheme_Indigo, R.style.AppTheme_Indigo),

    BLUE(R.color.app_primary_material_blue, R.color.app_accent_material_blue,
            R.style.Nav_AppTheme_Blue, R.style.AppTheme_Blue),

    LIGHT_BLUE(R.color.app_primary_material_light_blue, R.color.app_accent_material_light_blue,
            R.style.Nav_AppTheme_Light_Blue, R.style.AppTheme_Light_Blue),

    LAVENDER(R.color.app_primary_material_lavender, R.color.app_accent_material_lavender,
            R.style.Nav_AppTheme_Lavender, R.style.AppTheme_Lavender),

    CYAN(R.color.app_primary_material_cyan, R.color.app_accent_material_cyan,
            R.style.Nav_AppTheme_Cyan, R.style.AppTheme_Cyan),

    TEAL(R.color.app_primary_material_teal, R.color.app_accent_material_teal,
            R.style.Nav_AppTheme_Teal, R.style.AppTheme_Teal),

    GREEN(R.color.app_primary_material_green, R.color.app_accent_material_green,
            R.style.Nav_AppTheme_Green, R.style.AppTheme_Green),

    LIGHT_GREEN(R.color.app_primary_material_light_green, R.color.app_accent_material_light_green,
            R.style.Nav_AppTheme_Light_Green, R.style.AppTheme_Light_Green),

    LIME(R.color.app_primary_material_lime, R.color.app_accent_material_lime,
            R.style.Nav_AppTheme_Lime, R.style.AppTheme_Lime),

    BANANA(R.color.app_primary_material_banana, R.color.app_accent_material_banana,
            R.style.Nav_AppTheme_Banana, R.style.AppTheme_Banana),

    ORANGE(R.color.app_primary_material_orange, R.color.app_accent_material_orange,
            R.style.Nav_AppTheme_Orange, R.style.AppTheme_Orange),

    DEEP_ORANGE(R.color.app_primary_material_deep_orange, R.color.app_accent_material_deep_orange,
            R.style.Nav_AppTheme_Deep_Orange, R.style.AppTheme_Deep_Orange),

    BROWN(R.color.app_primary_material_brown, R.color.app_accent_material_brown,
            R.style.Nav_AppTheme_Brown, R.style.AppTheme_Brown),

    BLUE_GREY(R.color.app_primary_material_bluegrey, R.color.app_accent_material_bluegrey,
            R.style.Nav_AppTheme_BlueGrey, R.style.AppTheme_BlueGrey);

    int colorId;
    int accentColorId;
    int navigationThemeId;
    int themeId;

    Theme(int colorId, int accentColorId, int navigationThemeId, int themeId) {
        this.colorId = colorId;
        this.accentColorId = accentColorId;
        this.navigationThemeId = navigationThemeId;
        this.themeId = themeId;
    }

    public int getColorId() {
        return colorId;
    }

    public int getAccentColorId() {
        return accentColorId;
    }

    public int getNavigationThemeId() {
        return navigationThemeId;
    }

    public int getThemeId() {
        return themeId;
    }
}
