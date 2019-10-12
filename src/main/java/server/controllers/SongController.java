package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.models.Song;
import server.services.AlbumService;
import server.services.SongService;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

@RestController
public class SongController {
    private static final Logger LOGGER = Logger.getLogger(SongController.class.getName());

    @Autowired
    private SongService songService;

    @Autowired
    private AlbumService albumService;

    @PostMapping(value = "/api/album/{albumId}/song", consumes = "multipart/form-data") // C
    public ResponseEntity<Song> create(@RequestPart @Valid @NotNull @NotBlank MultipartFile audio,
                                           @PathVariable String albumId,
                                           @RequestParam(required = false) String title,
                                           @RequestParam(required = false) String artist) throws IOException, IllegalStateException {
        return ResponseEntity.ok(songService.save(audio, albumService.findById(albumId), title, artist));
    }

    @GetMapping("/api/album/{albumId}/song/{id}") // R
    public ResponseEntity<Song> get(@PathVariable String albumId, @PathVariable String id) {
        return ResponseEntity.ok(songService.get(albumService.findById(albumId), id));
    }

    @GetMapping(value = "/api/album/{albumId}/song/{id}/mp3") // R - mp3
    public ResponseEntity<?> getFile(@PathVariable String albumId, @PathVariable String id) throws IOException {
        Song requestingSong = songService.get(albumService.findById(albumId), id);
        File file = new File(requestingSong.getPath());
        if (file.exists()) {
            Path mp3FilePath = Paths.get(file.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(mp3FilePath));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + albumId + "-" + id +".mp3")
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType(SongService.MP3_CONTENT_TYPE))
                    .body(resource);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/album/{albumId}/songs") // R - album
    public Page<Song> getAlbumSongs(@PageableDefault(size = Integer.MAX_VALUE, sort = {"uploadDate"}, direction = Sort.Direction.DESC) Pageable pageable,
                                    @PathVariable String albumId) {
        return songService.findByAlbum(albumService.findById(albumId), pageable);
    }

    @PutMapping(value = "/api/album/{albumId}/song/{id}") // U
    public ResponseEntity<Song> update(@PathVariable String albumId, @PathVariable String id, @RequestBody Song song) {
        Song editingSong = songService.get(albumService.findById(albumId), id);
        editingSong.setTitle(song.getTitle());
        editingSong.setArtist(song.getArtist());
        return ResponseEntity.ok(songService.save(editingSong));
    }

    @DeleteMapping(value = "/api/album/{albumId}/song/{id}") // D
    public ResponseEntity<?> delete(@PathVariable String albumId, @PathVariable String id) {
        songService.delete(songService.get(albumService.findById(albumId), id));
        return ResponseEntity.ok().build();
    }
}
