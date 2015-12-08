package com.example.michael.musicplayer5;

import java.util.List;

/**
 * Created by michael on 11/29/15.
 */
public class SongInfo {

    public int resultCount;
    public List<Result> results;

    public class Result {

        public String wrapperType;
        public String kind;
        public Integer artistId;
        public Integer collectionId;
        public Integer trackId;
        public String artistName;
        public String collectionName;
        public String trackName;
        public String collectionCensoredName;
        public String trackCensoredName;
        public String artistViewUrl;
        public String collectionViewUrl;
        public String trackViewUrl;
        public String previewUrl;
        public String artworkUrl30;
        public String artworkUrl60;
        public String artworkUrl100;
        public Float collectionPrice;
        public Float trackPrice;
        public String releaseDate;
        public String collectionExplicitness;
        public String trackExplicitness;
        public Integer discCount;
        public Integer discNumber;
        public Integer trackCount;
        public Integer trackNumber;
        public Integer trackTimeMillis;
        public String country;
        public String currency;
        public String primaryGenreName;
        public String radioStationUrl;
        public Boolean isStreamable;
    }

}
