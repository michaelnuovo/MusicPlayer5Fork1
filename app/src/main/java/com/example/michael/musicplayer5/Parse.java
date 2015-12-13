package com.example.michael.musicplayer5;

/**
 * Created by michael on 12/10/15.
 */

//https://www.apple.com/itunes/affiliates/resources/documentation/itunes-store-web-service-search-api.html
// artworkUrl30":"http://is4.mzstatic.com/image/thumb/Music6/v4/68/b5/27/68b5273f-7044-8dbb-4ad1-82473837a136/source/30x30bb.jpg
    // https://itunes.apple.com/search?term=michael+jackson

    //Look up all albums for Jack Johnson:
//https://itunes.apple.com/lookup?id=909253&entity=album

//itunes.apple.com/search?term=michael+jackson&entity=album
    //https://itunes.apple.com/search?term=michael+jackson&entity=album


public class Parse {

    static public String jsonTrackInfoArraySearchUrl(String artists){

        // our model url:
        // https://itunes.apple.com/search?term=keith+jarrett+and+charlie+haden&media=music

        String splitNameString = artists.replace(" ", "+");
        return "https://itunes.apple.com/search?term=" + splitNameString + "&media=music";
    }

    static public String parseArtistName(String artistName){

        //String[] splited = artistName.split("\\s+");
        String str = artistName.replace(' ', '+');
        return str;
    }
}


