package info.romanelli.udacity.bakingapp.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class RecipeData implements Parcelable {

    // POJO design coded by me confirmed via http://www.jsonschema2pojo.org/

    @SerializedName("id")
    private int mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("ingredients")
    private List<IngredientsData> mIngredients;

    @SerializedName("steps")
    private List<StepsData> mSteps;

    @SerializedName("servings")
    private int mServings;

    @SerializedName("image")
    private String mImage;

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public List<IngredientsData> getIngredients() {
        return mIngredients;
    }

    public List<StepsData> getSteps() {
        return mSteps;
    }

    public int getServings() {
        return mServings;
    }

    public String getImage() {
        return mImage;
    }

    @Override
    public String toString() {
        return "\nRecipeData{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mServings=" + mServings +
                ", mImage='" + mImage + '\'' +
                ",\n\tmIngredients=" + mIngredients +
                ",\n\tmSteps=" + mSteps +
                "}";
    }

    ////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("WeakerAccess")
    protected RecipeData(Parcel in) {
        mId = in.readInt();
        mName = in.readString();

        //noinspection unchecked
        mIngredients = in.readArrayList(IngredientsData.class.getClassLoader());
        //noinspection unchecked
        mSteps = in.readArrayList(StepsData.class.getClassLoader());

        mServings = in.readInt();
        mImage = in.readString();
    }

    public static final Creator<RecipeData> CREATOR = new Creator<RecipeData>() {
        @Override
        public RecipeData createFromParcel(Parcel in) {
            return new RecipeData(in);
        }

        @Override
        public RecipeData[] newArray(int size) {
            return new RecipeData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeList(mIngredients);
        dest.writeList(mSteps);
        dest.writeInt(mServings);
        dest.writeString(mImage);
    }

}
