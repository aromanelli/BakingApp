package info.romanelli.udacity.bakingapp;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import info.romanelli.udacity.bakingapp.data.DataManager;
import info.romanelli.udacity.bakingapp.data.IngredientData;
import info.romanelli.udacity.bakingapp.data.RecipeData;
import info.romanelli.udacity.bakingapp.data.StepData;

public class DataViewModel extends ViewModel {

    final static public String TAG = DataViewModel.class.getSimpleName();

    @Override
    protected void onCleared() {
    }

    synchronized public List<RecipeData> getRecipes() {
        return DataManager.$().getRecipes();
    }

    synchronized public void setRecipes(final List<RecipeData> recipes) {
        DataManager.$().setRecipes(recipes);
    }

    synchronized public RecipeData getRecipeData() {
        return DataManager.$().getRecipeData();
    }

    synchronized public void setRecipeData(final RecipeData recipeData) {
        DataManager.$().setRecipeData(recipeData);
    }

    synchronized public StepData getStepData() {
        return DataManager.$().getStepData();
    }

    synchronized public void setStepData(final StepData stepData) {
        DataManager.$().setStepData(stepData);
    }

    synchronized public List<IngredientData> getIngredients() {
        return DataManager.$().getIngredients();
    }

}
