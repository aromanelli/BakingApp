package info.romanelli.udacity.bakingapp.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

final class StepsData implements Parcelable {

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
