package info.romanelli.udacity.bakingapp;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import info.romanelli.udacity.bakingapp.data.StepData;

/**
 * An activity representing a single RecipeInfo detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeInfoActivity}.
 */
public class RecipeInfoStepActivity extends AppCompatActivity {

    final static private String TAG = RecipeInfoStepActivity.class.getSimpleName();

    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipeinfo_step);

        setTitle(ViewModelProviders.of(this).get(DataViewModel.class).getRecipeData().getName());

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // Show the Up button in the action bar.
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html

        // Instantiate a ViewPager and a PagerAdapter ...
        // (https://developer.android.com/training/animation/screen-slide)
        mPager = findViewById(R.id.pager);
        if (mPager != null) { // Only our 'two pane' tablet view has a pager, phone view does not!
            mPager.setAdapter(
                    new RecipeInfoFragmentsPagerAdapter(
                            getSupportFragmentManager(), this, false)
            );
            // mPager.setOffscreenPageLimit(mPagerAdapter.getCount());

            // Set the right page, based on the StepData set into the view model by the recycler view adapter ...
            List<StepData> listStepData =
                    ViewModelProviders.of(this).get(DataViewModel.class).getRecipeData().getSteps();
            int index = listStepData.indexOf(ViewModelProviders.of(this).get(DataViewModel.class).getStepData());
            if (index < 0) {
                throw new IllegalStateException("Bad index for StepData!");
            }
            setCurrentPage(index);
        }

    }

    protected void setCurrentPage(final int index) {
        if (mPager != null) {
            mPager.setCurrentItem(index);
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager != null) {
            if (mPager.getCurrentItem() == 0) {
                // If the user is currently looking at the first step, allow the system to handle the
                // Back button. This calls finish() on this activity and pops the back stack.
                super.onBackPressed();
            } else {
                // Otherwise, select the previous step.
                setCurrentPage(mPager.getCurrentItem() - 1);
            }
        } else {
            NavUtils.navigateUpFromSameTask(this);
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

            NavUtils.navigateUpFromSameTask(this);
            // navigateUpTo( new Intent(this, RecipeInfoActivity.class) );

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
