package info.romanelli.udacity.bakingapp;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.exoplayer2.util.MimeTypes;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import info.romanelli.udacity.bakingapp.data.IngredientData;

public final class AppUtil {

    final static private String TAG = AppUtil.class.getSimpleName();

    static private Toast TOAST;
    static private AtomicBoolean PENDING_TOAST = new AtomicBoolean(false);
    static private CountDownTimer TIMER;

    static public void showToast(final Context owner, final String message, final boolean delayShowing) {

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

    static public boolean isEmpty(@Nullable final CharSequence text) {
        return (text == null) || (text.length() == 0);
        // REVIEWER: Purposeful design decision to code my own isEmpty, vs. using
        // AppUtil.isEmpty(CharSequence), as I shouldn't have to create a mock class,
        // and import the mocking libraries, or move a simple JUnit test into the
        // Android test package/hierarchy, just to do a simple String comparison.
        // I wanted to create one of each kind of test, AndroidJUnit, and JUnit,
        // for this project/review.
        // http://tools.android.com/tech-docs/unit-testing-support#TOC-Method-...-not-mocked.-
        // https://stackoverflow.com/questions/35763289/need-help-to-write-a-unit-test-using-mockito-and-junit4
    }

    /**
     * @param url  The {@link String} representing a url to examine for content type.
     * @return a five element boolean array, where
     * element 0 is if the {@code url} is a 'video' or not,
     * element 1 is if the {@code url} is an 'audio' or not,
     * element 2 is if the {@code url} is a 'image' or not,
     * element 3 is if the {@code url} is a 'text' or not,
     * element 4 is if the {@code url} is an 'application' or not,
     */
    static public boolean[] getContentTypeInfo(final String url) throws IOException {
        // https://google.github.io/ExoPlayer/supported-formats.html
        boolean[] flags = new boolean[5];
        if (! AppUtil.isEmpty(url)) {

            String contentType = URLConnection.guessContentTypeFromName(url);

            if (AppUtil.isEmpty(contentType)) {
                /*
                Asking URLConnection for the content type via guessContentTypeFromName may not
                work, as its default internal HashMap does not contain all types of content
                types, like ".mp4".  In that case, we try one more time by making it go out
                onto the Net and ask the server on the other end of the url for the content type.
                
                This block of code only seems to run when called from a JUnit test, not prod code!
                 */
                // Ask the server for the type ...
                contentType = new URL(url).openConnection().getContentType();
            }

            flags[0] = MimeTypes.isVideo(contentType);
            flags[1] = MimeTypes.isAudio(contentType);
            flags[2] = contentType.startsWith("image/");
            flags[3] = MimeTypes.isText(contentType);
            flags[4] = MimeTypes.isApplication(contentType);
        }

        return flags;
    }

    static public String getIngredientsText(final Context context, final List<IngredientData> listIngredientData) {
        final StringBuilder builder = new StringBuilder();
        if (context != null) {
            for (IngredientData ingredientData : listIngredientData) {
                // Swapping languages while in detail activity then hitting back button causes a IllegalFormatConversionException.
                String text = context.getResources().getString(
                        R.string.ingredient_detail,
                        ingredientData.getQuantity(),
                        ingredientData.getMeasure(),
                        ingredientData.getIngredient()
                );
                builder.append(text);
                builder.append('\n');
            }
        } else {
            builder.append("");
            Log.w(TAG, "getIngredientsText: Need an Activity reference to be able to display ingredients!");
        }
        return builder.toString();
    }

}
