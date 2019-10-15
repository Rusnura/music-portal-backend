package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import server.models.Album;
import server.services.AlbumService;
import server.services.UserService;
import java.util.logging.Logger;

@RestController
public class AlbumController {
    private static final Logger LOGGER = Logger.getLogger(AlbumController.class.getName());

    @Autowired
    private UserService userService;

    @Autowired
    private AlbumService albumService;

    @PostMapping("/api/album") // C
    public ResponseEntity<Album> create(@RequestBody Album album, Authentication authentication) {
        album.setUser(userService.findByUsername(authentication.getName()));
        return ResponseEntity.ok(albumService.save(album));
    }

    @GetMapping("/api/album/{id}") // R
    public ResponseEntity<?> get(@PathVariable String id, Authentication authentication) {
        return ResponseEntity.ok(albumService.checkAccessAndGet(id, authentication.getName()));
    }

    @GetMapping("/api/user/{username}/albums") // R - user albums
    public Page<Album> getAlbums(@PageableDefault(size = Integer.MAX_VALUE, sort = {"createDate"}, direction = Sort.Direction.DESC) Pageable pageable,
                                 @PathVariable String username, Authentication authentication) {
        if (username.equals(authentication.getName())) {
            return albumService.getByUser(authentication.getName(), pageable);
        }
        return albumService.getByUser(authentication.getName(), false, pageable);
    }

    @PutMapping("/api/album/{id}") // U
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Album album, Authentication authentication) {
        Album editingAlbum = albumService.getByIdAndUser(id, authentication.getName());
        editingAlbum.setName(album.getName());
        editingAlbum.setDescription(album.getDescription());
        return ResponseEntity.ok(albumService.save(editingAlbum));
    }

    @DeleteMapping(value = "/api/album/{id}") // D
    public ResponseEntity<?> delete(@PathVariable String id, Authentication authentication) {
        albumService.delete(albumService.getByIdAndUser(id, authentication.getName()));
        return ResponseEntity.ok().build();
    }
}
