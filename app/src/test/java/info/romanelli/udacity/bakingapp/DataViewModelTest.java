package info.romanelli.udacity.bakingapp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import info.romanelli.udacity.bakingapp.data.IngredientData;
import info.romanelli.udacity.bakingapp.data.RecipeData;

@SuppressWarnings("unused")
public class DataViewModelTest {

    private DataViewModel model;

    @Before
    public void setUp() {
        model = new DataViewModel();
    }

    @After
    public void tearDown() {
        model = null;
    }

    @Test
    public void setListRecipes() {
        try {
            model.setListRecipes(new ArrayList<RecipeData>());
        } catch (Exception e) {
            org.junit.Assert.fail();
        }
        try {
            model.setListRecipes(null);
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
    public void setIngredientData() {
        try {
            model.setIngredientData(null);
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

    @Test
    public void setIngredientsForRecipeData() {
        // First test is trying to set an empty list of ingredients, but a recipe has not be set yet ...
        try {
            model.setIngredientsForRecipeData(new ArrayList<IngredientData>());
            org.junit.Assert.fail();
        } catch (IllegalStateException ise) {
            // NO-OP: We want to be here
        } catch (Exception e) {
            org.junit.Assert.fail();
        }
        // Add/set the recipe ...
        model.setRecipeData(new RecipeData());
        // Now try again to set an empty list of ingredients ...
        try {
            model.setIngredientsForRecipeData(new ArrayList<IngredientData>());
            org.junit.Assert.assertTrue(model.getIngredientsForRecipeData().isEmpty());
        } catch (Exception e) {
            org.junit.Assert.fail();
        }
        // Now try again again to set an empty list (Collections.emptyList()) of ingredients ...
        // (Making sure addAll(Collection) works properly)
        try {
            model.setIngredientsForRecipeData(Collections.<IngredientData>emptyList());
            org.junit.Assert.assertTrue(model.getIngredientsForRecipeData().isEmpty());
        } catch (Exception e) {
            org.junit.Assert.fail();
        }
        // Try to set a null as the list of ingredients ...
        try {
            model.setIngredientsForRecipeData(null);
            org.junit.Assert.fail();
        } catch (Exception e) {
            // NO-OP: We want to be here
        }
        // Finally try to set a list of real data (not empty) ...
        try {
            ArrayList<IngredientData> list = new ArrayList<>();
            list.add(new IngredientData());
            model.setIngredientsForRecipeData(list);
            org.junit.Assert.assertTrue(model.getIngredientsForRecipeData().size() == 1);
        } catch (Exception e) {
            // NO-OP: We want to be here
        }
    }

}