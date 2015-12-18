package com.example.michael.musicplayer5;

import java.util.List;

/**
 * Created by michael on 12/15/15.
 */
public class SpotifyAlbumInfo {

    public Albums albums;

    public class Albums {

        String href;
        List<Item> items;               // corresponds to "items"
        Integer limit;
        String next;      // null?
        Integer offset;
        String previous;  // null?
        Integer total6;

        public class Item {
            String album_type;
            String[] available_markets;
            Urls external_urls;
            String href;
            String id;
            List<Image> images;
            String name;
            String type;
            String uri;

            public class Urls{
                String source;
            }

            public class Image {
                Integer height;
                String url;
                Integer width;
            }
        }
    }
}

/**

 so this works
 https://api.spotify.com/v1/search?q=album:in%20utero%20artist:nirvana&type=album
 arrival = album title (Encode spaces with the hex code %20 or +)
 artist = artist (Encode spaces with the hex code %20 or +)

 here is the entire object

 {
 "albums" : {
 "href" : "https://api.spotify.com/v1/search?query=album%3Ain+utero+artist%3Anirvana&offset=0&limit=20&type=album",

 // items

 "items" : [

 // an album item

 {"album_type" : "album",
 "available_markets" : [ "AD", "AR", "AT", "AU", "BE", "BG", "BO", "BR", "CH", "CL", "CO", "CR", "CY", "CZ", "DE", "DK", "DO", "EC", "EE", "ES", "FI", "FR", "GB", "GR", "GT", "HK", "HN", "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV", "MC", "MT", "MY", "NI", "NL", "NO", "NZ", "PA", "PE", "PH", "PL", "PT", "PY", "RO", "SE", "SG", "SI", "SK", "SV", "TR", "TW", "UY" ],
 "external_urls" : {"spotify" : "https://open.spotify.com/album/7mhk75WqSVNIvxZumfCdvu"},
 "href" : "https://api.spotify.com/v1/albums/7mhk75WqSVNIvxZumfCdvu",
 "id" : "7mhk75WqSVNIvxZumfCdvu",

 // images

 "images" : [ {
 "height" : 640,
 "url" : "https://i.scdn.co/image/b45585a5798fd96457e1680a7a71df192a0eea2e",
 "width" : 640
 }, {
 "height" : 300,
 "url" : "https://i.scdn.co/image/fd42f1b5f8b7ebc06a35d28cb64634fe4789fe2f",
 "width" : 300
 }, {
 "height" : 64,
 "url" : "https://i.scdn.co/image/73dc36ae229c19eae5749ed62bd53a275027b987",
 "width" : 64
 } ],

 "name" : "In Utero - 20th Anniversary - Deluxe Edition",
 "type" : "album",
 "uri" : "spotify:album:7mhk75WqSVNIvxZumfCdvu"
 },

 // an album item

 { "album_type" : "album",
 "available_markets" : [ "AD", "AR", "AT", "AU", "BE", "BG", "BO", "BR", "CH", "CL", "CO", "CR", "CY", "CZ", "DE", "DK", "DO", "EC", "EE", "ES", "FI", "FR", "GB", "GR", "GT", "HK", "HN", "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV", "MC", "MT", "MY", "NI", "NL", "NO", "NZ", "PA", "PE", "PH", "PL", "PT", "PY", "RO", "SE", "SG", "SI", "SK", "SV", "TR", "TW", "UY" ],
 "external_urls" : {
 "spotify" : "https://open.spotify.com/album/4HACR8HgOYj1HH4vCZ3MVi"
 },
 "href" : "https://api.spotify.com/v1/albums/4HACR8HgOYj1HH4vCZ3MVi",
 "id" : "4HACR8HgOYj1HH4vCZ3MVi",
 "images" : [ {
 "height" : 640,
 "url" : "https://i.scdn.co/image/73b5704bec326d9fa4e941cdf26174731476662d",
 "width" : 640
 }, {
 "height" : 300,
 "url" : "https://i.scdn.co/image/fa3ac92cd0b6dd6ab64b18d2eae9cd93f1cd72b9",
 "width" : 300
 }, {
 "height" : 64,
 "url" : "https://i.scdn.co/image/58b830adad05e908e28202f42abfd1b1f6ff227e",
 "width" : 64
 } ],
 "name" : "In Utero - 20th Anniversary Remaster",
 "type" : "album",
 "uri" : "spotify:album:4HACR8HgOYj1HH4vCZ3MVi"
 },

 // an album item

 {
 "album_type" : "album",
 "available_markets" : [ "AD", "AR", "AT", "AU", "BE", "BG", "BO", "BR", "CH", "CL", "CO", "CR", "CY", "CZ", "DE", "DK", "DO", "EC", "EE", "ES", "FI", "FR", "GB", "GR", "GT", "HK", "HN", "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV", "MC", "MT", "MY", "NI", "NL", "NO", "NZ", "PA", "PE", "PH", "PL", "PT", "PY", "RO", "SE", "SG", "SI", "SK", "SV", "TR", "TW", "UY" ],
 "external_urls" : {
 "spotify" : "https://open.spotify.com/album/1G8ngZlhZ1VbufNbKn182h"
 },
 "href" : "https://api.spotify.com/v1/albums/1G8ngZlhZ1VbufNbKn182h",
 "id" : "1G8ngZlhZ1VbufNbKn182h",
 "images" : [ {
 "height" : 640,
 "url" : "https://i.scdn.co/image/5d32f1c93b0cead78fa3c22172c16391d5746b1e",
 "width" : 640
 }, {
 "height" : 300,
 "url" : "https://i.scdn.co/image/94a76dd33f510416b37f0c0e4837635c2873bccb",
 "width" : 300
 }, {
 "height" : 64,
 "url" : "https://i.scdn.co/image/b18504d0716d0575c88e26636fd27770de854104",
 "width" : 64
 } ],
 "name" : "In Utero - 20th Anniversary Super Deluxe",
 "type" : "album",
 "uri" : "spotify:album:1G8ngZlhZ1VbufNbKn182h"
 },

 // an album item

 {
 "album_type" : "album",
 "available_markets" : [ "CA", "MX", "US" ],
 "external_urls" : {
 "spotify" : "https://open.spotify.com/album/6ohX7moZZnF1FwYrli1OJ6"
 },
 "href" : "https://api.spotify.com/v1/albums/6ohX7moZZnF1FwYrli1OJ6",
 "id" : "6ohX7moZZnF1FwYrli1OJ6",
 "images" : [ {
 "height" : 640,
 "url" : "https://i.scdn.co/image/e827bc5d3fb0596c63b31aaa2452a7e2d9a3090a",
 "width" : 640
 }, {
 "height" : 300,
 "url" : "https://i.scdn.co/image/3b379f72ae8b6b565ba8adaaf40b44a098d3dbaa",
 "width" : 300
 }, {
 "height" : 64,
 "url" : "https://i.scdn.co/image/b08161521e788c892f3acfb6643d6a8e8eedec35",
 "width" : 64
 } ],
 "name" : "In Utero - 20th Anniversary - Deluxe Edition",
 "type" : "album",
 "uri" : "spotify:album:6ohX7moZZnF1FwYrli1OJ6"
 },

 // an album item

 {
 "album_type" : "album",
 "available_markets" : [ "CA", "CR", "DO", "GT", "HN", "MX", "NI", "PA", "SV", "US" ],
 "external_urls" : {
 "spotify" : "https://open.spotify.com/album/7wOOA7l306K8HfBKfPoafr"
 },
 "href" : "https://api.spotify.com/v1/albums/7wOOA7l306K8HfBKfPoafr",
 "id" : "7wOOA7l306K8HfBKfPoafr",
 "images" : [ {
 "height" : 640,
 "url" : "https://i.scdn.co/image/2deead8fc7c9ff7de83c57f5406cc340eafb63d7",
 "width" : 640
 }, {
 "height" : 300,
 "url" : "https://i.scdn.co/image/85ed8e478b36c6d65726b02dccedc32a7620dcce",
 "width" : 300
 }, {
 "height" : 64,
 "url" : "https://i.scdn.co/image/738b55b255fc6792dfd6e5494bc2e3adffd7e207",
 "width" : 64
 } ],
 "name" : "In Utero - 20th Anniversary Remaster",
 "type" : "album",
 "uri" : "spotify:album:7wOOA7l306K8HfBKfPoafr"
 },

 // an album item

 {
 "album_type" : "album",
 "available_markets" : [ "CA", "MX", "US" ],
 "external_urls" : {
 "spotify" : "https://open.spotify.com/album/14VVqBoDw1SxoNLW3Cj3mN"
 },
 "href" : "https://api.spotify.com/v1/albums/14VVqBoDw1SxoNLW3Cj3mN",
 "id" : "14VVqBoDw1SxoNLW3Cj3mN",
 "images" : [ {
 "height" : 640,
 "url" : "https://i.scdn.co/image/50c43de8988eb6544de3937a9823dc7cd8c6e6bc",
 "width" : 640
 }, {
 "height" : 300,
 "url" : "https://i.scdn.co/image/e73e5bcb320e2bbd2b9a509fbfffdf8ec368b39b",
 "width" : 300
 }, {
 "height" : 64,
 "url" : "https://i.scdn.co/image/e634f1777a48078c49192d9516ab098abe0a8726",
 "width" : 64
 } ],
 "name" : "In Utero - 20th Anniversary Super Deluxe",
 "type" : "album",
 "uri" : "spotify:album:14VVqBoDw1SxoNLW3Cj3mN"
 } ],

 // end of items array

 "limit" : 20,
 "next" : null,
 "offset" : 0,
 "previous" : null,
 "total" : 6
 }
 }

 so the basic object model is this

 {
 "albums" : {
 "href" : "https://api.spotify.com/v1/search?query=album%3Ain+utero+artist%3Anirvana&offset=0&limit=20&type=album",
 "items" : [ ],
 "limit" : 20,
 "next" : null,
 "offset" : 0,
 "previous" : null,
 "total" : 6
 }
 }

 and this is the model of an item in the array

 { "album_type" : "album",
 "available_markets" : [AD", "AR", "AT", etc],
 "external_urls" : {"spotify" : "https://open.spotify.com/album/4HACR8HgOYj1HH4vCZ3MVi"},
 "href" : "https://api.spotify.com/v1/albums/4HACR8HgOYj1HH4vCZ3MVi",
 "id" : "4HACR8HgOYj1HH4vCZ3MVi",
 "images" : [],
 "name" : "In Utero - 20th Anniversary Remaster",
 "type" : "album",
 "uri" : "spotify:album:4HACR8HgOYj1HH4vCZ3MVi"
 },

 and this is the images model

 {
 "height" : 640,
 "url" : "https://i.scdn.co/image/50c43de8988eb6544de3937a9823dc7cd8c6e6bc",
 "width" : 640
 }, {
 "height" : 300,
 "url" : "https://i.scdn.co/image/e73e5bcb320e2bbd2b9a509fbfffdf8ec368b39b",
 "width" : 300
 }, {
 "height" : 64,
 "url" : "https://i.scdn.co/image/e634f1777a48078c49192d9516ab098abe0a8726",
 "width" : 64
 }

 **/