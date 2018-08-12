package info.romanelli.udacity.bakingapp;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.romanelli.udacity.bakingapp.data.DataManager;
import info.romanelli.udacity.bakingapp.data.RecipeData;
import info.romanelli.udacity.bakingapp.network.NetUtil;
import info.romanelli.udacity.bakingapp.network.RecipesFetcher;

public class MainActivity
        extends AppCompatActivity
        implements RecipesRecyclerViewAdapter.OnClickHandler, RecipesFetcher.Listener {

    final static private String TAG = MainActivity.class.getSimpleName();

    public static final String KEY_RECIPE_DATA = "key_recipe_data";
    public static final String KEY_BUNDLE_RV_ITEM_POS = "key_bundle_recyclerview_item_position";
    public static final String KEY_STEP_DATA = "key_step_data";
    public static final String KEY_STEP_DATA_ID = "key_index_step_data";

    public static final String CHANNEL_ID = "Video Player Notifications";

    @BindView(R.id.rv_recipes)
    RecyclerView mViewRecipes;

    private RecipesRecyclerViewAdapter mAdapterRecipes;

    private int mIndexFirstVisibleItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        DataManager.init(this);

        mViewRecipes.setHasFixedSize(true);

        // Determine how many columns of cards should be displayed.  Uses view_sizing.xml and orientation.
        int numberOfCardCols = getResources().getInteger(R.integer.recipe_view_num_of_cols);
        if (Configuration.ORIENTATION_LANDSCAPE == this.getResources().getConfiguration().orientation) {
            numberOfCardCols++;
        }
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfCardCols);
        mViewRecipes.setLayoutManager(layoutManager);

        mAdapterRecipes = new RecipesRecyclerViewAdapter(this);
        mViewRecipes.setAdapter(mAdapterRecipes);

        // If first-time call, fetched movie info data ...
        if (savedInstanceState == null ) {

            // Remove the previous selection ingredients from the widget(s)
            broadcastToWidgets(null);

            if (NetUtil.isOnline(this)) {
                mIndexFirstVisibleItem =0;
                RecipesFetcher.fetchRecipes(this, this);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.msg_cant_offline))
                        .setPositiveButton(
                                R.string.msg_close,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finishAffinity();
                                    }
                                }
                        )
                        .show();
            }
        } else {
            mIndexFirstVisibleItem = savedInstanceState.getInt(KEY_BUNDLE_RV_ITEM_POS);
            fetchedRecipes(
                    ViewModelProviders.of(this).get(DataViewModel.class).getRecipes()
            );
        }

        // According to docs, safe to recreate same channel more than once
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        // https://developer.android.com/training/notify-user/channels
        NotificationChannel notifyChannel = new NotificationChannel(
                CHANNEL_ID,
                // The user-visible name of the channel ...
                getString(R.string.notify_channel_name),
                NotificationManager.IMPORTANCE_LOW
        );
        // The user-visible description of the channel ...
        notifyChannel.setDescription(
                getString(R.string.notify_channel_desc)
        );
        notifyChannel.setShowBadge(false);
        notifyChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        getNotificationManager().createNotificationChannel(notifyChannel);
    }

    @NonNull
    private NotificationManager getNotificationManager() {
        NotificationManager notifyMgr =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notifyMgr == null)
            throw new IllegalStateException("Expected a non-null NotificationManager reference!");
        return notifyMgr;
    }

    @Override
    public void fetchedRecipes(List<RecipeData> recipes) {
        Log.d(TAG, "fetchedRecipes() called with: recipes = [" + recipes + "]");

        if (recipes == null) {
            AppUtil.showToast(this, getString(R.string.msg_err_fetching_recipes), false);

            mIndexFirstVisibleItem = 0;

            // A net fetching error could end up causing a
            // null to be passed in, and we don't want nulls.
            recipes = new ArrayList<>(0);
        }

        ViewModelProviders.of(this).get(DataViewModel.class).setRecipes(recipes);

        mAdapterRecipes.setData(recipes);
        mViewRecipes.getLayoutManager().scrollToPosition(mIndexFirstVisibleItem);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(
                KEY_BUNDLE_RV_ITEM_POS,
                ((GridLayoutManager) mViewRecipes.getLayoutManager())
                        .findFirstCompletelyVisibleItemPosition()
        );
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRecipeClick(RecipeData recipe, ImageView ivPoster) {
        ViewModelProviders.of(this).get(DataViewModel.class).setRecipeData(recipe);

        startActivity(
                new Intent(MainActivity.this, RecipeInfoActivity.class)
        );

        broadcastToWidgets(recipe);

    }

    private void broadcastToWidgets(final RecipeData recipe) {
        Intent intent = new Intent(this, RecipeInfoAppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        // REVIEWER: This seems so straightforward, but I never saw any mention of this
        // technique from anything I've read.  Just past the bundle on to the widget
        // directly, instead of making it have to go get the data itself.  Deliver the
        // data to it.  I hope this will be acceptable to you, seems foolproof.
        //
        // An alternative would be for the widget to listen to changes to the SharedPreferences,
        // which I update whenever the user makes choices of recipe/step, etc.
        Bundle bundle = new Bundle();
        bundle.putParcelable(MainActivity.KEY_RECIPE_DATA, recipe);
        intent.putExtra(RecipeInfoAppWidget.class.getSimpleName(), bundle);

        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplication());
        int ids[] = appWidgetManager.getAppWidgetIds(
                new ComponentName(this, RecipeInfoAppWidget.class)
        );
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

}
