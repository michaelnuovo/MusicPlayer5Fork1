package com.example.michael.musicplayer5;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by michael on 11/11/15.
 */
public class ArtistObject implements Parcelable {

    public String artistName;

    public int albumCount;
    public int artistTrackCount = 0;

    public ArrayList<SongObject> songObjectList = new ArrayList<>();

    public ArtistObject(SongObject songObject){

        this.artistName = songObject.artist;
        this.songObjectList.add(songObject);

    }

    // Parcelable Code

    public ArtistObject(Parcel in) {

        artistName = in.readString();
        albumCount = in.readInt();
        artistTrackCount = in.readInt();
        in.readTypedList(songObjectList, SongObject.CREATOR);
    }

    public static final Parcelable.Creator<ArtistObject> CREATOR = new Parcelable.Creator<ArtistObject>() {

        public ArtistObject createFromParcel(Parcel in) {

            return new ArtistObject(in);
        }

        public ArtistObject[] newArray(int size) {

            return new ArtistObject[size];
        }

    };

    public void readFromParcel(Parcel in) {

        artistName = in.readString();
        albumCount = in.readInt();
        artistTrackCount = in.readInt();
        in.readTypedList(songObjectList, SongObject.CREATOR);

    }
    public int describeContents() {

        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(artistName);
        dest.writeInt(albumCount);
        dest.writeInt(artistTrackCount);
        dest.writeTypedList(songObjectList);

    }

}
