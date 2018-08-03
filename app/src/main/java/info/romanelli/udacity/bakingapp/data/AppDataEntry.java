package info.romanelli.udacity.bakingapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

@Entity(tableName = AppDataDao.NAME_TABLE_APP_DATA)
public class AppDataEntry {

    @SuppressWarnings("DefaultAnnotationParam")
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = false) // We supply value, to force single record
    private int id;

    @TypeConverters(AppDataEntry.ListRecipeDataConverter.class)
    @ColumnInfo(name = "recipes")
    private List<RecipeData> listRecipeData;

    @ColumnInfo(name = "index_recipe_data")
    private int indexRecipeData;

    @ColumnInfo(name = "index_step_data")
    private int indexStepData;

    @ColumnInfo(name = "index_ingredients_data")
    private int indexIngredientData;

    public AppDataEntry(final List<RecipeData> listRecipeData,
                        final int indexRecipeData,
                        final int indexStepData,
                        final int indexIngredientData) {
        // Always force a single record (AppDataDao is OnConflictStrategy.REPLACE, AppDataModel)
        this.id = 0;
        this.listRecipeData = listRecipeData;
        this.indexRecipeData = indexRecipeData;
        this.indexStepData = indexStepData;
        this.indexIngredientData = indexIngredientData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<RecipeData> getListRecipeData() {
        return listRecipeData;
    }

    public void setListRecipeData(List<RecipeData> listRecipeData) {
        this.listRecipeData = listRecipeData;
    }

    public int getIndexRecipeData() {
        return indexRecipeData;
    }

    public void setIndexRecipeData(int indexRecipeData) {
        this.indexRecipeData = indexRecipeData;
    }

    public int getIndexStepData() {
        return indexStepData;
    }

    public void setIndexStepData(int indexStepData) {
        this.indexStepData = indexStepData;
    }

    public int getIndexIngredientData() {
        return indexIngredientData;
    }

    public void setIndexIngredientData(int indexIngredientData) {
        this.indexIngredientData = indexIngredientData;
    }

    public RecipeData getRecipeData() {
        if (getIndexRecipeData() == Integer.MIN_VALUE) {
            return null;
        }
        return getListRecipeData().get(getIndexRecipeData());
    }

    // Below getters/setters always assume there's no add, only update, of data POJOs.

    public void setRecipeData(RecipeData recipeData) {
        int index = getListRecipeData().indexOf(recipeData);
        if (index < 0) {
            throw new IllegalStateException("Bad RecipeData Index! Call setIndexXXXData(int) first!");
        } else {
            setIndexRecipeData(index);
            getListRecipeData().remove(getIndexRecipeData());
            getListRecipeData().add(getIndexRecipeData(), recipeData);
        }
    }

    public StepData getStepData() {
        if (getIndexRecipeData() == Integer.MIN_VALUE) {
            return null;
        }
        if (getIndexStepData() == Integer.MIN_VALUE) {
            return null;
        }
        return getListRecipeData().get(getIndexRecipeData()).getSteps().get(getIndexStepData());
    }

    public void setStepData(StepData stepData) {
        int index = getRecipeData().getSteps().indexOf(stepData);
        if (index < 0) {
            throw new IllegalStateException("Bad StepData Index! Call setIndexXXXData(int) first!");
        }
        setIndexStepData(index);
        getRecipeData().getSteps().remove(getIndexStepData());
        getRecipeData().getSteps().add(getIndexStepData(), stepData);
    }

    public IngredientData getIngredientData() {
        if (getIndexRecipeData() == Integer.MIN_VALUE) {
            return null;
        }
        if (getIndexIngredientData() == Integer.MIN_VALUE) {
            return null;
        }
        return getListRecipeData().get(getIndexRecipeData()).getIngredients().get(getIndexIngredientData());
    }

    public void setIngredientData(IngredientData ingredientData) {
        int index = getRecipeData().getIngredients().indexOf(ingredientData);
        if (index < 0) {
            throw new IllegalStateException("Bad IngredientData Index! Call setIndexXXXData(int) first!");
        }
        setIndexIngredientData(index);
        getRecipeData().getSteps().remove(getIndexIngredientData());
        getRecipeData().getIngredients().add(getIndexIngredientData(), ingredientData);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppDataEntry that = (AppDataEntry) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AppDataEntry{" +
                "id=" + id +
                ", indexRecipeData=" + indexRecipeData +
                ", indexStepData=" + indexStepData +
                ", indexIngredientData=" + indexIngredientData +
                ", listRecipeData=" + listRecipeData +
                '}';
    }

    static public class ListRecipeDataConverter {
        ListRecipeDataConverter() {
            super();
        }
        @TypeConverter
        public List<RecipeData> fromString(String value) {
            Type listType = new TypeToken<List<RecipeData>>() {}.getType();
            return new Gson().fromJson(value, listType);
        }
        @TypeConverter
        public String toString(List<RecipeData> list) {
            return new Gson().toJson(list);
        }
    }

    static class RecipeDataConverter {
        RecipeDataConverter() {
            super();
        }
        @TypeConverter
        public RecipeData fromString(String value) {
            Type listType = new TypeToken<RecipeData>() {}.getType();
            return new Gson().fromJson(value, listType);
        }
        @TypeConverter
        public String toString(RecipeData data) {
            return new Gson().toJson(data);
        }
    }

    static class StepDataConverter {
        StepDataConverter() {
            super();
        }
        @TypeConverter
        public StepData fromString(String value) {
            Type listType = new TypeToken<StepData>() {}.getType();
            return new Gson().fromJson(value, listType);
        }
        @TypeConverter
        public String toString(StepData data) {
            return new Gson().toJson(data);
        }
    }

    static class ListStepDataConverter {
        ListStepDataConverter() {
            super();
        }
        @TypeConverter
        public List<StepData> fromString(String value) {
            Type listType = new TypeToken<List<StepData>>() {}.getType();
            return new Gson().fromJson(value, listType);
        }
        @TypeConverter
        public String toString(List<StepData> list) {
            return new Gson().toJson(list);
        }
    }

    static class IngredientDataConverter {
        IngredientDataConverter() {
            super();
        }
        @TypeConverter
        public IngredientData fromString(String value) {
            Type listType = new TypeToken<IngredientData>() {}.getType();
            return new Gson().fromJson(value, listType);
        }
        @TypeConverter
        public String toString(IngredientData data) {
            return new Gson().toJson(data);
        }
    }

    static class ListIngredientDataConverter {
        ListIngredientDataConverter() {
            super();
        }
        @TypeConverter
        public List<IngredientData> fromString(String value) {
            Type listType = new TypeToken<List<IngredientData>>() {}.getType();
            return new Gson().fromJson(value, listType);
        }
        @TypeConverter
        public String toString(List<IngredientData> list) {
            return new Gson().toJson(list);
        }
    }

}
