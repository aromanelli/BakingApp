package info.romanelli.udacity.bakingapp;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import info.romanelli.udacity.bakingapp.data.IngredientData;
import info.romanelli.udacity.bakingapp.data.RecipeData;

/**
 * A fragment representing a single RecipeInfo detail screen.
 * This fragment is either contained in a
 * {@link RecipeInfoActivity}({@link RecipeInfoRecyclerViewAdapter})
 * in two-pane mode (on tablets), or a {@link RecipeInfoIngredientsActivity}
 * on handsets.
 */
public class RecipeInfoIngredientsFragment extends Fragment {

    /**
     * The recipe content this fragment is presenting.
     */
    private RecipeData mRecipeData;
    private List<IngredientData> mListIngredientData;

    /**
     * Mandatory empty constructor for the fragment manager to
     * instantiate the fragment (e.g. upon screen orientation changes).
     */
    public RecipeInfoIngredientsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if ((getArguments() != null) && getArguments().containsKey(MainActivity.KEY_STEP_DATA)) {
//
//            List<Parcelable> listData = getArguments().getParcelableArrayList(MainActivity.KEY_STEP_DATA);
//            if (listData != null) {
//                mRecipeData = (RecipeData) listData.get(0);
//                if (mRecipeData == null)
//                    throw new IllegalStateException("Expected a " + RecipeData.class.getSimpleName() + " reference!");
//                mListIngredientData = (IngredientData) listData.get(1);
//                if (mListIngredientData == null)
//                    throw new IllegalStateException("Expected a " + IngredientData.class.getSimpleName() + " reference!");
//            }
//
//        }

        Activity activity = this.getActivity();
        if (activity != null) {

            mRecipeData = ViewModelProviders.of(getActivity()).get(DataViewModel.class).getRecipeData();
                if (mRecipeData == null)
                    throw new IllegalStateException("Expected a " + RecipeData.class.getSimpleName() + " reference!");

            mListIngredientData = ViewModelProviders.of(getActivity()).get(DataViewModel.class).getIngredientsForRecipeData();
                if (mListIngredientData == null)
                    throw new IllegalStateException("Expected a List<" + IngredientData.class.getSimpleName() + "> reference!");

            // Needed for when fragment is in a solo activity
            activity.setTitle(mRecipeData.getName());
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.recipeinfo_ingredients_content, container, false);

        // Show the dummy content as text in a TextView.
        if (mRecipeData != null) {
            StringBuilder builder = new StringBuilder();
            for (IngredientData ingredientData : mListIngredientData) {
                // TODO Swapping languages while in detail activity then hitting back button causes a IllegalFormatConversionException.
                String text = getString(
                        R.string.ingredient_detail,
                        ingredientData.getQuantity(),
                        ingredientData.getMeasure(),
                        ingredientData.getIngredient()
                );
                builder.append(text);
                builder.append('\n');
            }
            ((TextView) rootView.findViewById(R.id.recipeinfo_ingredients_content)).setText(
                    builder.toString()
            );
        }

        return rootView;
    }
}
