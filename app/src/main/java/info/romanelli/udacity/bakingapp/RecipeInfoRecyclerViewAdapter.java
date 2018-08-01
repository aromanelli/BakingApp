package info.romanelli.udacity.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.romanelli.udacity.bakingapp.data.RecipeData;

public class RecipeInfoRecyclerViewAdapter extends RecyclerView.Adapter<RecipeInfoRecyclerViewAdapter.ViewHolder> {


    private final RecipeInfoActivity mParentActivity;
    private final RecipeData mRecipeData;
    private final boolean mTwoPane;

    RecipeInfoRecyclerViewAdapter(RecipeInfoActivity parent,
                                  RecipeData recipeData,
                                  boolean twoPane) {
        mRecipeData = recipeData;
        mParentActivity = parent;
        mTwoPane = twoPane;
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
        } else {
            text = res.getString(
                    R.string.step_number_info,
                    position, mRecipeData.getSteps().get((position - 1)).getShortDescription()
            );
        }
        holder.tvContent.setText(text);

        holder.itemView.setTag(mRecipeData);
        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RecipeData recipe = (RecipeData) view.getTag();
                        // TODO AOR Code Ingredients or Steps logic
                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putParcelable(MainActivity.KEY_RECIPE_DATA, recipe);
                            RecipeInfoStepFragment fragment = new RecipeInfoStepFragment();
                            fragment.setArguments(arguments);
                            mParentActivity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.recipeinfo_step_container, fragment)
                                    .commit();
                        } else {
                            final Bundle bundle = new Bundle(1);
                            bundle.putParcelable(MainActivity.KEY_RECIPE_DATA, recipe);
                            Context context = view.getContext();
                            Intent intent = new Intent(context, RecipeInfoStepActivity.class);
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
