package info.romanelli.udacity.bakingapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import info.romanelli.udacity.bakingapp.data.StepData;
import info.romanelli.udacity.bakingapp.event.StepDataEvent;

/**
 * A fragment representing a single RecipeInfo detail screen.
 * This fragment is either contained in a
 * {@link RecipeInfoActivity}({@link RecipeInfoRecyclerViewAdapter})
 * in two-pane mode (on tablets), or a {@link RecipeInfoStepActivity}
 * on handsets.
 */
public class RecipeInfoStepFragment extends Fragment implements PlaybackPreparer {

    // https://github.com/google/ExoPlayer/issues/4643

    // REVIEWER: Parts of the code below was re-purposed from ...
    //   https://github.com/google/ExoPlayer/blob/release-v2/demos/main/src/main/java/com/google/android/exoplayer2/demo/PlayerActivity.java
    //   AdvancedAndroid_ClassicalMusicQuiz:origin/TMED.06-Solution-AddMediaButtonReceiver
    //   https://medium.com/google-exoplayer/playback-notifications-with-exoplayer-a2f1a18cf93b
    // ... as they are boiler-plate type of code, and I have been struggling with
    // getting the lifecycle of ExoPlayers's in Fragments with ViewPagers to work correctly.

    private static final String TAG = RecipeInfoStepFragment.class.getSimpleName();

    private static final String MEDIA_IMAGE_NOT_VIDEO = "IMAGE_MEDIA";

    // Saved instance state keys.
    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";
    private static final String KEY_CURRENT_PAGE = "frag_current_page";

    private StepData mStepData;
    private int mStepDataId;

    private String mMediaURL;
    private String mNotifyTitle;
    private String mNotifyText;

    private PlayerNotificationAdapter mPlayerNotificationAdapter;

    private PlayerView mPlayerView;
    private SimpleExoPlayer mPlayer;
    private MediaSource mMediaSource;
    private DefaultTrackSelector mTrackSelector;
    private DefaultTrackSelector.Parameters mTrackSelectorParameters;
    private boolean mStartAutoPlay;
    private int mStartWindow;
    private long mStartPosition;

    private boolean mCurrentPage = false;

