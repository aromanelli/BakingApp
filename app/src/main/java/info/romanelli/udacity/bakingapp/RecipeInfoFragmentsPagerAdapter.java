package info.romanelli.udacity.bakingapp;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import info.romanelli.udacity.bakingapp.data.StepData;

public class RecipeInfoFragmentsPagerAdapter extends FragmentStatePagerAdapter {


    final static private String TAG = RecipeInfoFragmentsPagerAdapter.class.getSimpleName();

    private AppCompatActivity mParentActivity;
    private boolean mTwoPane;

    RecipeInfoFragmentsPagerAdapter(final FragmentManager fm,
                                    final AppCompatActivity mParentActivity,
                                    final boolean twoPane) {
        super(fm);
        this.mParentActivity = mParentActivity;
        this.mTwoPane = twoPane;
    }

    @Override
    public Fragment getItem(int position) {

        /*
        The FragmentPagerAdapter instantiates at least two Fragments on start, for index 0
        and for index 1.  If you want to get data from the Fragment which is on the screen,
        you can use addOnPageChangeListener for the Pager to get current position.
         */

        if (mTwoPane && (position == 0)) {
            return new RecipeInfoIngredientsFragment();
        } else {

            int stepIndex;
            if (mTwoPane) {
                stepIndex = (position - 1);
            } else {
                stepIndex = position;
            }

            // In this case, instead of just calling getStepData(), we use 'position' on getSteps() list ...
            StepData stepData = ViewModelProviders.of(mParentActivity).get(DataViewModel.class)
                    .getRecipeData().getSteps().get(stepIndex);
            Log.d(TAG, "getItem: position: " + position + ", stepIndex: " + stepIndex + ", stepData: " + stepData);

            Bundle bundle = new Bundle();
            bundle.putParcelable(MainActivity.KEY_STEP_DATA, stepData);
            bundle.putInt(MainActivity.KEY_INDEX_STEP_DATA, stepIndex);

            // https://developer.android.com/topic/libraries/architecture/viewmodel#sharing
            RecipeInfoStepFragment fragment = new RecipeInfoStepFragment();
            fragment.setArguments(bundle);

            return fragment;

        }
    }

    @Override
    public int getCount() {
        int count = ViewModelProviders.of(mParentActivity).get(DataViewModel.class)
                .getRecipeData().getSteps().size();
        if (mTwoPane) {
            count++;
        }
        return count;
    }

}
