package info.romanelli.udacity.bakingapp;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import info.romanelli.udacity.bakingapp.data.IngredientData;
import info.romanelli.udacity.bakingapp.data.RecipeData;
import info.romanelli.udacity.bakingapp.data.StepData;

public class DataViewModel extends ViewModel {

    private List<RecipeData> mListRecipes;
    private RecipeData mRecipeData;
    private IngredientData mIngredientData;
    private StepData mStepData;

    DataViewModel() {
        super();
    }

    public List<RecipeData> getListRecipes() {
        return mListRecipes;
    }

    public void setListRecipes(List<RecipeData> listRecipes) {
        if (listRecipes == null) {
            throw new IllegalArgumentException("Expected a non-null List<" + RecipeData.class.getSimpleName() + "> reference!");
        }
        this.mListRecipes = listRecipes;
    }

    public RecipeData getRecipeData() {
        return mRecipeData;
    }

    public void setRecipeData(RecipeData recipeData) {
        if (recipeData == null) {
            throw new IllegalArgumentException("Expected a non-null " + RecipeData.class.getSimpleName() + " reference!");
        }
        this.mRecipeData = recipeData;
    }

    public IngredientData getIngredientData() {
        return mIngredientData;
    }

    public void setIngredientData(IngredientData ingredientData) {
        if (ingredientData == null) {
            throw new IllegalArgumentException("Expected a non-null " + IngredientData.class.getSimpleName() + " reference!");
        }
        this.mIngredientData = ingredientData;
    }

    public StepData getStepData() {
        return mStepData;
    }

    public void setStepData(StepData stepData) {
        if (stepData == null) {
            throw new IllegalArgumentException("Expected a non-null " + StepData.class.getSimpleName() + " reference!");
        }
        this.mStepData = stepData;
    }

    public List<IngredientData> getIngredientsForRecipeData() {
        return getRecipeData().getIngredients();
    }

    public void setIngredientsForRecipeData(List<IngredientData> listIngredients) {
        if (listIngredients == null) {
            throw new IllegalArgumentException("Expected a non-null List<" + IngredientData.class.getSimpleName() + "> reference!");
        }
        if (getRecipeData() == null) {
            throw new IllegalStateException("There is no " + RecipeData.class.getSimpleName() + " to assign the ingredients to!");
        }
        if (listIngredients.equals(getRecipeData().getIngredients())) {
            // Nothing to do, same list/reference
            return;
        }
        getRecipeData().getIngredients().clear();
        getRecipeData().getIngredients().addAll(listIngredients);
    }

}