    /**
     * Mandatory empty constructor for the fragment manager to
     * instantiate the fragment (e.g. upon screen orientation changes).
     */
    public RecipeInfoStepFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getActivity() == null) {
            Log.e(TAG, "onCreate: getActivity() == null!");
        }

        if (getArguments() != null) {

            /*
            We use the bundle data, and not the ViewModelProviders.of(...).get(...).getXXX()
            method of data acquisition, because in RecipeInfoStepActivity, it uses a ViewPager,
            and it builds each of these fragments up front, even though the user sees just one
            of these at a time.  Each fragment has its own displaying orders, which StepData to
            display information about.

            In other words, multiple RecipeInfoStepFragments, each with their own StepData,
            and not multiple RecipeInfoStepFragments, all sharing the same one StepData.

            Could use a Map to get around the problem, but why bother; just use arguments.
             */

            mStepData = getArguments().getParcelable(MainActivity.KEY_STEP_DATA);
            if (mStepData == null)
                throw new IllegalStateException("Expected a " + StepData.class.getSimpleName() + " reference!");

            mStepDataId = getArguments().getInt(MainActivity.KEY_STEP_DATA_ID);
            if (mStepDataId <= 0) // Step ID is Step List Index + 1
                throw new IllegalStateException("Expected a StepData identifier value!");
            Log.d(TAG, "onCreate: mStepDataId: " + mStepDataId);

            mNotifyTitle = ViewModelProviders.of(getActivity()).get(DataViewModel.class)
                    .getRecipeData().getName();
            mNotifyText = mStepData.getShortDescription();

        }

        // Important to register after we're ready to do stuff, as RecipeInfoFragmentsPagerAdapter
        // uses the postSticky, which means the event annotated method gets called IMMEDIATELY
        // when the register (below) gets called.  If the fragment is newly instantiated, but not
        // had its onCreate called yet, then member vars are null when the event method is called.
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView;
        String url = getURLToUse();
        if (AppUtil.isEmpty(url) || MEDIA_IMAGE_NOT_VIDEO.equals(url)) {
            rootView = inflater.inflate(R.layout.recipeinfo_step_content_image, container, false);
            createImageView(rootView, url);
            createTextView(rootView);
        } else {
            rootView = inflater.inflate(R.layout.recipeinfo_step_content_video, container, false);
            createVideoView(rootView, url);
            createTextView(rootView);
        }

        if (savedInstanceState != null) {
            mTrackSelectorParameters = savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
            mStartAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            mStartWindow = savedInstanceState.getInt(KEY_WINDOW);
            mStartPosition = savedInstanceState.getLong(KEY_POSITION);
            mCurrentPage = savedInstanceState.getBoolean(KEY_CURRENT_PAGE);
        } else {
            mTrackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
            clearStartPosition();
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || mPlayer == null) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() { // Shutdown1
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() { // Shutdown2
        super.onStop();
        EventBus.getDefault().unregister(this); // Registered in RecipeInfoFragmentsPagerAdapter
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        updateTrackSelectorParameters();
        updateStartPosition();
        outState.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, mTrackSelectorParameters);
        outState.putBoolean(KEY_AUTO_PLAY, mStartAutoPlay);
        outState.putInt(KEY_WINDOW, mStartWindow);
        outState.putLong(KEY_POSITION, mStartPosition);

        outState.putBoolean(KEY_CURRENT_PAGE, mCurrentPage);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventStepData(StepDataEvent event) {
        Log.d(TAG, "eventStepData() called with: event = [" + event + "]");
        if (event.getType().equals(StepDataEvent.Type.SELECTED)) {
            if (mStepData.equals(event.getStepData())) {
                StepDataEvent stickyEvent = EventBus.getDefault().getStickyEvent(StepDataEvent.class);
                // Better check that an event was actually posted before
                if (event.equals(stickyEvent)) {
                    // "Consume" the sticky event
                    EventBus.getDefault().removeStickyEvent(stickyEvent);
                }
                mCurrentPage = true;
            } else {
                mCurrentPage = false;
                Log.d(TAG, "eventStepData: CLEARING notification for StepData id [" + mStepDataId + "]");
                if (mPlayer != null) {
                    mPlayer.setPlayWhenReady(false);
                }
            }
            Log.d(TAG, "eventStepData: mCurrentPage: " + mCurrentPage + " || " + event);
            setVideoPlayerNotificationState();
        }
    }

    @SuppressWarnings("unused")
    public boolean isCurrentPage() {
        return mCurrentPage;
    }

    @Override
    public void preparePlayback() {
        initializePlayer();
    }

    private void initializePlayer() {

        // Don't init player if there's no media to show it ...
        if (mMediaURL != null) {

            if (mPlayer == null) {

                if (getContext() == null)
                    throw new IllegalStateException("Expected a non-null getContext() value!");
                Context context = getContext().getApplicationContext();

                mTrackSelector = new DefaultTrackSelector();
                mTrackSelector.setParameters(mTrackSelectorParameters);

                mPlayer = ExoPlayerFactory.newSimpleInstance(
                        new DefaultRenderersFactory(context),
                        mTrackSelector,
                        new DefaultLoadControl()
                );

                if (mStepDataId == 0)
                    throw new IllegalStateException("mStepDataId should never be zero! [+ mStepDataId +]");
                mPlayerNotificationAdapter =
                        new PlayerNotificationAdapter(mPlayer, mStepDataId, mNotifyTitle, mNotifyText);
                PlayerNotificationManager pnm = new PlayerNotificationManager(
                        context,
                        MainActivity.CHANNEL_ID,
                        mStepDataId,
                        mPlayerNotificationAdapter
                );
                mPlayerNotificationAdapter.setManager(pnm);

                pnm.setOngoing(false);
                // pnm.setUseNavigationActions(false);
                pnm.setFastForwardIncrementMs(0); // Remove FF
                pnm.setStopAction(null); // Remove Stop
                pnm.setRewindIncrementMs(0);
                pnm.setUsePlayPauseActions(true);

                // Below does a "pnm.setPlayer(mPlayer)" call ...
                mPlayerNotificationAdapter.setNotificationState();

                mPlayer.addListener(new PlayerEventListener());

                mPlayer.setPlayWhenReady(mStartAutoPlay);
                mPlayerView.setPlayer(mPlayer);
                mPlayerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
                mPlayerView.setPlaybackPreparer(this);

                // Prepare the MediaSource.
                String userAgent = Util.getUserAgent(
                        context,
                        "RecipeInfo_" + RecipeInfoStepFragment.class.getSimpleName()
                );

                Log.d(TAG, "initializePlayer: mMediaURL: [" + mMediaURL + "]");
                mMediaSource =
                        new ExtractorMediaSource.Factory(
                                new DefaultDataSourceFactory(context, userAgent)
                        ).setExtractorsFactory(
                                new DefaultExtractorsFactory()
                        ).createMediaSource(
                                Uri.parse(mMediaURL)
                        );

            }

            boolean haveStartPosition = mStartWindow != C.INDEX_UNSET;
            if (haveStartPosition) {
                mPlayer.seekTo(mStartWindow, mStartPosition);
            }
            mPlayer.prepare(mMediaSource, !haveStartPosition, false);
        }

    }

    private void releasePlayer() {
        if (mPlayer != null) {
            updateTrackSelectorParameters();
            updateStartPosition();
            mPlayerNotificationAdapter.setNotificationActive(false);
            mPlayer.release(); // No mPlayer.stop() ?
            mPlayerNotificationAdapter = null;
            mPlayer = null;
            mMediaSource = null;
            mTrackSelector = null;
        }
    }

    private void updateTrackSelectorParameters() {
        if (mTrackSelector != null) {
            mTrackSelectorParameters = mTrackSelector.getParameters();
        }
    }

    private void updateStartPosition() {
        if (mPlayer != null) {
            mStartAutoPlay = mPlayer.getPlayWhenReady();
            mStartWindow = mPlayer.getCurrentWindowIndex();
            mStartPosition = Math.max(0, mPlayer.getContentPosition());
        }
    }

    private void clearStartPosition() {
        mStartAutoPlay = false;
        mStartWindow = C.INDEX_UNSET;
        mStartPosition = C.TIME_UNSET;
    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private void setVideoPlayerNotificationState() {

        if (mPlayerNotificationAdapter != null) { // Some pages don't have video on them

            if (!isCurrentPage()) {
                mPlayerNotificationAdapter.setNotificationActive(false);
                return;
            }

            final int playbackState = mPlayer.getPlaybackState();

            if (Player.STATE_IDLE == playbackState) {
                mPlayerNotificationAdapter.setNotificationActive(false);
            } else if (Player.STATE_BUFFERING == playbackState) {
                mPlayerNotificationAdapter.setNotificationActive(false);
            } else if (Player.STATE_READY == playbackState) {
                mPlayerNotificationAdapter.setNotificationActive(true);
            } else if (Player.STATE_ENDED == playbackState) {
                mPlayerNotificationAdapter.setNotificationActive(true);
            } else {
                Log.w(TAG, "setVideoPlayerNotificationState: Unknown Player state! ["+ playbackState +"]");
            }

        }

    }

    private class PlayerEventListener extends Player.DefaultEventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.d(TAG, "onPlayerStateChanged() called with: playWhenReady = [" + playWhenReady + "]/[" + mPlayer.getPlaybackState() + "], playbackState = [" + playbackState + "][" + mPlayer.getPlayWhenReady() + "]");
            setVideoPlayerNotificationState();
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
            if (mPlayer.getPlaybackError() != null) {
                // The user has performed a seek whilst in the error state. Update the resume position so
                // that if the user then retries, playback resumes from the position to which they seeked.
                updateStartPosition();
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition();
                initializePlayer();
            } else {
                updateStartPosition();
            }
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.d(TAG, "onTracksChanged() called with: trackGroups = [" + trackGroups + "], trackSelections = [" + trackSelections + "]");
        }

    }

    private class PlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {
        @Override
        public Pair<Integer, String> getErrorMessage(ExoPlaybackException e) {
            String errorString = getString(R.string.msg_err_occurred);
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                Exception cause = e.getRendererException();
                if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                            (MediaCodecRenderer.DecoderInitializationException) cause;
                    if (decoderInitializationException.decoderName == null) {
                        if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                            errorString = getString(R.string.msg_err_querying_decoders);
                        } else if (decoderInitializationException.secureDecoderRequired) {
                            errorString = getString(
                                    R.string.msg_err_no_secure_decoder//,
                                    //decoderInitializationException.mimeType
                            );
                        } else {
                            errorString = getString(
                                    R.string.msg_err_no_decoder//,
                                    //decoderInitializationException.mimeType
                            );
                        }
                    } else {
                        errorString = getString(
                                R.string.msg_err_instantiate_decoder//,
                                //decoderInitializationException.decoderName
                        );
                    }
                }
            }
            return Pair.create(0, errorString);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    private void createTextView(@NonNull final View rootView) {
        // Display the recipe step information ...
        TextView tvStepDesc = rootView.findViewById(R.id.recipeinfo_step_description);
        if (tvStepDesc != null) {
            tvStepDesc.setText(mStepData.getDescription());
        }
    }

    private void createImageView(@NonNull final View rootView, String url) {

        final ImageView ivThumbnail = rootView.findViewById(R.id.recipeinfo_step_video_thumbnail);

        if (AppUtil.isEmpty(url)) {
            ivThumbnail.setImageResource(R.drawable.ic_baseline_fastfood_24px);
        } else {
            Picasso.get()
                    .load(mStepData.getURLThumbnail())
                    .placeholder(R.drawable.ic_baseline_fastfood_24px)
                    .into(
                            ivThumbnail,
                            new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    Log.d(TAG, "Picasso:onSuccess: image fetched");
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e(TAG, "Picasso:onError: ", e);
                                    ivThumbnail.setImageResource(R.drawable.ic_baseline_fastfood_24px);
                                }
                            }
                    );
        }
        mMediaURL = null;
    }

    private void createVideoView(@NonNull final View rootView, String url) {
        // If we have a video to show, show it ...
        mPlayerView = rootView.findViewById(R.id.recipeinfo_step_video_player);
        mMediaURL = url;
    }

    /**
     * @return A valid {@link String} representation of a {@link java.net.URL}, or the
     * {@link String} "{@code IMAGE_MEDIA}", if the {@link java.net.URL} is not valid.
     */
    private String getURLToUse() {
        // Check video URL first, if empty, try thumbnail URL,
        // if empty, null out url so player doesn't show ...
        String url = null; // Null is for using a placeholder image instead
        try {
            // See if the Video URL is a valid video/audio ...
            boolean[] flagsContentType = AppUtil.getContentTypeInfo(mStepData.getURLVideo());
            if (flagsContentType[0] || flagsContentType[1]) { // Video/Audio
                url = mStepData.getURLVideo();
            } else {
                // Video URL was a bust, lets try the thumbnail URL ...
                flagsContentType = AppUtil.getContentTypeInfo(mStepData.getURLThumbnail());
                if (flagsContentType[0] || flagsContentType[1]) { // Video/Audio
                    url = mStepData.getURLThumbnail();
                } else if (flagsContentType[2]) { // Image
                    // Thumbnail URL actually points to an Image, not a Video/Audio ...
                    url = MEDIA_IMAGE_NOT_VIDEO; // set flag for below code to load thumbnail image
                }
            }
        } catch (IOException ioe) {
            Log.e(TAG, "getUrlToUse: Error while determining content types! ["+ mStepData.getURLVideo() +"]["+ mStepData.getURLThumbnail() +"]", ioe);
            url = null;
        }
        Log.i(TAG, "getURLToUse: ["+ url +"]");
        return url;
    }

    // https://medium.com/google-exoplayer/playback-notifications-with-exoplayer-a2f1a18cf93b
    private class PlayerNotificationAdapter implements MediaDescriptionAdapter {

        private SimpleExoPlayer aPlayer;
        private PlayerNotificationManager aPlayerNotifyMgr;

        private int aStepDataId;
        private String aNotifyTitle;
        private String aNotifyText;

        private Bitmap icon;

        private boolean flagActive = false;

        PlayerNotificationAdapter(final SimpleExoPlayer player, final int idStepData, final String title, final String text) {

            if (player == null)
                throw new IllegalArgumentException("Expected a non-null SimpleExoPlayer reference!");
            aPlayer = player;

            if (idStepData <= 0)
                throw new IllegalArgumentException("Expected a valid StepData id value! ["+ idStepData +"]");
            aStepDataId = idStepData;

            if (title == null)
                throw new IllegalArgumentException("Expected a non-null title reference!");
            aNotifyTitle = title;

            if (text == null)
                throw new IllegalArgumentException("Expected a non-null text reference!");
            aNotifyText = text;

            if (getContext() == null)
                throw new IllegalStateException("Expected a non-null getResources() value!");
            icon = BitmapFactory.decodeResource(
                    getContext().getApplicationContext().getResources(),
                    R.drawable.ic_baseline_fastfood_24px
            );

        }

        void setManager(
                final PlayerNotificationManager playerNotifyMgr) {
            if (playerNotifyMgr == null)
                throw new IllegalArgumentException("Expected a non-null PlayerNotificationManager reference!");
            this.aPlayerNotifyMgr = playerNotifyMgr;
        }

        /**
         * <p>Will call {@link #setNotificationActive(boolean)},
         * passing in the {@link #isCurrentPage()} result.</p>
         */
        void setNotificationState() {
            setNotificationActive( isCurrentPage() );
        }

        /**
         * <p>Will call {@link PlayerNotificationManager#setPlayer(Player)}, to tell it
         * whether it should show/hide any notifications for the {@link SimpleExoPlayer}
         * that it manages.</p>
         * @param active Whether the {@link PlayerNotificationManager} should be showing
         *               any notifications, or not.
         */
        synchronized void setNotificationActive(final boolean active) {

            if (aPlayerNotifyMgr == null)
                throw new IllegalArgumentException("Expected a non-null PlayerNotificationManager reference!");

            if ((active && flagActive) || (!active) && (!flagActive)) {
                return;
            }

            flagActive = active;

            // PlayerNotificationManager.NotificationBroadcastReceiver.onReceive(Context, Intent)
            // does NOT get called all the time, just some times!
            if (flagActive) {
                aPlayerNotifyMgr.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
                    @Override
                    public void onNotificationStarted(int notificationId, Notification notification) {
                        Log.d(TAG, "onNotificationStarted() called with: notificationId = [" + notificationId + "], notification = [" + notification + "]");
                    }

                    @Override
                    public void onNotificationCancelled(int notificationId) {
                        Log.d(TAG, "onNotificationCancelled() called with: notificationId = [" + notificationId + "]");
                    }
                });
            } else {
                aPlayerNotifyMgr.setNotificationListener(null);
            }

            // Setting the player to null/not-null is what actually makes the notification hide/show
            aPlayerNotifyMgr.setPlayer(active ? aPlayer : null);
        }

        @Override
        public String getCurrentContentTitle(Player player) {
            return aNotifyTitle + " " + aStepDataId;
        }

        @Nullable
        @Override
        public String getCurrentContentText(Player player) {
            return aNotifyText;
        }

        @Nullable
        @Override
        public Bitmap getCurrentLargeIcon(
                Player player, PlayerNotificationManager.BitmapCallback callback) {
            return icon;
        }

        @Nullable
        @Override
        public PendingIntent createCurrentContentIntent(Player player) {
            return null;
//            return PendingIntent.getActivity(
//                    getContext(), 0, new Intent(getContext(), getContext().getClass()), 0);
        }

    }

}
