package info.romanelli.udacity.bakingapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

final public class StepsData implements Parcelable {

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
        return "StepsData{" +
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
        StepsData stepsData = (StepsData) o;
        return mId == stepsData.mId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(mId);
    }

    ////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("WeakerAccess")
    protected StepsData(Parcel in) {
        mId = in.readInt();
        mShortDescription = in.readString();
        mDescription = in.readString();
        mURLVideo = in.readString();
        mURLThumbnail = in.readString();
    }

    public static final Creator<StepsData> CREATOR = new Creator<StepsData>() {
        @Override
        public StepsData createFromParcel(Parcel in) {
            return new StepsData(in);
        }

        @Override
        public StepsData[] newArray(int size) {
            return new StepsData[size];
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
