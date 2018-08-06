package info.romanelli.udacity.bakingapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

@SuppressWarnings("WeakerAccess")
public class VideoPlayerManager implements Player.EventListener {

    final static private String TAG = VideoPlayerManager.class.getSimpleName();

    // REVIEWER: Parts of the code below was re-purposed from
    // AdvancedAndroid_ClassicalMusicQuiz:origin/TMED.06-Solution-AddMediaButtonReceiver

    private Context mContext;

    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private NotificationManager mNotificationManager;

    private String mNotifyTitle;
    private String mNotifyText;


    VideoPlayerManager() {
        super();
    }

    /**
     * @param context Used for various video player setup api calls that require a {@link Context}.
     * @param playerView The {@link PlayerView} to show the video on/in.
     * @param uri A {@link String} that will be passed to {@link Uri#parse(String)} to determine the {@link Uri} of the video to show.
     * @param artwork_drawable_id if {@code >= 1}, value will be used to find a {@link Bitmap}, to set as the player default artwork.
     */
    public void initPlayer(final Context context,
                           final PlayerView playerView,
                           final String uri,
                           final String notifyTitle,
                           final String notifyText,
                           final int artwork_drawable_id) {
        initPlayer(
                context,
                playerView,
                Uri.parse(uri),
                notifyTitle,
                notifyText,
                ((artwork_drawable_id >= 1) ?
                        BitmapFactory.decodeResource(context.getResources(), artwork_drawable_id) :
                        null
                )
        );
    }

