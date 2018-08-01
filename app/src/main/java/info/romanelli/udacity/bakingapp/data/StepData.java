package info.romanelli.udacity.bakingapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

final public class StepData implements Parcelable {

    @SerializedName("id")
    private int mId;

    @SerializedName("shortDescription")
    private String mShortDescription;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("videoURL")
    private String mURLVideo;

    @SerializedName("thumbnailURL")
    private String mURLThumbnail;

    public int getId() {
        return mId;
    }

    public String getShortDescription() {
        return mShortDescription;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getURLVideo() {
        return mURLVideo;
    }

    public String getURLThumbnail() {
        return mURLThumbnail;
    }

    @Override
    public String toString() {
        return "StepData{" +
                "mId=" + mId +
                ", mShortDescription='" + mShortDescription + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mURLVideo='" + mURLVideo + '\'' +
                ", mURLThumbnail='" + mURLThumbnail + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StepData stepData = (StepData) o;
        return mId == stepData.mId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(mId);
    }

    ////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("WeakerAccess")
    protected StepData(Parcel in) {
        mId = in.readInt();
        mShortDescription = in.readString();
        mDescription = in.readString();
        mURLVideo = in.readString();
        mURLThumbnail = in.readString();
    }

    public static final Creator<StepData> CREATOR = new Creator<StepData>() {
        @Override
        public StepData createFromParcel(Parcel in) {
            return new StepData(in);
        }

        @Override
        public StepData[] newArray(int size) {
            return new StepData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mShortDescription);
        dest.writeString(mDescription);
        dest.writeString(mURLVideo);
        dest.writeString(mURLThumbnail);
    }

}
