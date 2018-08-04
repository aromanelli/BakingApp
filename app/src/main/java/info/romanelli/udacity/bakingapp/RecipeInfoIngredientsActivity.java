package info.romanelli.udacity.bakingapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import info.romanelli.udacity.bakingapp.data.IngredientData;

/**
 * An activity representing a single RecipeInfo detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeInfoActivity}.
 */
public class RecipeInfoIngredientsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipeinfo_ingredients);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (ViewModelProviders.of(this).get(DataViewModel.class).getRecipeData().getIngredients() == null) {
            throw new IllegalStateException("Expected a List<" + IngredientData.class.getSimpleName() + "> reference!");
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to
            // the activity, using a fragment transaction.
            /*
            Below not needed because fragment uses ViewModelProviders.of, unlike step fragment,
            which uses a bundle!  Leaving below REMmed out code for documentation purposes, in
            case ingredients fragment changes and requires same design as step fragment.
             */
//            Bundle bundle = new Bundle();
//            ArrayList<Parcelable> listData = new ArrayList<>();
//            listData.addAll( ViewModelProviders.of(this).get(DataViewModel.class).getRecipeData().getIngredients() );
//            bundle.putParcelableArrayList(
//                    MainActivity.KEY_INGREDIENT_DATA,
//                    listData
//            );

            // https://developer.android.com/topic/libraries/architecture/viewmodel#sharing
            RecipeInfoIngredientsFragment fragment = new RecipeInfoIngredientsFragment();
//            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipeinfo_ingredients_container, fragment)
                    .commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // android.R.id.home represents the Home or Up button. In
            // the case of this activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back

            navigateUpTo( new Intent(this, RecipeInfoActivity.class) );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