    /**
     * @param context Used for various video player setup api calls that require a {@link Context}.
     * @param playerView The {@link PlayerView} to show the video on/in.
     * @param mediaUri the {@link Uri} of the video to show.
     * @param artwork a {@link Bitmap}, to set as the player default artwork.
     */
    public void initPlayer(final Context context,
                    final PlayerView playerView,
                    final Uri mediaUri,
                    final String notifyTitle,
                    final String notifyText,
                    final Bitmap artwork) {
        Log.d(TAG, "initPlayer() called with: context = [" + context + "], playerView = [" + playerView + "], mediaUri = [" + mediaUri + "], artwork = [" + artwork + "]");

        try {

            if (mExoPlayer == null) {

                mContext = context;
                mNotifyTitle = notifyTitle;
                mNotifyText = notifyText;

                mPlayerView = playerView;

                if (artwork != null) {
                    mPlayerView.setDefaultArtwork(artwork);
                }

                initMediaSession();

                // Create an instance of the ExoPlayer.
                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();

                mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                        new DefaultRenderersFactory(mContext),
                        trackSelector,
                        loadControl
                );
                Log.d(TAG, "initPlayer() called.\n\t" + mExoPlayer + "\n\t" + mMediaSession + "\n\t" + this + "\n\t" + mContext);

                mPlayerView.setPlayer(mExoPlayer);

                // Set the ExoPlayer.EventListener to this activity.
                mExoPlayer.addListener(this);

                // Prepare the MediaSource.
                String userAgent = Util.getUserAgent(
                        mContext,
                        "RecipeInfo_" + VideoPlayerManager.class.getSimpleName()
                );

                MediaSource mediaSource = new ExtractorMediaSource.Factory(
                        new DefaultDataSourceFactory(mContext, userAgent))
                        .setExtractorsFactory(
                                new DefaultExtractorsFactory()
                        )
                        .createMediaSource(mediaUri);

                mExoPlayer.prepare(mediaSource);
                // When in tablet view, all frags instantiated up front, so
                // don't want to play until a frag is visible to the user.
                // In phone view, single frag shows right after instantiated.
                mExoPlayer.setPlayWhenReady(false);

            } else {
                throw new IllegalStateException("Already initialized!  Call releasePlayer().  [" + playerView + "][" + mediaUri + "][" + mContext + "][]");
            }

        } catch (Throwable t) {
            Log.e(TAG, "initPlayer: ", t);
            throw t;
        }

    }

    /**
     * Initializes the Media Session to be enabled with media buttons,
     * transport controls, callbacks and media controller.
     */
    private void initMediaSession() {

        // REVIEWER: This method taken verbatim from Classical Music Quiz code (boilerplate code)

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(mContext, TAG);
        Log.d(TAG, "initMediaSession() called.\n\t" + mExoPlayer + "\n\t" + mMediaSession + "\n\t" + this + "\n\t" + mContext);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new SessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }

    public void stopPlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
        } else {
            Log.d(TAG, "stopPlayer: No video player to request stop from.");
        }
    }

    public void releasePlayer() {
        Log.d(TAG, "releasePlayer() called.\n\t" + mExoPlayer + "\n\t" + mMediaSession + "\n\t" + this);

        if (mExoPlayer != null) {
            Log.d(TAG, "releasePlayer: Releasing player!\n\t" + mExoPlayer + "\n\t" + mMediaSession + "\n\t" + this);

            mNotificationManager.cancelAll(); // TODO AOR Must be called when rotating tablet from hori to verti

            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;

            if (mMediaSession != null) {
                mMediaSession.setActive(false);
                mMediaSession = null;
            }

            mContext = null;

        } else {
            throw new IllegalStateException("Must initialize the player before releasing it!");
        }
    }

    /**
     * Shows Media Style notification, with actions that
     * depend on the current MediaSession PlaybackState.
     * @param state The PlaybackState of the MediaSession.
     */
    private void showNotification(PlaybackStateCompat state) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(); // mNotificationManager assigned inside of this method
        } else {
            setNotificationManagerRef();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID);

        int icon;
        String play_pause;
        if(state.getState() == PlaybackStateCompat.STATE_PLAYING){
            icon = R.drawable.exo_controls_pause;
            play_pause = "PAUSE"; // getString(R.string.pause); // TODO AOR CODE THIS
        } else {
            icon = R.drawable.exo_controls_play;
            play_pause = "PLAY"; // getString(R.string.play); // TODO AOR CODE THIS
        }

        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon,
                play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mContext,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
        );

        NotificationCompat.Action restartAction = new android.support.v4.app.NotificationCompat.Action(
                R.drawable.exo_controls_previous,
                "RESTART", // getString(R.string.restart), // TODO AOR CODE THIS
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mContext,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
        );

        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                mContext, 0, new Intent(mContext, mContext.getClass()), 0);

        builder.setContentTitle(mNotifyTitle)
                .setContentText(mNotifyText)
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_baseline_fastfood_24px)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .setStyle(
                        new MediaStyle()
                                .setMediaSession(mMediaSession.getSessionToken())
                                .setShowActionsInCompactView(0, 1)
                );

        mNotificationManager.notify(0, builder.build());
    }

    private void setNotificationManagerRef() {
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager == null)
            throw new IllegalStateException("Expected a non-null NotificationManager reference!");
    }

    final static private String CHANNEL_ID = "Video Player Notifications";

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {

        setNotificationManagerRef();

        CharSequence name = mContext.getString(R.string.app_name) + " Media Playback"; // TODO AOR CODE THIS
        NotificationChannel mChannel = new NotificationChannel(
                CHANNEL_ID,
                name, // The user-visible name of the channel
                NotificationManager.IMPORTANCE_LOW
        );
        mChannel.setDescription("Media Playback Controls"); // The user-visible description of the channel // TODO AOR CODE THIS
        mChannel.setShowBadge(false);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        mNotificationManager.createNotificationChannel(mChannel);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
        Log.d(TAG, "onTimelineChanged() called with: timeline = [" + timeline + "], manifest = [" + manifest + "], reason = [" + reason + "]");
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.d(TAG, "onTracksChanged() called with: trackGroups = [" + trackGroups + "], trackSelections = [" + trackSelections + "]");
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        Log.d(TAG, "onLoadingChanged() called with: isLoading = [" + isLoading + "]");
    }

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the
     * MediaSession PlayBackState to keep in sync, and post the media notification.
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.d(TAG, "onPlayerStateChanged() called with: playWhenReady = [" + playWhenReady + "], playbackState = [" + playbackState + "]");
        if (mMediaSession != null) { // if releasePlayer() called, it does a stop(), which triggers this method one more time.
            if ((playbackState == Player.STATE_READY) && playWhenReady) {
                mStateBuilder.setState(
                        PlaybackStateCompat.STATE_PLAYING,
                        mExoPlayer.getCurrentPosition(),
                        1f
                );
            } else if ((playbackState == Player.STATE_READY)) {
                mStateBuilder.setState(
                        PlaybackStateCompat.STATE_PAUSED,
                        mExoPlayer.getCurrentPosition(),
                        0f
                );
            }
            mMediaSession.setPlaybackState(mStateBuilder.build());
            showNotification(mStateBuilder.build());
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
        Log.d(TAG, "onRepeatModeChanged() called with: repeatMode = [" + repeatMode + "]");
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        Log.d(TAG, "onShuffleModeEnabledChanged() called with: shuffleModeEnabled = [" + shuffleModeEnabled + "]");
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.d(TAG, "onPlayerError() called with: error = [" + error + "]");
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
        Log.d(TAG, "onPositionDiscontinuity() called with: reason = [" + reason + "]");
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        Log.d(TAG, "onPlaybackParametersChanged() called with: playbackParameters = [" + playbackParameters + "]");
    }

    @Override
    public void onSeekProcessed() {
        Log.d(TAG, "onSeekProcessed() called");
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class SessionCallback extends MediaSessionCompat.Callback {
        final private String TAG = VideoPlayerManager.TAG + "|" + this;
        @Override
        public void onPlay() {
            Log.d(TAG, "onPlay() called");
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            Log.d(TAG, "onPause() called");
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            Log.d(TAG, "onSkipToPrevious() called");
            mExoPlayer.seekTo(0);
        }
    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {
        // TODO AOR Needed?  Taken from AdvancedAndroid_ClassicalMusicQuiz:TMED.06-Solution
        final static private String TAG = VideoPlayerManager.TAG + "|" + MediaReceiver.class.getSimpleName();
        public MediaReceiver() {
            super();
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive() called with: context = [" + context + "], intent = [" + intent + "]");
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

}
