package info.romanelli.udacity.bakingapp;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.romanelli.udacity.bakingapp.data.StepData;
import info.romanelli.udacity.bakingapp.event.StepDataEvent;

/**
 * An activity representing a list of RecipeInfos. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeInfoStepActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeInfoActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    final static private String TAG = RecipeInfoStepActivity.class.getSimpleName();

    private boolean mTwoPane;

    @Nullable
    @BindView(R.id.pager)
    ViewPager mPager;

    private RecipeInfoFragmentsPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipeinfo);
        ButterKnife.bind(this);

        setTitle(ViewModelProviders.of(this).get(DataViewModel.class).getRecipeData().getName());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.recipeinfo_allfrags_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.rvRecipeInfo);
        assert recyclerView != null;
        ((RecyclerView) recyclerView).setAdapter(
                new RecipeInfoRecyclerViewAdapter(
                        this,
                        mTwoPane,
                        ViewModelProviders.of(this).get(DataViewModel.class).getRecipeData()
                )
        );

        // Instantiate a ViewPager and a PagerAdapter ...
        // (https://developer.android.com/training/animation/screen-slide)
        if (mPager != null) { // Only our 'two pane' tablet view has a pager, phone view does not!
            mPagerAdapter = new RecipeInfoFragmentsPagerAdapter(
                    getSupportFragmentManager(), this, mTwoPane); // mTwoPane, not false!
            mPager.setAdapter(mPagerAdapter);
            // mPager.setOffscreenPageLimit(mPagerAdapter.getCount());
            mPager.addOnPageChangeListener(this);
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
                NavUtils.navigateUpFromSameTask(this); // super.onBackPressed();
            } else {
                // Otherwise, select the previous step.
                setCurrentPage(mPager.getCurrentItem() - 1);
            }
        } else {
            NavUtils.navigateUpFromSameTask(this); // super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back

            NavUtils.navigateUpFromSameTask(this);
            // navigateUpTo( new Intent(this, RecipeInfoActivity.class) );

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (mPager != null) {
            mPager.removeOnPageChangeListener(this);
        }
        super.onDestroy();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        final StepData stepData;
        if (position == 0) {
            stepData = null; // Ingredients chosen
        } else {
            stepData = ViewModelProviders.of(this).get(DataViewModel.class)
                    .getRecipeData().getSteps().get((position - 1));
        }

        ViewModelProviders.of(this).get(DataViewModel.class)
                .setStepData(stepData);

        mPagerAdapter.postEvent( // (See RecipeInfoStepActivity)
                new StepDataEvent(StepDataEvent.Type.SELECTED, position, stepData)
        );
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

}
