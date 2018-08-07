package info.romanelli.udacity.bakingapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import info.romanelli.udacity.bakingapp.data.StepData;
import info.romanelli.udacity.bakingapp.event.StepDataEvent;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
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
//                mPager.setOffscreenPageLimit(mPagerAdapter.getCount());

            List<StepData> listStepData =
                    ViewModelProviders.of(this).get(DataViewModel.class).getRecipeData().getSteps();
            int index = listStepData.indexOf(ViewModelProviders.of(this).get(DataViewModel.class).getStepData());
            if (index < 0) {
                throw new IllegalStateException("Bad index for StepData!");
            }
            setCurrentPage(index);

            mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                // TODO AOR Consolidate this listener code with RecipeInfoActivity's version!
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    Log.d(TAG, "onPageSelected() called with: position = [" + position + "]");
                    StepData stepData = ViewModelProviders.of(RecipeInfoStepActivity.this).get(DataViewModel.class)
                            .getRecipeData().getSteps().get(position);
                    ViewModelProviders.of(RecipeInfoStepActivity.this).get(DataViewModel.class)
                            .setStepData(stepData);
                    // Tell any video players to stop, as a switch is about to happen ...
                    EventBus.getDefault().post(
                            new StepDataEvent(StepDataEvent.Type.SELECTED, position, stepData)
                    );
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }

    }

    protected void setCurrentPage(final int index) {
        mPager.setCurrentItem(index);
    }

    @Override
    public void onBackPressed() {
        /////////////////////////////////////////////////////////////////
        // On Nexus 10, view a step vertically, rotate to horizontal, see
        // blank view, then press below back button, mPager to be null.
        /////////////////////////////////////////////////////////////////
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
            super.onBackPressed();
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
