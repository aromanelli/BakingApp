package info.romanelli.udacity.bakingapp;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import info.romanelli.udacity.bakingapp.data.RecipeData;
import info.romanelli.udacity.bakingapp.data.StepData;

/**
 * A fragment representing a single RecipeInfo detail screen.
 * This fragment is either contained in a
 * {@link RecipeInfoActivity}({@link RecipeInfoRecyclerViewAdapter})
 * in two-pane mode (on tablets), or a {@link RecipeInfoStepActivity}
 * on handsets.
 */
public class RecipeInfoStepFragment extends Fragment {

    /**
     * The recipe content this fragment is presenting.
     */
    private RecipeData mRecipeData;
    private StepData mStepData;

    /**
     * Mandatory empty constructor for the fragment manager to
     * instantiate the fragment (e.g. upon screen orientation changes).
     */
    public RecipeInfoStepFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((getArguments() != null) && getArguments().containsKey(MainActivity.KEY_STEP_DATA)) {

            /*
            We use the bundle data, and not the ViewModelProviders.of(...).get(...).getXXX()
            method of data acquisition, because in RecipeInfoStepActivity, it uses a ViewPager,
            and it builds each of these fragments up front, even though the user sees just one
            of these at a time.  Each fragment has its own displaying orders, which StepData to
            display information about.

            In other words, multiple RecipeInfoStepFragments, each with their own StepData,
            and not multiple RecipeInfoStepFragments, all sharing the same one StepData.
             */

            List<Parcelable> listData = getArguments().getParcelableArrayList(MainActivity.KEY_STEP_DATA);
            if (listData != null) {
                mRecipeData = (RecipeData) listData.get(0);
                if (mRecipeData == null)
                    throw new IllegalStateException("Expected a " + RecipeData.class.getSimpleName() + " reference!");
                mStepData = (StepData) listData.get(1);
                if (mStepData == null)
                    throw new IllegalStateException("Expected a " + StepData.class.getSimpleName() + " reference!");
            }

        }

        if (getActivity() != null) {

            // Needed for when fragment is in a solo activity
            getActivity().setTitle(mRecipeData.getName());
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recipeinfo_step_content, container, false);

        if (getActivity() == null) {
            throw new IllegalStateException("Expected a " + RecipeData.class.getSimpleName() + " reference!");
        }

        // Show the dummy content as text in a TextView.
        ((TextView) rootView.findViewById(R.id.recipeinfo_step_description)).setText(
                mStepData.getDescription() // TODO AOR CODE THIS !
        );

        return rootView;
    }
}
