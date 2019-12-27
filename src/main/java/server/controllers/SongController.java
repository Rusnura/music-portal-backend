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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.models.Playlist;
import server.models.Song;
import server.services.PlaylistService;
import server.services.SongService;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.logging.Logger;

@RestController
public class SongController {
    private static final Logger LOGGER = Logger.getLogger(SongController.class.getName());

    @Autowired
    private SongService songService;

    @Autowired
    private PlaylistService playlistService;

    @PostMapping(value = "/api/playlist/{playlistId}/song", consumes = "multipart/form-data") // C
    public ResponseEntity<Song> create(@RequestPart @Valid @NotNull @NotBlank MultipartFile audio,
                                       @PathVariable String playlistId,
                                       @RequestPart("body") Song song, Authentication authentication) throws IOException, IllegalStateException {
        Playlist playlist = playlistService.getByIdAndUser(playlistId, authentication.getName());
        return ResponseEntity.ok(songService.save(audio, playlist, song.getTitle(), song.getArtist()));
    }

    @GetMapping("/api/songs")
    public Page<Song> getMySongs(@PageableDefault(size = Integer.MAX_VALUE, sort = {"uploadDate"}, direction = Sort.Direction.DESC) Pageable pageable,
                                 Authentication authentication) {
        return songService.findByUser(authentication.getName(), pageable);
    }

    @GetMapping("/api/playlist/{playlistId}/song/{id}") // R
    public ResponseEntity<Song> get(@PathVariable String playlistId, @PathVariable String id, Authentication authentication) {
        return ResponseEntity.ok(
                songService.get(playlistService.checkAccessAndGet(playlistId, authentication.getName()), id)
        );
    }

    @GetMapping(value = "/api/playlist/{playlistId}/song/{id}/mp3") // R - mp3
    public ResponseEntity<?> getFile(@PathVariable String playlistId, @PathVariable String id, Authentication authentication) throws IOException {
        Song requestingSong = songService.get(playlistService.checkAccessAndGet(playlistId, authentication.getName()), id);
        try {
            ByteArrayResource file = songService.getMP3File(requestingSong);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + requestingSong.getArtist() + "-" + requestingSong.getTitle() +".mp3")
                    .contentLength(file.contentLength())
                    .contentType(MediaType.parseMediaType(SongService.MP3_CONTENT_TYPE))
                    .body(file);
        } catch (NoSuchFileException e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/api/playlist/{playlistId}/songs") // R - playlist
    public Page<Song> getPlaylistSongs(@PageableDefault(size = Integer.MAX_VALUE, sort = {"uploadDate"}, direction = Sort.Direction.DESC) Pageable pageable,
                                    @PathVariable String playlistId, Authentication authentication) {
        return songService.findByPlaylist(playlistService.checkAccessAndGet(playlistId, authentication.getName()), pageable);
    }

    @PutMapping(value = "/api/playlist/{playlistId}/song/{id}") // U
    public ResponseEntity<Song> update(@PathVariable String playlistId, @PathVariable String id, @RequestBody Song song,
                                       Authentication authentication) {
        Song editingSong = songService.get(playlistService.getByIdAndUser(playlistId, authentication.getName()), id);
        editingSong.setTitle(song.getTitle());
        editingSong.setArtist(song.getArtist());
        return ResponseEntity.ok(songService.save(editingSong));
    }

    @DeleteMapping(value = "/api/playlist/{playlistId}/song/{id}") // D
    public ResponseEntity<?> delete(@PathVariable String playlistId, @PathVariable String id, Authentication authentication) {
        songService.delete(songService.get(playlistService.getByIdAndUser(playlistId, authentication.getName()), id));
        return ResponseEntity.ok().build();
    }
}
