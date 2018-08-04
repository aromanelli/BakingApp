package info.romanelli.udacity.bakingapp.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class DataManagerTest {

    @BeforeClass
    static public void initForTesting() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("info.romanelli.udacity.bakingapp", appContext.getPackageName());
        DataManager.init(appContext);
    }

    @Test
    public void testGetSetRecipeData() {
        try {
            RecipeData recipeData = DataManager.$().getRecipeData();
            assertNotNull(recipeData);
            assertNotEquals("TEST", recipeData.getName());
            DataManager.$().setRecipeData(RecipeData.getTestData());
            recipeData = DataManager.$().getRecipeData();
            assertNotNull(recipeData);
            assertEquals("TEST", recipeData.getName());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetSetStepData() {
        try {
            StepData stepData = DataManager.$().getStepData();
            assertNotNull(stepData);
            assertNotEquals("TEST1", stepData.getShortDescription());
            assertNotEquals("TEST2", stepData.getDescription());
            DataManager.$().setStepData(StepData.getTestData());
            stepData = DataManager.$().getStepData();
            assertNotNull(stepData);
            assertEquals("TEST1", stepData.getShortDescription());
            assertEquals("TEST2", stepData.getDescription());
        } catch (Exception e) {
            fail();
        }
    }

}