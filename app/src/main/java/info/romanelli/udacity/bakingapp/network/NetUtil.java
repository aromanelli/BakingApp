package info.romanelli.udacity.bakingapp.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import info.romanelli.udacity.bakingapp.AppUtil;
import info.romanelli.udacity.bakingapp.R;

public final class NetUtil {

    final static private String TAG = NetUtil.class.getSimpleName();

    static public boolean ifOnline(final Context context) {
        Log.d(TAG, "ifOnline() called with: context = [" + context + "] " + Thread.currentThread().getName());
        if (isOnline(context)) {
            Log.d(TAG, "ifOnline() returning 'true' context = [" + context + "] " + Thread.currentThread().getName());
            return true;
        }
        else {
            Log.d(TAG, "ifOnline() returning 'false' context = [" + context + "] " + Thread.currentThread().getName());
            AppUtil.showToast(context, context.getString(R.string.msg_offline), false);
            return false;
        }
    }

    static public boolean isOnline(final Context context) {
        Log.d(TAG, "isOnline() called with: context = [" + context + "] " + Thread.currentThread().getName());
        final boolean flag;
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            flag = (netInfo != null) && netInfo.isConnectedOrConnecting();
        } else {
            Log.w(TAG, "isOnline: Unable to obtain a ConnectivityManager reference! " + Thread.currentThread().getName());
            flag = false;
        }
        Log.d(TAG, "isOnline: Online? " + flag);
        return flag;
    }


}
