package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import server.models.Playlist;
import server.models.Song;
import server.models.User;
import server.services.PlaylistService;
import server.services.SongService;
import server.services.UserService;

@RestController
public class PrepareController {
  @Autowired
  private UserService userService;

  @Autowired
  private PlaylistService playlistService;

  @Autowired
  private SongService songService;

  @GetMapping("/prepare")
  public ResponseEntity<?> prepare() {
    StringBuilder sb = new StringBuilder();

    User adminUser = new User();
    adminUser.setName("Admin");
    adminUser.setLastname("SuperUser");
    adminUser.setUsername("admin");
    adminUser.setPassword("$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6");
    userService.save(adminUser);

    User rusnuraUser = new User();
    rusnuraUser.setName("Руслан");
    rusnuraUser.setLastname("Тумасов");
    rusnuraUser.setUsername("rusnura");
    rusnuraUser.setPassword("$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6");
    userService.save(rusnuraUser);

    // Public and private playlists for admin
    Playlist adminPrivatePlaylist = new Playlist();
    adminPrivatePlaylist.setId("a4bdbd85-db1d-49ef-889b-a9b8a6774962");
    adminPrivatePlaylist.setName("Приватный альбом admin");
    adminPrivatePlaylist.setDescription("Приватный альбом admin");
    adminPrivatePlaylist.setInternal(true);
    adminPrivatePlaylist.setUser(adminUser);
    playlistService.save(adminPrivatePlaylist);

    Playlist adminPublicPlaylist = new Playlist();
    adminPublicPlaylist.setId("c5672c71-f09b-48bf-8f79-850a3e2627b2");
    adminPublicPlaylist.setName("Публичный альбом admin");
    adminPublicPlaylist.setDescription("Публичный альбом admin");
    adminPublicPlaylist.setInternal(false);
    adminPublicPlaylist.setUser(adminUser);
    playlistService.save(adminPublicPlaylist);

    // Public and private playlists for rusnura
    Playlist rusnuraPrivatePlaylist = new Playlist();
    rusnuraPrivatePlaylist.setId("6df3b3ff-6d22-441a-be82-e749e2e2b251");
    rusnuraPrivatePlaylist.setName("Приватный плэйлист rusnura");
    rusnuraPrivatePlaylist.setDescription("Приватный плэйлист rusnura");
    rusnuraPrivatePlaylist.setInternal(true);
    rusnuraPrivatePlaylist.setUser(rusnuraUser);
    playlistService.save(rusnuraPrivatePlaylist);

    Playlist rusnuraPublicPlaylist = new Playlist();
    rusnuraPublicPlaylist.setId("3f0d7123-782f-4531-93ef-73288f45bbdc");
    rusnuraPublicPlaylist.setName("Публичный плэйлист rusnura");
    rusnuraPublicPlaylist.setDescription("Публичный плэйлист rusnura");
    rusnuraPublicPlaylist.setInternal(false);
    rusnuraPublicPlaylist.setUser(rusnuraUser);
    playlistService.save(rusnuraPublicPlaylist);

    // Add private songs for admin
    Song privatePlaylistSong = new Song();
    privatePlaylistSong.setId("d54f8821-aaeb-4f3f-a450-dbde60c2efef");
    privatePlaylistSong.setArtist("Серебро");
    privatePlaylistSong.setTitle("Мама люба");
    privatePlaylistSong.setPlaylist(adminPrivatePlaylist);
    privatePlaylistSong.setPath("D:\\Music\\Server\\a4bdbd85-db1d-49ef-889b-a9b8a6774962\\Serebro - Мама Люба.mp3");
    songService.save(privatePlaylistSong);

    // Add public songs for admin
    Song publicPlaylistSong = new Song();
    publicPlaylistSong.setId("c5672c71-f09b-48bf-8f79-850a3e2627b2");
    publicPlaylistSong.setArtist("NINTENDO");
    publicPlaylistSong.setTitle("Буду погибать молодым");
    publicPlaylistSong.setPlaylist(adminPublicPlaylist);
    publicPlaylistSong.setPath("D:\\Music\\Server\\a4bdbd85-db1d-49ef-889b-a9b8a6774962\\NINTENDO - Буду погибать мАло. дым.mp3");
    songService.save(publicPlaylistSong);

    return ResponseEntity.ok(sb.toString());
  }
}
