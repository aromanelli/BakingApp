package info.romanelli.udacity.bakingapp.data;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import info.romanelli.udacity.bakingapp.MainActivity;
import info.romanelli.udacity.bakingapp.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    // Dev note: See section on @Rule for activity with intent usage in below link ...
    // https://android.jlelse.eu/the-basics-of-android-espresso-testing-activities-fragments-7a8bfbc16dc5
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void TestRecipesInformation() {

        onView(withId(R.id.rv_recipes)).check(
                matches(isDisplayed())
        );

        // Even though we passed the isDisplayed() above, the async fetching of the data
        // by RecipesFetcher, notifying MainActivity, which updates DataManager, may take
        // some millis after displaying, so we wait a reasonable time for DataManager to
        // have the same data that was displayed by Main Activity, given to by RecipesFetcher.
        int waitCounter = 0;
        List<RecipeData> recipeDataList = null;
        while ((recipeDataList == null) && (waitCounter < 40)) { // 250ms x 40 = 10 seconds
            recipeDataList = DataManager.$().getRecipes();
            if (recipeDataList == null) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    waitCounter++;
                }
            }
        }
        if (recipeDataList == null) {
            fail("Unable to get recipe data from model via net fetcher!");
        }

        // Test the app title appears in AppBar/top area of activity ...
        // https://developer.android.com/training/testing/espresso/lists#recycler-view-list-items
        onView(withText(
                mActivityTestRule.getActivity().getResources().getString(R.string.app_name))
        ).check(matches(isDisplayed()));

        // For each recipe, do checks ...
        for (int i = 0; i < recipeDataList.size(); i++) {

            // Make sure each recipe is viewable ...
            onView(withId(R.id.rv_recipes)).perform(RecyclerViewActions.scrollToPosition(i))
                    .check(matches(isDisplayed()));

            // https://spin.atomicobject.com/2016/04/15/espresso-testing-recyclerviews/

            // Make sure each recipe's name is visible ...
            onView(RecyclerViewMatcher.with(R.id.rv_recipes).atPosition(i))
                    .check(matches(hasDescendant(withText(
                            recipeDataList.get(i).getName()
                    ))));

            // Make sure each recipe's servings info is visible ...
            // (See RecipesRecyclerViewAdapter for getQuantityString production usage)
            int numberOfServings = recipeDataList.get(i).getServings();
            String textServings = mActivityTestRule.getActivity().getResources().getQuantityString(
                    R.plurals.number_of_servings,
                    // When using the getQuantityString() method, you need to pass the count
                    // twice if your string includes string formatting with a number!
                    numberOfServings, numberOfServings
            );
            onView(RecyclerViewMatcher.with(R.id.rv_recipes).atPosition(i))
                    .check(matches(hasDescendant(withText( textServings ))));

        }
    }

    /*
    Some links from Udacity reviewer ...

    https://www.youtube.com/watch?v=JlHJFZvZyxw
    https://spin.atomicobject.com/2016/04/15/espresso-testing-recyclerviews/
    http://alexander-thiele.blogspot.in/2016/01/espresso-ui-tests-and-recyclerview.html

    https://www.thedroidsonroids.com/blog/android/espresso-test-recording
    https://www.captechconsulting.com/blogs/automated-ui-testing-made-quick-and-easy-with-android-studios-espresso-test-recorder
    https://peirr.com/writing-android-tests-with-espresso-test-recorder/
     */

}
