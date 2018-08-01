package info.romanelli.udacity.bakingapp;

import android.app.AlertDialog;
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

import info.romanelli.udacity.bakingapp.data.RecipeData;
import info.romanelli.udacity.bakingapp.network.NetUtil;
import info.romanelli.udacity.bakingapp.network.RecipesFetcher;

public class MainActivity
        extends AppCompatActivity
        implements RecipesRecyclerViewAdapter.OnClickHandler, RecipesFetcher.Listener {

    final static private String TAG = MainActivity.class.getSimpleName();

    final static public String KEY_BUNDLE_RECIPES = "key_bundle_recipese";
    final static public String KEY_BUNDLE_RV_ITEM_POS = "key_bundle_recyclerview_item_position";
    final static public String KEY_RECIPE_DATA = "key_recipe_data";

    private RecyclerView mViewRecipes;

    private RecipesRecyclerViewAdapter mAdapterRecipes;

    private List<RecipeData> mListRecipes;
    
    private int mIndexFirstVisibleItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        if (savedInstanceState == null ||
                (!savedInstanceState.containsKey(KEY_BUNDLE_RECIPES)) ) {
            if (NetUtil.isOnline(this)) {
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
            mListRecipes = savedInstanceState.getParcelableArrayList(KEY_BUNDLE_RECIPES);
            mIndexFirstVisibleItem = savedInstanceState.getInt(KEY_BUNDLE_RV_ITEM_POS);
            fetchedRecipes(mListRecipes);
        }

    }

    @Override
    public void fetchedRecipes(List<RecipeData> recipes) {
        Log.d(TAG, "fetchedRecipes() called with: recipes = [" + recipes + "]");
        this.mListRecipes = recipes;
        mViewRecipes.getLayoutManager().scrollToPosition(mIndexFirstVisibleItem);
        mAdapterRecipes.setData(recipes);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(
                KEY_BUNDLE_RECIPES,
                // Bundle.putParcelableArrayList requires ArrayList, not List
                (ArrayList<RecipeData>) mListRecipes
        );
        outState.putInt(
                KEY_BUNDLE_RV_ITEM_POS,
                ((GridLayoutManager) mViewRecipes.getLayoutManager())
                        .findFirstCompletelyVisibleItemPosition()
        );
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRecipeClick(RecipeData recipe, ImageView ivPoster) {
        final Bundle bundle = new Bundle(1);
        bundle.putParcelable(KEY_RECIPE_DATA, recipe);
        final Intent intent = new Intent(MainActivity.this, RecipeInfoActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
