package com.cruzroja.android.app;

import android.content.ContentResolver;
import android.database.Cursor;

import com.cruzroja.android.database.CreuRojaContract;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lapuente on 29.10.13.
 */
public class Settings {
    public static final String ACCESS_TOKEN = "AccessToken";
    public static final String MAP_TYPE = "MapType";

    public static final String SHOW_ADAPTADAS = "adaptadas";
    public static final String SHOW_ASAMBLEA = "asamblea";
    public static final String SHOW_BRAVO = "bravo";
    public static final String SHOW_CUAP = "cuap";
    public static final String SHOW_HOSPITAL = "hospital";
    public static final String SHOW_MARITIMO = "maritimo";
    public static final String SHOW_NOSTRUM = "nostrum";
    public static final String SHOW_SOCIAL = "social";
    public static final String SHOW_TERRESTRE = "terrestre";

    public static long getLastUpdateTime(ContentResolver cr){
        String[] projection = {
                "MAX (" + CreuRojaContract.Locations.LAST_MODIFIED + ") AS "
                        + CreuRojaContract.Locations.LAST_MODIFIED
        };
        Cursor c = cr.query(CreuRojaContract.Locations.CONTENT_LOCATIONS, projection, null,
                null, null);
        long lastUpdateTime = 0;
        if(c.moveToFirst()){
            lastUpdateTime = c.getLong(c.getColumnIndex(CreuRojaContract.Locations.LAST_MODIFIED));
        }
        c.close();
        return lastUpdateTime;

    }
}
