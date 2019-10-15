package server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import server.exceptions.ResourceNotFoundException;
import server.models.Album;
import server.models.Song;
import server.repositories.SongRepository;
import javax.annotation.PostConstruct;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SongService extends AbstractService<Song> {
    private static final Logger LOGGER = Logger.getLogger(SongService.class.getName());
    public static final String MP3_CONTENT_TYPE = "audio/mp3";

    @Value("${music.dir}")
    private String audioFilesDirectoryPath;
    private File audioFilesDirectory;

    @Autowired
    private SongRepository songRepo;

    @PostConstruct
    private void init() {
        audioFilesDirectory = new File(audioFilesDirectoryPath);
    }

    public Page<Song> findByAlbum(Album album, Pageable pageable) {
        return songRepo.findAllByAlbum(album, pageable);
    }

    public Song save(MultipartFile audioFile, Album album, String title, String artist) throws IllegalStateException, IOException {
        // TODO: Need security implementing
        File uploadedFile;
        if (!audioFile.isEmpty() && audioFile.getOriginalFilename() != null) {
            if (!MP3_CONTENT_TYPE.equals(audioFile.getContentType())) {
                throw new IOException("Please, upload correct MP3 file!");
            }

            if (audioFilesDirectory.exists() && audioFilesDirectory.canWrite()) {
                File albumDirectory = new File(audioFilesDirectory, album.getId());
                if (!albumDirectory.exists()) {
                    albumDirectory.mkdir();
                }

                if (albumDirectory.canWrite()) {
                    try {
                        byte[] bytes = audioFile.getBytes();
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(uploadedFile = new File(albumDirectory, audioFile.getOriginalFilename())));
                        bos.write(bytes);
                        bos.close();
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Cannot upload file: " + e.getMessage() + "!", e);
                        throw new IOException("Can't upload your mp3 file!");
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Album directory isn't writable! Album directory = " + albumDirectory.getPath() +
                            ", canRead=" + albumDirectory.canRead() +
                            ", canWrite=" + albumDirectory.canWrite());
                    throw new IOException("Uploading directory isn't writable!");
                }
            } else {
                LOGGER.log(Level.WARNING, "Album directory isn't writable! Album directory = " + audioFilesDirectory.getPath() +
                        ", exists=" + audioFilesDirectory.exists() +
                        ", canRead=" + audioFilesDirectory.canRead() +
                        ", canWrite=" + audioFilesDirectory.canWrite());
                throw new IOException("User data directory isn't writable");
            }
        } else {
            throw new IllegalStateException("Uploading file is empty!");
        }

        Song song = new Song();
        song.setAlbum(album);
        // TODO: Include IDv3 metadata reader
        song.setArtist((StringUtils.isEmpty(artist) ? "Неизвестно": artist));
        song.setTitle((StringUtils.isEmpty(title) ? "Неизвестно": title));
        song.setPath(uploadedFile.getPath());
        return save(song);
    }

    public Song get(Album album, String songId) {
        return songRepo.findByAlbumAndId(album, songId).orElseThrow(() -> new ResourceNotFoundException("Song with ID='" + songId + "' not found!"));
    }

    public void delete(Song song) {
        songRepo.delete(song);
    }
}