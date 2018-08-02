package info.romanelli.udacity.bakingapp.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final public class RecipeData implements Parcelable {

    // POJO design coded by me, confirmed via http://www.jsonschema2pojo.org/
    final static private String TAG = RecipeData.class.getSimpleName();

    @SerializedName("id")
    private int mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("ingredients")
    private List<IngredientData> mIngredients;

    @SerializedName("steps")
    private List<StepData> mSteps;

    @SerializedName("servings")
    private int mServings;

    @SerializedName("image")
    private String mImage;

    private Uri mImageUri;

    @VisibleForTesting
    public RecipeData() {
        super();
        // MUST be ArrayList, to match RecipeData(Parcel) assignment, and for testing purposes.
        mIngredients = new ArrayList<>(); // MUST be ArrayList
        mSteps = new ArrayList<>();
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public List<IngredientData> getIngredients() {
        return mIngredients;
    }

    public List<StepData> getSteps() {
        return mSteps;
    }

    public int getServings() {
        return mServings;
    }

    public String getImage() {
        return mImage;
    }

//    // TODO AOR REMOVE, FOR DEBUGGING PURPOSES ONLY! (See also RecipesRecyclerViewAdapter.setData(List<RecipeData>)
//    public String setImage(final String image) {
//        String oldImage = mImage;
//        mImage = image;
//        return oldImage;
//    }

    /**
     * @return A {@link Uri} to the image, or {@code null} if no valid path to image is available.
     */
    public Uri getImageUri() {
        if ((mImage != null) && (mImage.length() >= 1) && mImageUri == null) {
            try {
                mImageUri = Uri.parse(mImage).buildUpon().build();
                Log.d(TAG, "getImageUri: ["+ mImageUri +"]");
            } catch (UnsupportedOperationException use) {
                Log.e(TAG, "getImageUri: ", use);
            }
        }
        return mImageUri;
    }

    @Override
    public String toString() {
        return "\nRecipeData{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mServings=" + mServings +
                ", mImage='" + mImage + '\'' +
                ", mImageUri='" + mImageUri + '\'' +
                ",\n\tmIngredients=" + mIngredients +
                ",\n\tmSteps=" + mSteps +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeData that = (RecipeData) o;
        return mId == that.mId &&
                Objects.equals(mName, that.mName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(mId, mName);
    }

    ////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("WeakerAccess")
    protected RecipeData(Parcel in) {
        mId = in.readInt();
        mName = in.readString();

        //noinspection unchecked
        mIngredients = in.readArrayList(IngredientData.class.getClassLoader());
        //noinspection unchecked
        mSteps = in.readArrayList(StepData.class.getClassLoader());

        mServings = in.readInt();
        mImage = in.readString();
        mImageUri = in.readParcelable(Uri.class.getClassLoader());
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
        dest.writeParcelable(mImageUri, 0);
    }

}
