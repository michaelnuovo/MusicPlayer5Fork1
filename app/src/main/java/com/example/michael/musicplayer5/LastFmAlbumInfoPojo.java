package com.example.michael.musicplayer5;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by michael on 12/23/15.
 */

public class LastFmAlbumInfoPojo {

    @SerializedName("album")
    public Album album;

    public class Album {

        @SerializedName("name")
        String name;

        //@SerializedName("artist")
       // String artist;

       // @SerializedName("mbid")
       // String mbid;

        //@SerializedName("url")
        //String url;

        @SerializedName("image")
        List<Image> images; // class

        //@SerializedName("listeners")
        //String listeners;

        //@SerializedName("playcount")
        //String playcount;

        //@SerializedName("tracks")
        //Tracks tracks; // class

        //@SerializedName("wiki")
        //Wiki wiki;

       public class Image{

            @SerializedName("#text")
            String text;

            @SerializedName("size")
            String size;
       }

       // public class Tracks{
            //empty
      //  }

      //  public class Wiki{
            //empty
      //  }
    }
}