package com.example.michael.musicplayer5;

import android.os.Parcel;
import android.os.Parcelable;

public class SongObject implements Parcelable{

    // My Code

    public String albumArtURI;
    public String albumTitle;
    public String artist;
    public String songTitle;
    public String songPath;
    public String songDuration;
    public String albumURL;

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
        albumTitle = in.readString();
        artist = in.readString();
        songTitle = in.readString();
        songPath = in.readString();
        songDuration = in.readString();
        albumURL = in.readString();

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumArtURI);
        dest.writeString(albumTitle);
        dest.writeString(artist);
        dest.writeString(songTitle);
        dest.writeString(songPath);
        dest.writeString(songDuration);
        dest.writeString(albumURL);

    }
}
