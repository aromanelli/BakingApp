package info.romanelli.udacity.bakingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import info.romanelli.udacity.bakingapp.network.NetUtil;
import info.romanelli.udacity.bakingapp.network.RecipeData;
import info.romanelli.udacity.bakingapp.network.RecipesFetcher;

public class MainActivity
        extends AppCompatActivity
        implements RecipesAdapter.OnClickHandler, RecipesFetcher.Listener {

    final static private String TAG = MainActivity.class.getSimpleName();

    final static public String KEY_BUNDLE_RECIPES = "key_bundle_recipese";
    final static public String KEY_RECIPE_DATA = "key_recipe_data";

    private RecyclerView mViewRecipes;

    private RecipesAdapter mAdapterRecipes;

    // Bundle.putParcelableArrayList requires ArrayList not List
    private List<RecipeData> listRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewRecipes = findViewById(R.id.rv_recipes);
        mViewRecipes.setHasFixedSize(true);
        // TODO AOR Figure out correct layout manager for multiple columns in tablet in landscape
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        mViewRecipes.setLayoutManager(layoutManager);

        mAdapterRecipes = new RecipesAdapter(this);
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
            listRecipes = savedInstanceState.getParcelableArrayList(KEY_BUNDLE_RECIPES);
            fetchedRecipes(listRecipes);
        }

    }

    @Override
    public void fetchedRecipes(List<RecipeData> recipes) {
        Log.d(TAG, "fetchedRecipes() called with: recipes = [" + recipes + "]");
        this.listRecipes = recipes;
        mViewRecipes.getLayoutManager().scrollToPosition(0);
        mAdapterRecipes.setData(recipes);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(
                KEY_BUNDLE_RECIPES, (ArrayList<RecipeData>) listRecipes);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRecipeClick(RecipeData recipe, ImageView ivPoster) {
        Log.d(TAG, "onRecipeClick() called with: recipe = [" + recipe + "], ivPoster = [" + ivPoster + "]");
        final Bundle bundle = new Bundle(1);
        bundle.putParcelable(KEY_RECIPE_DATA, recipe);
        // TODO AOR Activate below code
//        final Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }

}
