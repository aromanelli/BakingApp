package info.romanelli.udacity.bakingapp;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.PlayerView;
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
public class RecipeInfoStepFragment extends Fragment {
    // https://developer.android.com/guide/components/fragments#Lifecycle

    final static private String TAG = RecipeInfoStepFragment.class.getSimpleName();

    private StepData mStepData;
    private VideoPlayerManager mVideoPlayerMgr; // TODO AOR Need to add Bundle/rotation support for this member

    private FragmentActivity mActivity;
    private int mIdFragment = Integer.MIN_VALUE;

    /**
     * Mandatory empty constructor for the fragment manager to
     * instantiate the fragment (e.g. upon screen orientation changes).
     */
    public RecipeInfoStepFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mActivity = this.getActivity();
        if (mActivity == null) {
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

            Could use a Map<fragment, StepData> to get around the problem.
             */

            mStepData = getArguments().getParcelable(MainActivity.KEY_STEP_DATA);
            if (mStepData == null)
                throw new IllegalStateException("Expected a " + StepData.class.getSimpleName() + " reference!");

            mIdFragment = getArguments().getInt(MainActivity.KEY_INDEX_STEP_DATA);
            if (mIdFragment == Integer.MIN_VALUE)
                throw new IllegalStateException("Expected a fragment identifier value!");
        }

        // Needed for when fragment is in a solo mActivity
        mActivity.setTitle(
                ViewModelProviders.of(mActivity).get(DataViewModel.class)
                        .getRecipeData().getName()
        );

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recipeinfo_step_content, container, false);

        //////////////////////////////////////////////////////////////////////
        // REVIEWER: The thumbnail URL from the json is a mp4, not an png/jpg,
        // so assuming we're always just showing a video, or nothing, and not
        // an image, so not using ImageView.
        //////////////////////////////////////////////////////////////////////

        // Check video URL first, if empty, try thumbnail URL,
        // if empty, null out url so player doesn't show ...

        String urlToUse = null; // Null is for using a placeholder image instead
        try {
            // See if the Video URL is a valid video/audio ...
            boolean[] flagsContentType = AppUtil.getContentTypeInfo(mStepData.getURLVideo());
            if (flagsContentType[0] || flagsContentType[1]) { // Video/Audio
                urlToUse = mStepData.getURLVideo();
            } else {
                // Video URL was a bust, lets try the thumbnail URL ...
                flagsContentType = AppUtil.getContentTypeInfo(mStepData.getURLThumbnail());
                if (flagsContentType[0] || flagsContentType[1]) { // Video/Audio
                    urlToUse = mStepData.getURLThumbnail();
                } else if (flagsContentType[2]) { // Image
                    // Thumbnail URL actually points to an Image, not a Video/Audio ...
                    urlToUse = "IMAGE"; // set flag for below code to load thumbnail image
                }
            }
        } catch (IOException ioe) {
            Log.e(TAG, "onCreateView: Error while determining content types! ["+ mStepData.getURLVideo() +"]["+ mStepData.getURLThumbnail() +"]", ioe);
            urlToUse = null;
        }

        // If we have a video to show, show it ...
        PlayerView playerView = rootView.findViewById(R.id.recipeinfo_step_video);
        final ImageView ivThumbnail = rootView.findViewById(R.id.recipeinfo_step_video_thumbnail);
        if ((! AppUtil.isEmpty(urlToUse)) && (! "IMAGE".equals(urlToUse))) {
            ivThumbnail.setVisibility(View.GONE);
            mVideoPlayerMgr = new VideoPlayerManager();
            mVideoPlayerMgr.initPlayer(
                    mActivity,
                    playerView,
                    urlToUse,
                    ViewModelProviders.of(mActivity).get(DataViewModel.class).getRecipeData().getName(),
                    mStepData.getShortDescription(),
                    Integer.MIN_VALUE
            );
        } else {
            // Hide play video player ...
            playerView.setVisibility(View.GONE);
            // If we have an image url, display it instead of the default/placeholder image ...
            if ("IMAGE".equals(urlToUse)) { // Legit url to an image, not null, etc.
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

        // Display the recipe step information ...
        TextView tvStepDesc = rootView.findViewById(R.id.recipeinfo_step_description);
        if (tvStepDesc != null) {
            tvStepDesc.setText(mStepData.getDescription());
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mVideoPlayerMgr != null) {
            Log.d(TAG, "onDestroyView: Calling VideoPlayerManager.releasePlayer()! " + this);
            mVideoPlayerMgr.releasePlayer();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoPlayerEvent(StepDataEvent event) {
        if (event.getType().equals(StepDataEvent.Type.SELECTED)) {
            stopPlayer();
            // TODO AOR Handle notification area on switched fragment
        }
    }

    public void stopPlayer() {
        if (mVideoPlayerMgr != null) {
            mVideoPlayerMgr.stopPlayer();
        } else {
            Log.d(TAG, "stopPlayer: No video player manager to request stop from.");
        }
    }

}
