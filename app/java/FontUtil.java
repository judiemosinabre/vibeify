package com.example.vibeifyfer;

import android.content.Context;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

public class FontUtil {
    public static Typeface getInterBold18(Context context) {
        return ResourcesCompat.getFont(context, R.font.inter_bold_18);
    }

    public static Typeface getInterRegular18(Context context) {
        return ResourcesCompat.getFont(context, R.font.inter_regular_18);
    }

    public static Typeface getInterBold28(Context context) {
        return ResourcesCompat.getFont(context, R.font.inter_bold_28);
    }

    public static Typeface getKronaOne(Context context) {
        return ResourcesCompat.getFont(context, R.font.krona_one);
    }
}
