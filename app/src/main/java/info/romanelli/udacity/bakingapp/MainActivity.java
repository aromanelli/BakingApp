package info.romanelli.udacity.bakingapp;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import info.romanelli.udacity.bakingapp.data.DataManager;
import info.romanelli.udacity.bakingapp.data.RecipeData;
import info.romanelli.udacity.bakingapp.network.NetUtil;
import info.romanelli.udacity.bakingapp.network.RecipesFetcher;

public class MainActivity
        extends AppCompatActivity
        implements RecipesRecyclerViewAdapter.OnClickHandler, RecipesFetcher.Listener {

    final static private String TAG = MainActivity.class.getSimpleName();

    final static public String KEY_BUNDLE_RV_ITEM_POS = "key_bundle_recyclerview_item_position";
    final static public String KEY_STEP_DATA = "key_step_data";
    final static public String KEY_STEP_DATA_ID = "key_index_step_data";

    private RecyclerView mViewRecipes;

    private RecipesRecyclerViewAdapter mAdapterRecipes;

    private int mIndexFirstVisibleItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataManager.init(this);

        mViewRecipes = findViewById(R.id.rv_recipes);
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
    }

}
