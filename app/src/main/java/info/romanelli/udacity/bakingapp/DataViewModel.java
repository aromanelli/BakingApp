package info.romanelli.udacity.bakingapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import info.romanelli.udacity.bakingapp.data.AppDataEntry;
import info.romanelli.udacity.bakingapp.data.AppDatabase;
import info.romanelli.udacity.bakingapp.data.IngredientData;
import info.romanelli.udacity.bakingapp.data.RecipeData;
import info.romanelli.udacity.bakingapp.data.StepData;

public class DataViewModel extends ViewModel {

    final static public String TAG = DataViewModel.class.getSimpleName();

    private LiveData<AppDataEntry> entryAppData;
    private AppDataObserver observer;

    private AppDataEntry mEntry = new AppDataEntry(
            new ArrayList<RecipeData>(0),
            Integer.MIN_VALUE,
            Integer.MIN_VALUE,
            Integer.MIN_VALUE
    );

    static public void resetAppDataDBRecord() {
        // Always force a single record (AppDataDao is OnConflictStrategy.REPLACE, AppDataEntry)
        AppExecutors.$().diskIO().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        // Create initial record/row ...
                        AppDatabase.$().getDao().insert(
                                new AppDataEntry(
                                        new ArrayList<RecipeData>(0),
                                        Integer.MIN_VALUE,
                                        Integer.MIN_VALUE,
                                        Integer.MIN_VALUE
                                )
                        );
                    }
                });
    }

    DataViewModel() {
        super();
        // Get LiveData and start observing ...
        entryAppData = AppDatabase.$().getDao().get();
        observer = new AppDataObserver();
        entryAppData.observeForever(observer);
    }

    private class AppDataObserver implements Observer<AppDataEntry> {
        @Override
        public void onChanged(@Nullable AppDataEntry appDataEntry) {
            Log.d(TAG, "onChanged() called with: appDataEntry = [" + appDataEntry + "]");
            mEntry = appDataEntry;
        }
    }

    @Override
    protected void onCleared() {
        entryAppData.removeObserver(observer);
    }

    public LiveData<AppDataEntry> getLiveData() {
        return entryAppData;
    }

    synchronized public List<RecipeData> getRecipes() {
        return mEntry.getListRecipeData();
    }

    synchronized public void setRecipes(final List<RecipeData> listRecipes) {
        synchronized (TAG) {
            if (listRecipes == null) {
                throw new IllegalArgumentException("Expected a non-null List<" + RecipeData.class.getSimpleName() + "> reference!");
            }
            mEntry.setListRecipeData(listRecipes);
            // We don't save list to db, so no need to do anything further.
        }
    }

    synchronized public RecipeData getRecipeData() {
        return mEntry.getRecipeData();
    }

    synchronized public void setRecipeData(final RecipeData recipeData) {
        if (recipeData == null) {
            throw new IllegalArgumentException("Expected a non-null " + RecipeData.class.getSimpleName() + " reference!");
        }
        AppExecutors.$().diskIO().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        synchronized (TAG) {
                            // Fetch the AppDataEntry ...
                            AppDatabase.$().getDao().get().getValue();
                            // Wait until the fetch returns, then update ...
                            getLiveData().observeForever(new Observer<AppDataEntry>() {
                                @Override
                                public void onChanged(@Nullable AppDataEntry appDataEntry) {
                                    mEntry = appDataEntry;
                                    getLiveData().removeObserver(this); // TODO AOR Confirm 'this' is Observer not DataViewModel
                                    mEntry.setRecipeData(recipeData);
                                    // Put into the database ...
                                    AppDatabase.$().getDao().update(mEntry);
                                }
                            });
                        }
                    }
                });
    }

    synchronized public StepData getStepData() {
        return mEntry.getStepData();
    }

    synchronized public void setStepData(final StepData stepData) {
        if (stepData == null) {
            throw new IllegalArgumentException("Expected a non-null " + StepData.class.getSimpleName() + " reference!");
        }
        AppExecutors.$().diskIO().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        synchronized (TAG) {
                            // Fetch the AppDataEntry ...
                            AppDatabase.$().getDao().get().getValue();
                            // Wait until the fetch returns, then update ...
                            getLiveData().observeForever(new Observer<AppDataEntry>() {
                                @Override
                                public void onChanged(@Nullable AppDataEntry appDataEntry) {
                                    mEntry = appDataEntry;
                                    getLiveData().removeObserver(this); // TODO AOR Confirm 'this' is Observer not DataViewModel
                                    mEntry.setStepData(stepData);
                                    // Put into the database ...
                                    AppDatabase.$().getDao().update(mEntry);
                                }
                            });
                        }
                    }
                });
    }

    synchronized public IngredientData getIngredientData() {
        return mEntry.getIngredientData();
    }

    synchronized public void setIngredientData(final IngredientData ingredientData) {
        if (ingredientData == null) {
            throw new IllegalArgumentException("Expected a non-null " + IngredientData.class.getSimpleName() + " reference!");
        }
        AppExecutors.$().diskIO().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        synchronized (TAG) {
                            // Fetch the AppDataEntry ...
                            AppDatabase.$().getDao().get().getValue();
                            // Wait until the fetch returns, then update ...
                            getLiveData().observeForever(new Observer<AppDataEntry>() {
                                @Override
                                public void onChanged(@Nullable AppDataEntry appDataEntry) {
                                    mEntry = appDataEntry;
                                    getLiveData().removeObserver(this); // TODO AOR Confirm 'this' is Observer not DataViewModel
                                    mEntry.setIngredientData(ingredientData);
                                    // Put into the database ...
                                    AppDatabase.$().getDao().update(mEntry);
                                }
                            });
                        }
                    }
                });
    }

}
