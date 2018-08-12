package info.romanelli.udacity.bakingapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.romanelli.udacity.bakingapp.data.RecipeData;
import info.romanelli.udacity.bakingapp.data.StepData;

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
                .inflate(R.layout.recipeinfo_parts_container, parent, false);
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

                        // Launch activity, or set fragment, based on if two panes or not ...
                        int index = (int) view.getTag();
                        if (mTwoPane) {
                            mParentActivity.setCurrentPage(index);
                        } else {

                            // In two pane mode, OnPageChangeListener on ViewPager will do
                            // the below setting for us, but when we instead launch an Activity
                            // directly, we need to set StepData ourselves, before launch ...
                            StepData stepData;
                            if (index == 0) {
                                stepData = null; // Ingredients chosen
                            } else {
                                stepData = ViewModelProviders.of(mParentActivity).get(DataViewModel.class)
                                        .getRecipeData().getSteps().get((index - 1));
                            }
                            ViewModelProviders.of(mParentActivity).get(DataViewModel.class)
                                    .setStepData(stepData);

                            // Determine if we're going to show ingredients or steps information ...
                            final Class<? extends AppCompatActivity> clazzActivity;
                            if (index == 0) {
                                // User selected the List<IngredientsData> entry ...
                                clazzActivity = RecipeInfoIngredientsActivity.class;
                            } else {
                                // User selected a StepData entry ...
                                clazzActivity = RecipeInfoStepActivity.class;
                                // RecipeInfoFragmentsPagerAdapter handled instantiation of fragments already
                            }
                            // Launch the Activity ...
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

        @BindView(R.id.content)
        TextView tvContent;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }
    }

}
