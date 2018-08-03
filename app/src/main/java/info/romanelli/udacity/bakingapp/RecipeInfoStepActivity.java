package info.romanelli.udacity.bakingapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import info.romanelli.udacity.bakingapp.data.RecipeData;
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
    private PagerAdapter mPagerAdapter;

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

        if (savedInstanceState == null) {
            List<? extends Parcelable> listData = getIntent().getParcelableArrayListExtra(MainActivity.KEY_STEP_DATA);
            ViewModelProviders.of(this).get(DataViewModel.class).setRecipeData(
                    (RecipeData) listData.get(0)
            );
            ViewModelProviders.of(this).get(DataViewModel.class).setStepData(
                    (StepData) listData.get(1)
            );
        }
        if (ViewModelProviders.of(this).get(DataViewModel.class).getRecipeData() == null)
            throw new IllegalStateException("Expected a " + RecipeData.class.getSimpleName() + " reference!");
        if (ViewModelProviders.of(this).get(DataViewModel.class).getStepData() == null)
            throw new IllegalStateException("Expected a "+ StepData.class.getSimpleName() +" reference!");

        if (savedInstanceState == null) {
            // Instantiate a ViewPager and a PagerAdapter ...
            // (https://developer.android.com/training/animation/screen-slide)
            mPager = findViewById(R.id.pager);
            mPagerAdapter = new RecipeInfoStepPagerAdapter(getSupportFragmentManager(), this);
            mPager.setAdapter(mPagerAdapter);

            List<StepData> listStepData =
                    ViewModelProviders.of(this).get(DataViewModel.class).getRecipeData().getSteps();
            mPager.setOffscreenPageLimit( listStepData.size() );
            int index = listStepData.indexOf(ViewModelProviders.of(this).get(DataViewModel.class).getStepData());
            if (index < 0) {
                throw new IllegalStateException("Bad index for StepData!");
            }
            mPager.setCurrentItem(index);

            mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    Log.d(TAG, "onPageScrolled() called with: position = [" + position + "], positionOffset = [" + positionOffset + "], positionOffsetPixels = [" + positionOffsetPixels + "]");
                }
                @Override
                public void onPageSelected(int position) {
                    Log.d(TAG, "onPageSelected() called with: position = [" + position + "]");
                    StepData stepData = ViewModelProviders.of(RecipeInfoStepActivity.this).get(DataViewModel.class)
                            .getRecipeData().getSteps().get(position);
                    System.out.println(">>>>> " + position + " || " + stepData); // TODO AOR REMOVE
                    ViewModelProviders.of(RecipeInfoStepActivity.this).get(DataViewModel.class)
                            .setStepData(stepData);
                }
                @Override
                public void onPageScrollStateChanged(int state) {
                    Log.d(TAG, "onPageScrollStateChanged() called with: state = [" + state + "]");
                }
            });

        }

//        // savedInstanceState is non-null when there is fragment state
//        // saved from previous configurations of this activity
//        // (e.g. when rotating the screen from portrait to landscape).
//        // In this case, the fragment will automatically be re-added
//        // to its container so we don't need to manually add it.
//        // For more information, see the Fragments API guide at:
//        //
//        // http://developer.android.com/guide/components/fragments.html
//        //
//        if (savedInstanceState == null) {
//            // Create the detail fragment and add it to
//            // the activity, using a fragment transaction.
//
//            Bundle bundle = new Bundle();
//            ArrayList<Parcelable> listData = new ArrayList<>(2);
//            // 'mRecipeData' is needed by RecipeInfo(Ingredient/Step)Activity
//            // when calling back to RecipeInfoActivity when user backs out
//            listData.add(0, ViewModelProviders.of(this).get(DataViewModel.class).getRecipeData()); // 0 for documentation reasons
//            listData.add( ViewModelProviders.of(this).get(DataViewModel.class).getStepData() );
//            bundle.putParcelableArrayList(
//                    MainActivity.KEY_STEP_DATA,
//                    listData
//            );
//
//            // https://developer.android.com/topic/libraries/architecture/viewmodel#sharing
//            RecipeInfoStepFragment fragment = new RecipeInfoStepFragment();
//            fragment.setArguments(bundle); // No need when using ViewModelProviders.of
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.recipeinfo_step_container, fragment)
//                    .commit();
//        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // android.R.id.home represents the Home or Up button. In
            // the case of this activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back

            final Bundle bundle = new Bundle(1);
            bundle.putParcelable(
                    MainActivity.KEY_RECIPE_DATA,
                    ViewModelProviders.of(this).get(DataViewModel.class).getRecipeData()
            );
            Intent intent = new Intent(this, RecipeInfoActivity.class);
            intent.putExtras(bundle);
            navigateUpTo(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    static private class RecipeInfoStepPagerAdapter extends FragmentStatePagerAdapter {

        final static private String TAG = RecipeInfoStepPagerAdapter.class.getSimpleName();

        private RecipeInfoStepActivity parentActivity;

        public RecipeInfoStepPagerAdapter(FragmentManager fm, RecipeInfoStepActivity parentActivity) {
            super(fm);
            this.parentActivity = parentActivity;
        }

        @Override
        public Fragment getItem(int position) {

            /*
            The FragmentPagerAdapter instantiates 2 Fragments on start, for index 0 and for index 1.
            If you want to get data from the Fragment which is on the screen, you can use
            addOnPageChangeListener for the Pager to get current position.
             */

            // In this case, instead of just calling getStepData(), we use 'position' on getSteps() list ...
            StepData stepData = ViewModelProviders.of(parentActivity).get(DataViewModel.class)
                    .getRecipeData().getSteps().get(position);
            Log.d(TAG, "getItem: position: " + position + ", stepData: " + stepData);

            // TODO AOR Below may not work right because of timing of updates vs reads in fragment
//            ViewModelProviders.of(parentActivity).get(DataViewModel.class)
//                    .setStepData(stepData); // Fragment

            Bundle bundle = new Bundle();
            ArrayList<Parcelable> listData = new ArrayList<>(2);
            // 'mRecipeData' is needed by RecipeInfo(Ingredient/Step)Activity
            // when calling back to RecipeInfoActivity when user backs out
            listData.add(0, ViewModelProviders.of(parentActivity).get(DataViewModel.class)
                    .getRecipeData()); // 0 for documentation reasons

            listData.add(stepData);

            bundle.putParcelableArrayList(
                    MainActivity.KEY_STEP_DATA,
                    listData
            );

            // https://developer.android.com/topic/libraries/architecture/viewmodel#sharing
            RecipeInfoStepFragment fragment = new RecipeInfoStepFragment();
            fragment.setArguments(bundle); // No need when using ViewModelProviders.of

            return fragment;
        }

        @Override
        public int getCount() {
            return ViewModelProviders.of(parentActivity).get(DataViewModel.class)
                    .getRecipeData().getSteps().size();
        }
    }

}
