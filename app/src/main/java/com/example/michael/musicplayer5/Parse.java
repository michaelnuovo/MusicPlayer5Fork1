package com.example.michael.musicplayer5;

import android.util.Log;

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

    static public String iTunesUrl(String artists){

        // our model url:
        // https://itunes.apple.com/search?term=keith+jarrett+and+charlie+haden&media=music

        String splitNameString = artists.replace(" ", "+");
        return "https://itunes.apple.com/search?term=" + splitNameString + "&media=music" + "&limit=200"; // limit is 200 per page
    }

    static public String spotifyUrl(String artist, String album){

        //Log.v("TAG","artist is "+artist);
        //Log.v("TAG","album is "+album);

        String splitNameArtist = artist.replace(" ", "%20");
        String splitNameAlbum = album.replace(" ", "%20");

        //Log.v("TAG","splitNameArtist is "+splitNameArtist);
        //Log.v("TAG","splitNameAlbum is "+splitNameAlbum);

        String jsonUrl = "https://api.spotify.com/v1/search?q=album:arrival%20artist:abba&type=album" + "&limit=50"; // limit is 50 per page
        jsonUrl = jsonUrl.replace("abba",splitNameArtist);
        jsonUrl = jsonUrl.replace("arrival",splitNameAlbum);

        //Log.v("TAG", "#@R#" + jsonUrl);

        return jsonUrl;
    }

    static public String parseArtistName(String artistName){

        //String[] splited = artistName.split("\\s+");
        String str = artistName.replace(' ', '+');
        return str;
    }
}


