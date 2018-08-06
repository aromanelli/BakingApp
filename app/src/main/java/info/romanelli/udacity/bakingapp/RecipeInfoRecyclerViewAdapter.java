package info.romanelli.udacity.bakingapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import info.romanelli.udacity.bakingapp.data.RecipeData;
import info.romanelli.udacity.bakingapp.data.StepData;
import info.romanelli.udacity.bakingapp.event.StepDataEvent;

public class RecipeInfoRecyclerViewAdapter extends RecyclerView.Adapter<RecipeInfoRecyclerViewAdapter.ViewHolder> {

    final static private String TAG = RecipeInfoRecyclerViewAdapter.class.getSimpleName();

    private final RecipeInfoActivity mParentActivity;
    private final RecipeData mRecipeData;
    private final boolean mTwoPane;

    RecipeInfoRecyclerViewAdapter(RecipeInfoActivity parent,
                                  boolean twoPane,
                                  RecipeData recipeData ) {

        mParentActivity = parent;
        mTwoPane = twoPane;
        mRecipeData = recipeData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipeinfo_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.itemView.setTag(position);
        Resources res = holder.tvContent.getResources();

        String text;
        if (position == 0) {
            text = res.getQuantityString(
                    R.plurals.recipe_ingredients,
                    // When using the getQuantityString() method, you need to pass the count
                    // twice if your string includes string formatting with a number!
                    mRecipeData.getIngredients().size(), mRecipeData.getIngredients().size()
            );
        } else {
            text = res.getString(
                    R.string.step_number_info,
                    mRecipeData.getSteps().get((position - 1)).getShortDescription()
            );
        }
        holder.tvContent.setText(text);

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int index = (int) view.getTag();

                        final Class<? extends AppCompatActivity> clazzActivity;
                        final Class<? extends Fragment> clazzFragment;

                        final Bundle bundle = new Bundle(1);

                        // Determine if we're going to show ingredients or steps information ...

                        StepData stepData = null;
                        int stepIndex = Integer.MIN_VALUE;
                        if (index == 0) {
                            // User selected the List<IngredientsData> entry ...
                            clazzActivity = RecipeInfoIngredientsActivity.class;
                            clazzFragment = RecipeInfoIngredientsFragment.class;
                        } else {
                            // User selected a StepData entry ...
                            stepIndex = index - 1;
                            stepData = mRecipeData.getSteps().get(stepIndex);
                            ViewModelProviders.of(mParentActivity).get(DataViewModel.class)
                                    .setStepData(stepData); // Fragment
                            bundle.putParcelable(MainActivity.KEY_STEP_DATA, stepData);
                            bundle.putInt(MainActivity.KEY_INDEX_STEP_DATA, stepIndex);

                            clazzActivity = RecipeInfoStepActivity.class;
                            clazzFragment = RecipeInfoStepFragment.class;
                        }

                        // Launch activity, or set fragment, based on if two panes or not ...
                        if (mTwoPane) {
                            // https://developer.android.com/topic/libraries/architecture/viewmodel#sharing
                            Fragment fragment;
                            try {

                                // TODO AOR Recreate a new fragment, vs. reusing an old one?
                                fragment = clazzFragment.newInstance();
                                // Look at RecipeInfoStepPagerAdapter/FragmentStatePagerAdapter, as that is what RecipeInfoStepActivity uses

                            } catch (InstantiationException | IllegalAccessException e) {
                                Log.e(TAG, "Error creating Fragment ["+ clazzFragment +"]: ", e);
                                AppUtil.showToast(
                                        mParentActivity,
                                        mParentActivity.getString(R.string.msg_err_occurred),
                                        false
                                );
                                return;
                            }
                            // Notify fragments with video players that one was selected, so stop your player, etc. ...
                            EventBus.getDefault().post(
                                    new StepDataEvent(StepDataEvent.Type.SELECTED, stepIndex, stepData)
                            );

                            fragment.setArguments(bundle);
                            mParentActivity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.recipeinfo_fragment_container, fragment)
                                    .commit();
                        } else {
                            Context context = view.getContext();
                            context.startActivity(
                                    new Intent(context, clazzActivity)
                            );
                        }

                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return 1 + mRecipeData.getSteps().size(); // 1 is for all Ingredients single text
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvContent;
        ViewHolder(View view) {
            super(view);
            tvContent = view.findViewById(R.id.content);
        }
    }

}
