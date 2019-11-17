package server.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "SONGS")
public class Song extends AbstractEntity {
    private String title;
    private String artist;
    private Date uploadDate = new Date();

    @JsonIgnore
    private String path;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    private Album album;

    @NotNull
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    public Song() {}

    public String getAlbumId() {
        return album.getId();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
