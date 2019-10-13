package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Album> create(@RequestBody Album album) {
        return ResponseEntity.ok(albumService.save(album));
    }

    @GetMapping("/api/album/{id}") // R
    public ResponseEntity<?> get(@PathVariable String id) {
        return ResponseEntity.ok(albumService.findById(id));
    }

    @GetMapping("/api/user/{userId}/albums") // R - user albums
    public Page<Album> getAlbums(@PageableDefault(size = Integer.MAX_VALUE, sort = {"createDate"}, direction = Sort.Direction.DESC) Pageable pageable,
                                 @PathVariable String userId) {
        return albumService.findByUser(userService.findByUsername(userId), pageable);
    }

    @PutMapping("/api/album/{id}") // U
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Album album) {
        Album editingAlbum = albumService.findById(id);
        editingAlbum.setName(album.getName());
        editingAlbum.setDescription(album.getDescription());
        return ResponseEntity.ok(albumService.save(album));
    }

    @DeleteMapping(value = "/api/album/{id}") // D
    public ResponseEntity<?> delete(@PathVariable String id) {
        albumService.delete(albumService.findById(id));
        return ResponseEntity.ok().build();
    }
}
