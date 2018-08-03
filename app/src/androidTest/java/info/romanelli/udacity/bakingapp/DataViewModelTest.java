package info.romanelli.udacity.bakingapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import info.romanelli.udacity.bakingapp.data.AppDatabase;
import info.romanelli.udacity.bakingapp.data.RecipeData;

import static org.junit.Assert.assertEquals;

/**
 * <p>Instrumented test, which will execute on an Android device.</p>
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @see <a href="https://proandroiddev.com/testing-the-un-testable-and-beyond-with-android-architecture-components-part-1-testing-room-4d97dec0f451">Testing the Un-Testable With Android Architecture Components - Room Queries</a> */
@RunWith(AndroidJUnit4.class)
public class DataViewModelTest {

    private DataViewModel model;

    @Before
    public void setUp() {

        try {

            // Context of the app under test.
            Context appContext = InstrumentationRegistry.getTargetContext();
            assertEquals("info.romanelli.udacity.bakingapp", appContext.getPackageName());

            AppDatabase.init(appContext);
            DataViewModel.resetAppDataDBRecord();

        } catch (Exception e) {
            // NO-OP: Ignore
        }

        model = new DataViewModel();
    }

    @After
    public void tearDown() {
        model = null;
    }

    @Test
    public void setListRecipes() {
        try {
            model.setRecipes(new ArrayList<RecipeData>());
        } catch (Exception e) {
            org.junit.Assert.fail();
        }
        try {
            model.setRecipes(null);
            org.junit.Assert.fail();
        } catch (Exception e) {
            // NO-OP: We want to be here
        }
    }

    @Test
    public void setRecipeData() {
        try {
            model.setRecipeData(null);
            org.junit.Assert.fail();
        } catch (Exception e) {
            // NO-OP: We want to be here
        }
    }

    @Test
    public void setStepData() {
        try {
            model.setStepData(null);
            org.junit.Assert.fail();
        } catch (Exception e) {
            // NO-OP: We want to be here
        }
    }

}