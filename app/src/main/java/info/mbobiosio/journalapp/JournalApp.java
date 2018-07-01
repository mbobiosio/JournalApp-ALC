package info.mbobiosio.journalapp;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.google.firebase.database.FirebaseDatabase;

public class JournalApp extends Application {

    JournalApp mJournalApp;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        JournalApp.mContext = getApplicationContext();
        mJournalApp = this;

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
