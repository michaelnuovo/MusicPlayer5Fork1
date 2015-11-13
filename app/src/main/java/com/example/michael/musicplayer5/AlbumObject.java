package com.example.michael.musicplayer5;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by michael on 11/11/15.
 */
public class AlbumObject implements Parcelable {

    public String albumTitle;
    public String albumArtist;
    public int albumTrackCount = 0;
    public String albumArtURI;
    public ArrayList<SongObject> songObjectList = new ArrayList<>();

    public AlbumObject(String albumTitle, String albumArtist, String albumArtURI, SongObject songObject){

        this.albumTitle = albumTitle;
        this.albumArtist = albumArtist;
        this.albumArtURI = albumArtURI;
        this.songObjectList.add(songObject);

    }

    // Parcelable Code

    public AlbumObject(Parcel in) {
        //super(); // super() calls the parent constructor with no arguments.
        //readFromParcel(in);

        albumTitle = in.readString();
        albumArtist = in.readString();
        albumArtURI = in.readString();
        albumTrackCount = in.readInt();
        in.readTypedList(songObjectList, SongObject.CREATOR);
    }

    public static final Parcelable.Creator<AlbumObject> CREATOR = new Parcelable.Creator<AlbumObject>() {

        public AlbumObject createFromParcel(Parcel in) {

            return new AlbumObject(in);
        }

        public AlbumObject[] newArray(int size) {

            return new AlbumObject[size];
        }

    };

    public void readFromParcel(Parcel in) {
        albumArtURI = in.readString();
        albumTitle = in.readString();
        albumArtist = in.readString();

        albumTrackCount = in.readInt();
        in.readTypedList(songObjectList, SongObject.CREATOR);

    }
    public int describeContents() {

        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumArtURI);
        dest.writeString(albumTitle);
        dest.writeString(albumArtist);

        dest.writeInt(albumTrackCount);
        dest.writeTypedList(songObjectList);

    }

}
