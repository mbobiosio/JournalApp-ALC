package info.mbobiosio.journalapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Constants {

    public static String USERS = "users";
    public static String JOURNAL_APP = "journals";
    public static String AVATAR = "profile_images";
    public static final String DEFAULT_AVATAR = "http://medinixtechnologies.com/wp-content/uploads/2017/04/dummy-man-570x570.png";

    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static String convertToReadableTime(long timestamp) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("E dd, MM-yyyy", Locale.getDefault());
        Date date = new Date(timestamp);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    public static String convertToReadableDate(final String dateStr){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());

        try {
            Date date = dateFormatter.parse(dateStr);
            SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd - hh:mm a");
            String formattedDate = formatter.format(date);

            return formattedDate;
        } catch (ParseException e) {


        }

        return dateStr;
    }
}
