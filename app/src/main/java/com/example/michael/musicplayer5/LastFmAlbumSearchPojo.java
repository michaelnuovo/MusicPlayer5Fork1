package com.example.michael.musicplayer5;

// Album.image[n];

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastFmAlbumSearchPojo {

    @SerializedName("results")
    public Results results;

    public class Results {

        //@SerializedName(“opensearch:Query”)
        //QueryInfo opensearch;

        @SerializedName("opensearch:totalResults")
        String totalResults;

        @SerializedName("opensearch:startIndex")
        String startIndex;

        @SerializedName("opensearch:itemsPerPage")
        String itemsPerPage;

        @SerializedName("albummatches")
        AlbumMatches albumMatches;

        //@SerializedName("@attr")
        //Attribute attribute;

        public class QueryInfo {

            @SerializedName("#text")
            String text;

            @SerializedName("role")
            String role;

            @SerializedName("searchTerms")
            String searchTerms;

            @SerializedName("startPage")
            String startPage;
        }

        public class AlbumMatches {

            @SerializedName("album")
            List<Album> albums;

            public class Album {

                @SerializedName("name")
                String name;

                @SerializedName("artist")
                String artist;

                @SerializedName("url")
                String url;

                @SerializedName("image")
                List<Image> images;

                @SerializedName("streamable")
                String streamable;

                @SerializedName("mbid")
                String mbid;

                public class Image {

                    @SerializedName("#text")
                    String imageUrl;

                    @SerializedName("size")
                    String imageSizeDescription;
                }
            }
        }

        public class Attribute {

            @SerializedName("for")
            String _for;
        }
    }
}