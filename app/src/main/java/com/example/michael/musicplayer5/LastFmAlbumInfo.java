package com.example.michael.musicplayer5;

// Album.image[n];

public class LastFmAlbumInfo {

    public class MyPojo
    {
        private Results results;

        public Results getResults ()
        {
            return results;
        }

        public void setResults (Results results)
        {
            this.results = results;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [results = "+results+"]";
        }
    }

    public class Results
    {
        private Albummatches albummatches;

        public Albummatches getAlbummatches ()
        {
            return albummatches;
        }

        public void setAlbummatches (Albummatches albummatches)
        {
            this.albummatches = albummatches;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [albummatches = "+albummatches+"]";
        }
    }

    public class Attr
    {
        private String _for;

    public String getFor ()
    {
        return _for;
    }

    public void setFor (String _for)
    {
        this._for = _for;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [for = "+_for+"]";
    }
}

public class Albummatches
{
    private Album[] album;

    public Album[] getAlbum ()
    {
        return album;
    }

    public void setAlbum (Album[] album)
    {
        this.album = album;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [album = "+album+"]";
    }
}

public class Album
{
    private String mbid;

    private String name;

    private String streamable;

    private Image[] image;

    private String artist;

    private String url;

    public String getMbid ()
    {
        return mbid;
    }

    public void setMbid (String mbid)
    {
        this.mbid = mbid;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getStreamable ()
    {
        return streamable;
    }

    public void setStreamable (String streamable)
    {
        this.streamable = streamable;
    }

    public Image[] getImage ()
    {
        return image;
    }

    public void setImage (Image[] image)
    {
        this.image = image;
    }

    public String getArtist ()
    {
        return artist;
    }

    public void setArtist (String artist)
    {
        this.artist = artist;
    }

    public String getUrl ()
    {
        return url;
    }

    public void setUrl (String url)
    {
        this.url = url;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [mbid = "+mbid+", name = "+name+", streamable = "+streamable+", image = "+image+", artist = "+artist+", url = "+url+"]";
    }
}

public class Image
{
    private String text;

    private String size;

    public String getText()
{
    return text;
}

    public void setText(String text)
{
    this.text = text;
}

    public String getSize ()
    {
        return size;
    }

    public void setSize (String size)
    {
        this.size = size;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [#text = "+text+", size = "+size+"]";
    }
}
}