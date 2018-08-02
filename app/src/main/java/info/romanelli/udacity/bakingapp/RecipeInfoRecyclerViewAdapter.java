package info.romanelli.udacity.bakingapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.romanelli.udacity.bakingapp.data.IngredientData;
import info.romanelli.udacity.bakingapp.data.RecipeData;
import info.romanelli.udacity.bakingapp.data.StepData;

public class RecipeInfoRecyclerViewAdapter extends RecyclerView.Adapter<RecipeInfoRecyclerViewAdapter.ViewHolder> {

    final static private String TAG = RecipeInfoRecyclerViewAdapter.class.getSimpleName();

    private final RecipeInfoActivity mParentActivity;
    private final RecipeData mRecipeData;
    private final boolean mTwoPane;

    RecipeInfoRecyclerViewAdapter(RecipeInfoActivity parent,
                                  boolean twoPane) {

        mParentActivity = parent;
        mTwoPane = twoPane;
        mRecipeData = ViewModelProviders.of(mParentActivity).get(DataViewModel.class).getRecipeData();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipeinfo_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        String text;
        Resources res = holder.tvContent.getResources();
        if (position == 0) {
            text = res.getQuantityString(
                    R.plurals.recipe_ingredients,
                    // When using the getQuantityString() method, you need to pass the count
                    // twice if your string includes string formatting with a number!
                    mRecipeData.getIngredients().size(), mRecipeData.getIngredients().size()
            );
            holder.itemView.setTag(mRecipeData.getIngredients());
        } else {
            text = res.getString(
                    R.string.step_number_info,
                    mRecipeData.getSteps().get((position - 1)).getShortDescription()
            );
            holder.itemView.setTag(mRecipeData.getSteps().get((position - 1)));
        }

        holder.tvContent.setText(text);

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Class<? extends AppCompatActivity> clazzActivity;
                        final Class<? extends Fragment> clazzFragment;

                        final Bundle bundle = new Bundle(2);
                        ArrayList<Parcelable> listData = new ArrayList<>(2);
                        // 'mRecipeData' is needed by RecipeInfo(Ingredient/Step)Activity
                        // when calling back to RecipeInfoActivity when user backs out
                        listData.add(0, mRecipeData); // 0 for documentation reasons

                        // Determine if we're going to show ingredients or steps information ...
                        if (view.getTag() instanceof StepData) {
                            ViewModelProviders.of(mParentActivity).get(DataViewModel.class)
                                    .setStepData((StepData) view.getTag()); // Fragment
                            listData.add( (Parcelable) view.getTag() );
                            bundle.putParcelableArrayList(
                                    MainActivity.KEY_STEP_DATA,
                                    listData
                            );
                            clazzActivity = RecipeInfoStepActivity.class;
                            clazzFragment = RecipeInfoStepFragment.class;
                        }
                        else { // Assume List<IngredientData>
                            // Below call to setIngredientsForRecipeData not needed, since setTag(...) above sets the same list!
                            //noinspection unchecked
                            ViewModelProviders.of(mParentActivity).get(DataViewModel.class)
                                    .setIngredientsForRecipeData(((List<IngredientData>) view.getTag())); // For fragment
                            //noinspection unchecked
                            listData.addAll( (List<Parcelable>) view.getTag() );
                            bundle.putParcelableArrayList(
                                    MainActivity.KEY_INGREDIENT_DATA,
                                    listData
                            );
                            clazzActivity = RecipeInfoIngredientsActivity.class;
                            clazzFragment = RecipeInfoIngredientsFragment.class;
                        }

                        // Launch activity, or set fragment, based on if two panes or not ...
                        if (mTwoPane) {
                            // https://developer.android.com/topic/libraries/architecture/viewmodel#sharing
                            Fragment fragment;
                            try {
                                fragment = clazzFragment.newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                Log.e(TAG, "Error creating Fragment ["+ clazzFragment +"]: ", e);
                                AppUtil.showToast(
                                        mParentActivity,
                                        mParentActivity.getString(R.string.msg_err_occurred),
                                        false
                                );
                                return;
                            }
                            fragment.setArguments(bundle); // No need when using ViewModelProviders.of
                            mParentActivity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.recipeinfo_fragment_container, fragment)
                                    .commit();
                        } else {
                            Context context = view.getContext();
                            Intent intent = new Intent(context, clazzActivity);
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        }

                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return 1 + mRecipeData.getSteps().size(); // 1 is for all Ingredients
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvContent;
        ViewHolder(View view) {
            super(view);
            tvContent = view.findViewById(R.id.content);
        }
    }

}
