package info.romanelli.udacity.bakingapp.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

final class IngredientsData implements Parcelable {

    @SerializedName("quantity")
    private float mQuantity;

    @SerializedName("measure")
    private String mMeasure;

    @SerializedName("ingredient")
    private String mIngredient;

    @Override
    public String toString() {
        return "IngredientsData{" +
                "mQuantity=" + mQuantity +
                ", mMeasure='" + mMeasure + '\'' +
                ", mIngredient='" + mIngredient + '\'' +
                '}';
    }

    ////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("WeakerAccess")
    protected IngredientsData(Parcel in) {
        mQuantity = in.readFloat();
        mMeasure = in.readString();
        mIngredient = in.readString();
    }

    public static final Creator<IngredientsData> CREATOR = new Creator<IngredientsData>() {
        @Override
        public IngredientsData createFromParcel(Parcel in) {
            return new IngredientsData(in);
        }

        @Override
        public IngredientsData[] newArray(int size) {
            return new IngredientsData[size];
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
