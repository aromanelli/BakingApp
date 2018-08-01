package info.romanelli.udacity.bakingapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.romanelli.udacity.bakingapp.data.RecipeData;

/**
 * A fragment representing a single RecipeInfo detail screen.
 * This fragment is either contained in a {@link RecipeInfoActivity}
 * in two-pane mode (on tablets) or a {@link RecipeInfoStepActivity}
 * on handsets.
 */
public class RecipeInfoStepFragment extends Fragment {

    /**
     * The recipe content this fragment is presenting.
     */
    private RecipeData mRecipeData;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeInfoStepFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((getArguments() != null) && getArguments().containsKey(MainActivity.KEY_RECIPE_DATA)) {

            // Load the content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mRecipeData = getArguments().getParcelable(MainActivity.KEY_RECIPE_DATA);
            if (mRecipeData == null)
                throw new IllegalStateException("Expected a "+ RecipeData.class.getSimpleName() +" reference!");

            Activity activity = this.getActivity();
            if (activity != null) {
                activity.setTitle(mRecipeData.getName());
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipeinfo_step_content, container, false);

        // Show the dummy content as text in a TextView.
        if (mRecipeData != null) {
            ((TextView) rootView.findViewById(R.id.recipeinfo_step_content)).setText(
                    mRecipeData.getSteps().get(0).getDescription() // TODO AOR CODE THIS 0
            );
        }

        return rootView;
    }
}
