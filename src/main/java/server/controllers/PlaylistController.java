package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import server.models.Playlist;
import server.models.User;
import server.services.PlaylistService;
import server.services.UserService;

import java.util.logging.Logger;

@RestController
public class PlaylistController {
  private static final Logger LOGGER = Logger.getLogger(PlaylistController.class.getName());

  @Autowired
  private UserService userService;

  @Autowired
  private PlaylistService playlistService;

  @PostMapping("/api/playlist") // C
  public ResponseEntity<Playlist> create(@RequestBody Playlist playlist, Authentication authentication) {
    User user = userService.findByUsername(authentication.getName());
    if (StringUtils.hasText(playlist.getName())) {
      throw new IllegalStateException("Playlist name is empty");
    }
    Playlist newPlaylist = new Playlist();
    newPlaylist.setName(playlist.getName());
    newPlaylist.setDescription(playlist.getDescription());
    newPlaylist.setUser(user);
    newPlaylist.setInternal(playlist.isInternal());
    return ResponseEntity.ok(playlistService.save(newPlaylist));
  }

  @GetMapping("/api/playlist/{id}") // R
  public ResponseEntity<?> get(@PathVariable String id, Authentication authentication) {
    return ResponseEntity.ok(playlistService.checkAccessAndGet(id, authentication.getName()));
  }

  @GetMapping("/api/playlists") // R - my playlists
  public Page<Playlist> getMyPlaylists(@PageableDefault(size = Integer.MAX_VALUE, sort = {"createDate"}, direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) {
    return playlistService.getByUser(authentication.getName(), pageable);
  }

  @GetMapping("/api/user/{username}/playlists") // R - user playlists
  public Page<Playlist> getPlaylists(@PageableDefault(size = Integer.MAX_VALUE, sort = {"createDate"}, direction = Sort.Direction.DESC) Pageable pageable,
                                     @PathVariable String username, Authentication authentication) {
    if (username.equals(authentication.getName())) {
      return playlistService.getByUser(authentication.getName(), pageable);
    }
    return playlistService.getByUser(username, false, pageable);
  }

  @PutMapping("/api/playlist/{id}") // U
  public ResponseEntity<?> update(@PathVariable String id, @RequestBody Playlist playlist, Authentication authentication) {
    Playlist editingPlaylist = playlistService.getByIdAndUser(id, authentication.getName());
    editingPlaylist.setName(playlist.getName());
    editingPlaylist.setDescription(playlist.getDescription());
    editingPlaylist.setInternal(playlist.isInternal());
    return ResponseEntity.ok(playlistService.save(editingPlaylist));
  }

  @DeleteMapping(value = "/api/playlist/{id}") // D
  public ResponseEntity<?> delete(@PathVariable String id, Authentication authentication) {
    playlistService.delete(playlistService.getByIdAndUser(id, authentication.getName()));
    return ResponseEntity.ok().build();
  }
}
