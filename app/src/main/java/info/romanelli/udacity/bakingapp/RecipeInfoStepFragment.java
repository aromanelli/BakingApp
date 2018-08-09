package info.romanelli.udacity.bakingapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
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
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import info.romanelli.udacity.bakingapp.data.StepData;

/**
 * A fragment representing a single RecipeInfo detail screen.
 * This fragment is either contained in a
 * {@link RecipeInfoActivity}({@link RecipeInfoRecyclerViewAdapter})
 * in two-pane mode (on tablets), or a {@link RecipeInfoStepActivity}
 * on handsets.
 */
public class RecipeInfoStepFragment extends Fragment implements PlaybackPreparer, PlayerControlView.VisibilityListener {

    // REVIEWER: Parts of the code below was re-purposed from ...
    // https://github.com/google/ExoPlayer/blob/release-v2/demos/main/src/main/java/com/google/android/exoplayer2/demo/PlayerActivity.java
    //    ... and ...
    // AdvancedAndroid_ClassicalMusicQuiz:origin/TMED.06-Solution-AddMediaButtonReceiver
    // ... as they are boiler-plate type of code, and I have been struggling with
    // getting the lifecycle of ExoPlayers's in Fragments with ViewPagers to work correctly.

    private static final String TAG = RecipeInfoStepFragment.class.getSimpleName();

    private static final String CHANNEL_ID = "Video Player Notifications";

    // Saved instance state keys.
    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";

    static private MediaSessionCompat MEDIA_SESSION;
    static private PlaybackStateCompat.Builder PLAYBACK_STATE_BUILDER;

    private StepData mStepData;

    private String mediaURL;
    private String notifyTitle;
    private String notifyText;
    private NotificationManager notifyMgr;

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private MediaSource mediaSource;
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;

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

            notifyTitle =
                    ViewModelProviders.of(getActivity()).get(DataViewModel.class).getRecipeData().getName();
            notifyText = mStepData.getShortDescription();

        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recipeinfo_step_content, container, false);

        createVideoView(rootView);

        // Display the recipe step information ...
        TextView tvStepDesc = rootView.findViewById(R.id.recipeinfo_step_description);
        if (tvStepDesc != null) {
            tvStepDesc.setText(mStepData.getDescription());
        }

        if (savedInstanceState != null) {
            trackSelectorParameters = savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            startWindow = savedInstanceState.getInt(KEY_WINDOW);
            startPosition = savedInstanceState.getLong(KEY_POSITION);
        } else {
            trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
            clearStartPosition();
        }

        return rootView;
    }

