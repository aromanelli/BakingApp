package info.romanelli.udacity.bakingapp;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single RecipeInfo detail screen.
 * This fragment is either contained in a
 * {@link RecipeInfoActivity}({@link RecipeInfoRecyclerViewAdapter})
 * in two-pane mode (on tablets), or a {@link RecipeInfoIngredientsActivity}
 * on handsets.
 */
public class RecipeInfoIngredientsFragment extends Fragment {

    /**
     * Mandatory empty constructor for the fragment manager to
     * instantiate the fragment (e.g. upon screen orientation changes).
     */
    public RecipeInfoIngredientsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this.getActivity();
        if (activity != null) {
            // Needed for when fragment is in a solo activity
            activity.setTitle(ViewModelProviders.of(getActivity()).get(DataViewModel.class).getRecipeData().getName());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.recipeinfo_ingredients_content, container, false);

        FragmentActivity activity = getActivity();
        if (activity == null)
            throw new IllegalStateException("Expected a non-null Activity reference!");
        ((TextView) rootView.findViewById(R.id.recipeinfo_ingredients_content)).setText(
                AppUtil.getIngredientsText(
                        activity,
                        ViewModelProviders.of(activity).get(DataViewModel.class)
                                .getRecipeData().getIngredients()
                )
        );

        return rootView;
    }
}
