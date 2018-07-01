package info.mbobiosio.journalapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

    private static final String IS_FIRST_LAUNCH = "IsFirstLaunch";
    private static final String PREF_NAME = "journalapp";
    int PRIVATE_MODE = 0;

    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    Context mContext;

    public PreferenceHelper(Context mContext) {
        this.mContext = mContext;
        mPref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPref.edit();
    }

    public void setFirstLaunch(boolean isFirstLanuch) {
        mEditor.putBoolean(IS_FIRST_LAUNCH, isFirstLanuch);
        mEditor.commit();
    }
    public boolean isFirstLaunch() {
        return mPref.getBoolean(IS_FIRST_LAUNCH, true);
    }
}