//    @Override
//    public void onNewIntent(Intent intent) {
//        releasePlayer();
//        clearStartPosition();
//        setIntent(intent);
//    }

    @Override
    public void onStart() { // Called when the Fragment is visible to the user.
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() { // Called when the fragment is visible to the user and actively running.
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
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
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onDestroyView() { // Shutdown3
        super.onDestroyView();
    }

    @Override
    public void onDestroy() { // Shutdown4
        super.onDestroy();

        // TODO AOR When to clean up the media session to not lead? Its static, all frags use it. In parent activity?
//        // More than one frag can be destroyed at once, or
//        // going from step fragment to ingredients fragment
//        if (MEDIA_SESSION != null) {
//            MEDIA_SESSION.setActive(false);
//            MEDIA_SESSION = null;
//            Log.d(TAG, "onDestroy: MEDIA_SESSION has been nulled.");
//        }

    }

    @Override
    public void onDetach() { // Shutdown5
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        updateTrackSelectorParameters();
        updateStartPosition();
        outState.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters);
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);
    }

    @Override
    public void preparePlayback() {
        initializePlayer();
    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    private void initializePlayer() {

        // Even though media session is static var, this must happen
        // each time, as it also assign non-static member vars!
        initMediaSession();

        // Don't init player if there's no media to show it ...
        if (mediaURL != null) {

            if (player == null) {

                trackSelector = new DefaultTrackSelector();
                trackSelector.setParameters(trackSelectorParameters);

                player = ExoPlayerFactory.newSimpleInstance(
                        new DefaultRenderersFactory(getContext()),
                        trackSelector,
                        new DefaultLoadControl()
                );

                player.addListener(new PlayerEventListener());
                player.setPlayWhenReady(startAutoPlay);
                playerView.setPlayer(player);
                playerView.setPlaybackPreparer(this);

                // Prepare the MediaSource.
                String userAgent = Util.getUserAgent(
                        getContext(),
                        "RecipeInfo_" + RecipeInfoStepFragment.class.getSimpleName()
                );

                Log.d(TAG, "initializePlayer: mediaURL: [" + mediaURL + "]");
                if (getContext() == null) throw new IllegalStateException("Expected a non-null Context reference!");
                mediaSource = new ExtractorMediaSource.Factory(
                        new DefaultDataSourceFactory(getContext(), userAgent)
                )
                        .setExtractorsFactory(
                                new DefaultExtractorsFactory()
                        )
                        .createMediaSource(Uri.parse(mediaURL));

            }

            boolean haveStartPosition = startWindow != C.INDEX_UNSET;
            if (haveStartPosition) {
                player.seekTo(startWindow, startPosition);
            }
            player.prepare(mediaSource, !haveStartPosition, false);
        }

    }

    private void releasePlayer() {
        if (player != null) {
            updateTrackSelectorParameters();
            updateStartPosition();
            player.release(); // No player.stop() ?
            player = null;
            mediaSource = null;
            trackSelector = null;
        }
    }

    private void updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector.getParameters();
        }
    }

    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
    }

    private void clearStartPosition() {
        startAutoPlay = false;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
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

    private class PlayerEventListener extends Player.DefaultEventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if ((playbackState == Player.STATE_READY) && playWhenReady) {
                PLAYBACK_STATE_BUILDER.setState(
                        PlaybackStateCompat.STATE_PLAYING,
                        player.getCurrentPosition(),
                        1f
                );
            } else if ((playbackState == Player.STATE_READY)) {
                PLAYBACK_STATE_BUILDER.setState(
                        PlaybackStateCompat.STATE_PAUSED,
                        player.getCurrentPosition(),
                        1f
                );
            }
            MEDIA_SESSION.setPlaybackState(PLAYBACK_STATE_BUILDER.build());
            showNotification(PLAYBACK_STATE_BUILDER.build());
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
            if (player.getPlaybackError() != null) {
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

    private void createVideoView(@NonNull final View rootView) {

        String url = getURLToUse();

        // If we have a video to show, show it ...
        playerView = rootView.findViewById(R.id.recipeinfo_step_video);
        final ImageView ivThumbnail = rootView.findViewById(R.id.recipeinfo_step_video_thumbnail);
        if ((! AppUtil.isEmpty(url)) && (! "IMAGE_MEDIA".equals(url))) {
            ivThumbnail.setVisibility(View.GONE);
            // initPlayer(...) was called here originally
            playerView.setControllerVisibilityListener(this);
            playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
        } else {
            // Hide play video player ...
            playerView.setVisibility(View.GONE);
            // If we have an image url, display it instead of the default/placeholder image ...
            if ("IMAGE_MEDIA".equals(url)) { // Legit url to an image, not null, etc.
                url = null;
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
        }
        mediaURL = url;
    }

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
                    url = "IMAGE_MEDIA"; // set flag for below code to load thumbnail image
                }
            }
        } catch (IOException ioe) {
            Log.e(TAG, "getUrlToUse: Error while determining content types! ["+ mStepData.getURLVideo() +"]["+ mStepData.getURLThumbnail() +"]", ioe);
            url = null;
        }
        return url;
    }

    /**
     * Initializes the Media Session to be enabled with media buttons,
     * transport controls, callbacks and media controller.
     */
    private void initMediaSession() {
        Log.d(TAG, "initMediaSession() called\n\t" + player + "\n\t" + MEDIA_SESSION + "\n\t" + this + "\n\t" + getContext());

        if (MEDIA_SESSION != null) {
            return;
        }

        // Create a MediaSessionCompat.
        if (getContext() == null) throw new IllegalStateException("Expected a non-null Context reference!");
        MEDIA_SESSION = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        MEDIA_SESSION.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        MEDIA_SESSION.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        PLAYBACK_STATE_BUILDER = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        MEDIA_SESSION.setPlaybackState(PLAYBACK_STATE_BUILDER.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        MEDIA_SESSION.setCallback(new SessionCallback());

        // Start the Media Session since the activity is active.
        MEDIA_SESSION.setActive(true);
    }

    /**
     * Shows Media Style notification, with actions that
     * depend on the current MediaSession PlaybackState.
     * @param state The PlaybackState of the MediaSession.
     */
    private void showNotification(PlaybackStateCompat state) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(); // notifyMgr assigned inside of this method
        } else {
            setNotificationManagerRef();
        }

        if (getContext() == null) throw new IllegalStateException("Expected a non-null Context reference!");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID);

        int icon;
        String play_pause;
        if(state.getState() == PlaybackStateCompat.STATE_PLAYING){
            icon = R.drawable.exo_controls_pause;
            play_pause = getString(R.string.exo_controls_pause_description);
        } else {
            icon = R.drawable.exo_controls_play;
            play_pause = getString(R.string.exo_controls_play_description);
        }

        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon,
                play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        getContext(),
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
        );

        NotificationCompat.Action restartAction = new android.support.v4.app.NotificationCompat.Action(
                R.drawable.exo_controls_previous,
                getString(R.string.exo_controls_previous_description),
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        getContext(),
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
        );

        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                getContext(), 0, new Intent(getContext(), getContext().getClass()), 0);

        builder.setContentTitle(notifyTitle)
                .setContentText(notifyText)
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_baseline_fastfood_24px)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .setStyle(
                        new android.support.v4.media.app.NotificationCompat.MediaStyle()
                                .setMediaSession(MEDIA_SESSION.getSessionToken())
                                .setShowActionsInCompactView(0, 1)
                );

        notifyMgr.notify(0, builder.build());
    }

    private void setNotificationManagerRef() {
        if (getContext() == null) throw new IllegalStateException("Expected a non-null Context reference!");
        notifyMgr = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notifyMgr == null)
            throw new IllegalStateException("Expected a non-null NotificationManager reference!");
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {

        setNotificationManagerRef();

        NotificationChannel mChannel = new NotificationChannel(
                CHANNEL_ID,
                // The user-visible name of the channel ...
                getString(R.string.notify_channel_name),
                NotificationManager.IMPORTANCE_LOW
        );
        // The user-visible description of the channel ...
        mChannel.setDescription(
                getString(R.string.notify_channel_desc)
        );
        mChannel.setShowBadge(false);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notifyMgr.createNotificationChannel(mChannel);
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class SessionCallback extends MediaSessionCompat.Callback {
        final private String TAG = RecipeInfoStepFragment.TAG + "|" + this;
        @Override
        public void onPlay() {
            Log.d(TAG, "onPlay() called");
            player.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            Log.d(TAG, "onPause() called");
            player.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            Log.d(TAG, "onSkipToPrevious() called");
            player.seekTo(0);
        }
    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {
        private static final String TAG = RecipeInfoStepFragment.TAG + "|" + MediaReceiver.class.getSimpleName();
        public MediaReceiver() {
            super();
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive() called with: context = [" + context + "], intent = [" + intent + "]");
            MediaButtonReceiver.handleIntent(MEDIA_SESSION, intent);
        }
    }

}
