package com.example.michael.musicplayer5;

import android.os.Parcel;
import android.os.Parcelable;

public class SongObject implements Parcelable{

    // My Code

    public String albumArtURI;
    public String album;
    public String artist;
    public String title;
    public String data;
    public String duration;

    public SongObject(){
        super();
    }

    // Parcelable Code

    public SongObject(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<SongObject> CREATOR = new Parcelable.Creator<SongObject>() {
        public SongObject createFromParcel(Parcel in) {
            return new SongObject(in);
        }

        public SongObject[] newArray(int size) {

            return new SongObject[size];
        }

    };

    public void readFromParcel(Parcel in) {
        albumArtURI = in.readString();
        album = in.readString();
        artist = in.readString();
        title = in.readString();
        data = in.readString();
        duration = in.readString();

    }
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumArtURI);
        dest.writeString(album);
        dest.writeString(artist);
        dest.writeString(title);
        dest.writeString(data);
        dest.writeString(duration);

    }
}
