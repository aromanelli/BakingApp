package info.romanelli.udacity.bakingapp.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class DataManager {

    final static private String TAG = DataManager.class.getSimpleName();

    static private DataManager REF;

    private SharedPreferences mPrefs;

    private List<RecipeData> mRecipes;
    private RecipeData mRecipeData;
    private StepData mStepData;

    @SuppressLint("ApplySharedPref")
    private DataManager(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        // Clear out any previous prefs ...
        // TODO AOR Should we clear? How does this affect the Widget?
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();
        mRecipeData = new RecipeData(null);
        setRecipeData(mRecipeData);
        mStepData = new StepData(null);
        setStepData(mStepData);
        editor.commit();
    }

    static public void init(final Context context) {
        if (REF == null) {
            synchronized (TAG) {
                if (REF == null) {
                    Log.d(TAG, "$: Creating data manager for Context [" + context + "]!");
                    REF = new DataManager(context);
                } else {
                    Log.w(TAG, DataManager.class.getSimpleName() + " is already initialized." );
                }
            }
        }
    }

    static public DataManager $() {
        if (REF == null) {
            throw new IllegalStateException("Must initialize first!");
        }
        return REF;
    }

    public List<RecipeData> getRecipes() {
        return this.mRecipes;
    }

    public void setRecipes(final List<RecipeData> recipes) {
        if (recipes == null) throw new IllegalArgumentException("Non-null reference expected!");
        this.mRecipes = recipes;
    }

    public RecipeData getRecipeData() {
        return mRecipeData;
    }

    RecipeData fetchRecipeData() {
        return new RecipeDataConverter().fromString(
                mPrefs.getString(
                        RecipeData.class.getSimpleName(),
                        null
                )
        );
    }

    public void setRecipeData(final RecipeData recipeData) {
        if (recipeData == null) throw new IllegalArgumentException("Non-null reference expected!");
        mRecipeData = recipeData;
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(
                RecipeData.class.getSimpleName(),
                new RecipeDataConverter().toString(recipeData)
        );
        editor.apply();
    }

    public StepData getStepData() {
        return mStepData;
    }

    StepData fetchStepData() {
        return new StepDataConverter().fromString(
                mPrefs.getString(
                        StepData.class.getSimpleName(),
                        new Gson().toJson(new StepData(null))
                )
        );
    }

    public void setStepData(final StepData stepData) {
        if (stepData == null) throw new IllegalArgumentException("Non-null reference expected!");
        mStepData = stepData;
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(
                StepData.class.getSimpleName(),
                new StepDataConverter().toString(stepData)
        );
        editor.apply();
    }

    public List<IngredientData> getIngredients() {
        return getRecipeData().getIngredients();
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    static class ListRecipeDataConverter {
        private Gson mGson;
        ListRecipeDataConverter() {
            super();
            mGson = new Gson();
        }
        public List<RecipeData> fromString(String value) {
            Type listType = new TypeToken<List<RecipeData>>() {}.getType();
            return mGson.fromJson(value, listType);
        }
        public String toString(List<RecipeData> list) {
            return mGson.toJson(list);
        }
    }

    @SuppressWarnings("unused")
    static class RecipeDataConverter {
        private Gson mGson;
        RecipeDataConverter() {
            super();
            mGson = new Gson();
        }
        RecipeData fromString(String value) {
            Type listType = new TypeToken<RecipeData>() {}.getType();
            return mGson.fromJson(value, listType);
        }
        String toString(RecipeData data) {
            return mGson.toJson(data);
        }
    }

    @SuppressWarnings("unused")
    static class StepDataConverter {
        private Gson mGson;
        StepDataConverter() {
            super();
            mGson = new Gson();
        }
        StepData fromString(String value) {
            Type listType = new TypeToken<StepData>() {}.getType();
            return mGson.fromJson(value, listType);
        }
        String toString(StepData data) {
            return mGson.toJson(data);
        }
    }

    @SuppressWarnings("unused")
    static class ListStepDataConverter {
        private Gson mGson;
        ListStepDataConverter() {
            super();
            mGson = new Gson();
        }
        List<StepData> fromString(String value) {
            Type listType = new TypeToken<List<StepData>>() {}.getType();
            return mGson.fromJson(value, listType);
        }
        String toString(List<StepData> list) {
            return mGson.toJson(list);
        }
    }

    @SuppressWarnings("unused")
    static class IngredientDataConverter {
        private Gson mGson;
        IngredientDataConverter() {
            super();
            mGson = new Gson();
        }
        IngredientData fromString(String value) {
            Type listType = new TypeToken<IngredientData>() {}.getType();
            return mGson.fromJson(value, listType);
        }
        String toString(IngredientData data) {
            return mGson.toJson(data);
        }
    }

    @SuppressWarnings("unused")
    static class ListIngredientDataConverter {
        private Gson mGson;
        ListIngredientDataConverter() {
            super();
            mGson = new Gson();
        }
        List<IngredientData> fromString(String value) {
            Type listType = new TypeToken<List<IngredientData>>() {}.getType();
            return mGson.fromJson(value, listType);
        }
        String toString(List<IngredientData> list) {
            return mGson.toJson(list);
        }
    }

}
