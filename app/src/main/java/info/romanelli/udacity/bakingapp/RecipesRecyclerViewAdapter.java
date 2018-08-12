package info.romanelli.udacity.bakingapp;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.romanelli.udacity.bakingapp.data.RecipeData;

public class RecipesRecyclerViewAdapter extends RecyclerView.Adapter<RecipesRecyclerViewAdapter.ViewHolder> {

    final static private String TAG = RecipesRecyclerViewAdapter.class.getSimpleName();

    private List<RecipeData> listRecipes;
    final private OnClickHandler clickHandler;

    RecipesRecyclerViewAdapter(final OnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    public void setData(final List<RecipeData> listRecipes) {
        if (listRecipes == null) {
            throw new IllegalArgumentException("Expected a non-null recipes reference!");
        }
        this.listRecipes = listRecipes;

//        ////////////////////////////////////////////////
//        // TODO FOR DEBUGGING PURPOSES ONLY! (See also RecipeData.setImage(String)
//        for (int i = 0; i < listRecipes.size(); i++) {
//            if (i == 2) {
//                listRecipes.get(i).setImage(
//                        "xxxhttps://imagesvc.timeincapp.com/v3/mm/image?url=https%3A%2F%2Fcdn-image.realsimple.com%2Fsites%2Fdefault%2Ffiles%2Fstyles%2Fportrait_435x518%2Fpublic%2Fimage%2Fimages%2F1210new%2Fred-lentil-curry-ictcrop_300.jpg%3Fitok%3DJ2Owt05b&w=700&q=85"
//                );
//            } else if ( (i % 2) == 0) {
//                listRecipes.get(i).setImage(
//                        "https://imagesvc.timeincapp.com/v3/mm/image?url=https%3A%2F%2Fcdn-image.realsimple.com%2Fsites%2Fdefault%2Ffiles%2Fstyles%2Fportrait_435x518%2Fpublic%2Fimage%2Fimages%2F1210new%2Fred-lentil-curry-ictcrop_300.jpg%3Fitok%3DJ2Owt05b&w=700&q=85"
//                );
//            }
//        }
//        ////////////////////////////////////////////////

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

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
            Picasso.get()
                    .load(recipe.getImageUri())
                    .placeholder(R.drawable.ic_baseline_fastfood_24px)
                    .into(
                        holder.ivRecipePicture,
                        new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "Picasso:onSuccess: image fetched");
                            }
                            @Override
                            public void onError(Exception e) {
                                Log.e(TAG, "Picasso:onError: ", e);
                                holder.ivRecipePicture.setImageResource(R.drawable.ic_baseline_error_outline_24px);
                            }
                        }
                );
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

        @BindView(R.id.recipe_picture)
        ImageView ivRecipePicture;

        @BindView(R.id.recipe_name)
        TextView tvRecipeName;

        @BindView(R.id.recipe_servings)
        TextView tvRecipeServings;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
