package server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "PLAYLISTS")
public class Playlist extends AbstractEntity {
  private String name;
  @Column(length = 512)
  private String description;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date createDate = new Date();
  private boolean internal = true;

  @NotNull
  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  private User user;

  @JsonIgnore
  @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Song> songs = new LinkedList<>();

  public Playlist() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public boolean isInternal() {
    return internal;
  }

  public void setInternal(boolean isPrivatePlaylist) {
    this.internal = isPrivatePlaylist;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<Song> getSongs() {
    return songs;
  }

  public void setSongs(List<Song> songs) {
    this.songs = songs;
  }

  @Override
  public String toString() {
    return "Playlist{" +
      "name='" + name + '\'' +
      ", description='" + description + '\'' +
      ", createDate=" + createDate +
      ", internal=" + internal +
      ", user=" + user +
      '}';
  }
}
