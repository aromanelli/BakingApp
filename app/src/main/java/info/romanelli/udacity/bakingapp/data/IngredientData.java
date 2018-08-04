package info.romanelli.udacity.bakingapp.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

final public class IngredientData implements Parcelable {

    @SerializedName("quantity")
    private float mQuantity;

    @SerializedName("measure")
    private String mMeasure;

    @SerializedName("ingredient")
    private String mIngredient;

    public float getQuantity() {
        return mQuantity;
    }

    public String getMeasure() {
        return mMeasure;
    }

    public String getIngredient() {
        return mIngredient;
    }

    @Override
    public String toString() {
        return "IngredientData{" +
                "mQuantity=" + mQuantity +
                ", mMeasure='" + mMeasure + '\'' +
                ", mIngredient='" + mIngredient + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IngredientData that = (IngredientData) o;
        return Float.compare(that.mQuantity, mQuantity) == 0 &&
                Objects.equals(mMeasure, that.mMeasure) &&
                Objects.equals(mIngredient, that.mIngredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mQuantity, mMeasure, mIngredient);
    }

////////////////////////////////////////////////////////////////////////////////

    /**
     * Values assigned, for testing purposes only
     * (based on equals() testing parameters) ...<br><br>
     *
     * ingredientData.mIngredient = "TEST";<br>
     * ingredientData.mQuantity = 123;<br>
     * ingredientData.mMeasure = "FPS";<br>
     * @return IngredientData
     */
    @VisibleForTesting
    static public IngredientData getTestData() {
        IngredientData ingredientData = new IngredientData(null);
        ingredientData.mIngredient = "TEST";
        ingredientData.mQuantity = 123;
        ingredientData.mMeasure = "FPS";
        return ingredientData;
    }

    @SuppressWarnings("WeakerAccess")
    protected IngredientData(Parcel in) {
        if (in != null) {
            mQuantity = in.readFloat();
            mMeasure = in.readString();
            mIngredient = in.readString();
        }
    }

    public static final Creator<IngredientData> CREATOR = new Creator<IngredientData>() {
        @Override
        public IngredientData createFromParcel(Parcel in) {
            return new IngredientData(in);
        }

        @Override
        public IngredientData[] newArray(int size) {
            return new IngredientData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(mQuantity);
        dest.writeString(mMeasure);
        dest.writeString(mIngredient);
    }

}
