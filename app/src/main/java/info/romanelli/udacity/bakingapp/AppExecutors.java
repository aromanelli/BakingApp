package info.romanelli.udacity.bakingapp;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    // REVIEWER: Much of below code came from T09b.10 exercise code

    final static private String TAG = AppExecutors.class.getSimpleName();

    static private AppExecutors REF;

    final private Executor diskIO;
    private final Executor mainThread;
    private final Executor networkIO;

    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    static public AppExecutors $() {
        if (REF == null) {
            synchronized (TAG) {
                if (REF == null) {
                    Log.d(TAG, "$: Creating Executors!");
                    REF = new AppExecutors(
                            Executors.newSingleThreadExecutor(),
                            Executors.newFixedThreadPool(3),
                            new MainThreadExecutor()
                    );
                }
            }
        }
        return REF;
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    public Executor netIO() {
        return networkIO;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

}
