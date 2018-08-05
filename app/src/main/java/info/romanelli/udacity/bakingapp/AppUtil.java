package info.romanelli.udacity.bakingapp;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

public final class AppUtil {

    final static private String TAG = AppUtil.class.getSimpleName();

    static private Toast TOAST;
    static private AtomicBoolean PENDING_TOAST = new AtomicBoolean(false);
    static private CountDownTimer TIMER;

    static public void showToast(final Context owner, final String message, final boolean delayShowing) {

        // TODO AOR Using android.os.Handler class removes allot of below code?
        /*
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ...
                }
            },
            DELAY_MILLIS
        );
         */

        Log.d(TAG, "showToast() called with: owner = [" + owner + "], message = [" +
                message + "], pending = ["+ PENDING_TOAST.get() +"] " + Thread.currentThread().getName());

        if (PENDING_TOAST.compareAndSet(false, true)) {
            if (TIMER != null) {
                TIMER.cancel();
            }
            cancelToast();
        }

        if (delayShowing) {
            TIMER = new CountDownTimer(200, 100) {
                public void onTick(long millisUntilFinished) {
                    Log.d(TAG, "onTick() called with: millisUntilFinished = [" + millisUntilFinished +
                            "], pending = [" + PENDING_TOAST.get() + "] " + Thread.currentThread().getName());
                    if (!PENDING_TOAST.get()) {
                        Log.d(TAG, "onTick() canceling timer!  millisUntilFinished = [" + millisUntilFinished +
                                "], pending = [" + PENDING_TOAST.get() + "] " + Thread.currentThread().getName());
                        cancel(); // Cancel timer, not toast
                    }
                }
                public void onFinish() {
                    Log.d(TAG, "onFinish() called, pending = [" + PENDING_TOAST.get() + "] " + Thread.currentThread().getName());
                    if (PENDING_TOAST.get()) {
                        drawToast(owner, message);
                        PENDING_TOAST.set(false);
                    }
                }
            }.start();
        }
        else {
            drawToast(owner, message);
            PENDING_TOAST.set(false);
        }

    }

    static public void hideToast() {
        Log.d(TAG, "hideToast() called, pending = ["+ PENDING_TOAST.get() +"] " + Thread.currentThread().getName());
        cancelToast();
        PENDING_TOAST.set(false);
    }

    static private void cancelToast() {
        Log.d(TAG, "cancelToast() called, TOAST = ["+ TOAST +"], pending = ["+ PENDING_TOAST.get() +"] " + Thread.currentThread().getName());
        if (TOAST != null) {
            Log.d(TAG, "cancelToast: CANCELING TOAST! " + Thread.currentThread().getName());
            TOAST.cancel();
            TOAST = null;
        }
    }

    static private void drawToast(final Context owner, final String message) {
        cancelToast(); // In case a previous toast is showing, hide, NOT cancel!
        TOAST = Toast.makeText(
                owner,
                (message != null) ? message : owner.getString(R.string.msg_retrieving_data),
                Toast.LENGTH_LONG);
        TOAST.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        Log.d(TAG, "onFinish() called, SHOWING TOAST!  pending = [" + PENDING_TOAST.get() + "] " + Thread.currentThread().getName());
        TOAST.show();
    }

}
