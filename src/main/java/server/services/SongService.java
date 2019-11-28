package server.services;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import server.exceptions.IncorrectAudioException;
import server.exceptions.ResourceNotFoundException;
import server.models.Playlist;
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

    public Page<Song> findByPlaylist(Playlist playlist, Pageable pageable) {
        return songRepo.findAllByPlaylist(playlist, pageable);
    }

    public Page<Song> findByUser(String username, Pageable pageable) {
        return songRepo.findAllByUser_Username(username, pageable);
    }

    public Song save(MultipartFile audioFile, Playlist playlist, String title, String artist) throws IllegalStateException, IOException {
        // TODO: Need security implementing
        File uploadedFile;
        if (!audioFile.isEmpty() && audioFile.getOriginalFilename() != null) {
            if (!MP3_CONTENT_TYPE.equals(audioFile.getContentType())) {
                throw new IncorrectAudioException("Please, upload correct MP3 file!");
            }

            if (audioFilesDirectory.exists() && audioFilesDirectory.canWrite()) {
                File playlistDirectory = new File(audioFilesDirectory, playlist.getId());
                if (!playlistDirectory.exists()) {
                    playlistDirectory.mkdir();
                }

                if (playlistDirectory.canWrite()) {
                    try {
                        byte[] bytes = audioFile.getBytes();
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(uploadedFile = new File(playlistDirectory, audioFile.getOriginalFilename())));
                        bos.write(bytes);
                        bos.close();
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Cannot upload file: " + e.getMessage() + "!", e);
                        throw new IncorrectAudioException("Can't upload your mp3 file!");
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Playlist directory isn't writable! Playlist directory = " + playlistDirectory.getPath() +
                            ", canRead=" + playlistDirectory.canRead() +
                            ", canWrite=" + playlistDirectory.canWrite());
                    throw new IOException("Uploading directory isn't writable!");
                }
            } else {
                LOGGER.log(Level.WARNING, "Playlist directory isn't writable! Playlist directory = " + audioFilesDirectory.getPath() +
                        ", exists=" + audioFilesDirectory.exists() +
                        ", canRead=" + audioFilesDirectory.canRead() +
                        ", canWrite=" + audioFilesDirectory.canWrite());
                throw new IncorrectAudioException("User data directory isn't writable");
            }
        } else {
            throw new IncorrectAudioException("Uploading file is empty!");
        }

        Song song = new Song();
        song.setPlaylist(playlist);
        if (StringUtils.isEmpty(artist) || StringUtils.isEmpty(title)) {
            try {
                Mp3File mp3file = new Mp3File(uploadedFile);
                if (mp3file.hasId3v1Tag()) {
                    ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                    artist = StringUtils.isEmpty(artist) ? id3v1Tag.getArtist() : artist;
                    title = StringUtils.isEmpty(title) ? id3v1Tag.getTitle() : title;
                } else if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                    artist = StringUtils.isEmpty(artist) ? id3v2Tag.getArtist() : artist;
                    title = StringUtils.isEmpty(title) ? id3v2Tag.getTitle() : title;
                }
            } catch (UnsupportedTagException | InvalidDataException e) {
                LOGGER.log(Level.WARNING, "Cannot to get mp3 tags! Skipping...");
            }
        }
        song.setUser(playlist.getUser());
        song.setArtist((StringUtils.isEmpty(artist) ? "Неизвестно": artist));
        song.setTitle((StringUtils.isEmpty(title) ? "Неизвестно": title));
        song.setPath(uploadedFile.getPath());
        return save(song);
    }

    public Song get(Playlist playlist, String songId) {
        return songRepo.findByPlaylistAndId(playlist, songId).orElseThrow(() -> new ResourceNotFoundException("Song with ID='" + songId + "' not found!"));
    }

    public void delete(Song song) {
        songRepo.delete(song);
    }
}