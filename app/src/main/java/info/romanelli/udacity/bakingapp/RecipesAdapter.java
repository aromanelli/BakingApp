package info.romanelli.udacity.bakingapp;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import info.romanelli.udacity.bakingapp.network.RecipeData;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    final static private String TAG = RecipesAdapter.class.getSimpleName();

    private List<RecipeData> listRecipes;
    final private OnClickHandler clickHandler;

    RecipesAdapter(final OnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    public void setData(final List<RecipeData> listRecipes) {
        this.listRecipes = listRecipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.recipe_card,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final RecipeData recipe = listRecipes.get(position);

        if (recipe.getName() != null && recipe.getName().trim().length() >= 1) {
            holder.tvRecipeName.setText(recipe.getName());
        } else {
            holder.tvRecipeName.setText("");
        }

        if (recipe.getServings() >= 1) {
            // https://developer.android.com/guide/topics/resources/string-resource#Plurals
            Resources res = holder.tvRecipeServings.getResources();
            String text = res.getQuantityString(
                    R.plurals.number_of_servings,
                    // When using the getQuantityString() method, you need to pass the count
                    // twice if your string includes string formatting with a number!
                    recipe.getServings(), recipe.getServings()
            );
            holder.tvRecipeServings.setText(text);
        } else {
            holder.tvRecipeServings.setText("");
        }

        if (recipe.getImage() != null && recipe.getImage().trim().length() >= 1) {
            // TODO AOR Fetch image from the Internet!
            // AppUtil.setPosterToView(null, recipe.getImage(), holder.ivRecipePicture);
            // See PopularMovies2
            // holder.ivRecipePicture.setImageResource(?);
        } else {
            holder.ivRecipePicture.setImageResource(R.drawable.ic_baseline_fastfood_24px);
        }

    }

    @Override
    public int getItemCount() {
        return listRecipes != null ? listRecipes.size() : 0;
    }

    public interface OnClickHandler {
        void onRecipeClick(final RecipeData recipe, final ImageView ivPoster);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivRecipePicture;
        TextView tvRecipeName;
        TextView tvRecipeServings;

        ViewHolder(View itemView) {
            super(itemView);

            ivRecipePicture = itemView.findViewById(R.id.recipe_picture);
            tvRecipeName = itemView.findViewById(R.id.recipe_name);
            tvRecipeServings = itemView.findViewById(R.id.recipe_servings);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                clickHandler.onRecipeClick(
                        listRecipes.get(getAdapterPosition()),
                        ivRecipePicture
                );
            }
        }

    }

}
