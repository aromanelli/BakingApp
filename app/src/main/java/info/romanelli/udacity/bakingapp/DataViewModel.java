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

    public DataViewModel() {
        super();
    }

    public List<RecipeData> getListRecipes() {
        return mListRecipes;
    }

    public void setListRecipes(List<RecipeData> mListRecipes) {
        this.mListRecipes = mListRecipes;
    }

    public RecipeData getRecipeData() {
        return mRecipeData;
    }

    public void setRecipeData(RecipeData mRecipeData) {
        this.mRecipeData = mRecipeData;
    }

    public IngredientData getIngredientData() {
        return mIngredientData;
    }

    public void setIngredientData(IngredientData mIngredientData) {
        this.mIngredientData = mIngredientData;
    }

    public StepData getStepData() {
        return mStepData;
    }

    public void setStepData(StepData mStepData) {
        this.mStepData = mStepData;
    }

}
