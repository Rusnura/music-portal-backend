package server.services;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import server.exceptions.IncorrectAudioException;
import server.exceptions.ResourceNotFoundException;
import server.interfaces.IFilesStorage;
import server.interfaces.impls.DirectoryFilesStorage;
import server.interfaces.impls.RemoteURLFilesStorage;
import server.models.Playlist;
import server.models.Song;
import server.repositories.SongRepository;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SongService extends AbstractService<Song> {
    private static final Logger LOGGER = Logger.getLogger(SongService.class.getName());
    public static final String MP3_CONTENT_TYPE = "audio/mp3";

    @Value("${music.dir}")
    private String audioFilesSource;
    private IFilesStorage filesStorage;

    @Autowired
    private SongRepository songRepo;

    @PostConstruct
    private void init() {
        try {
            filesStorage = new RemoteURLFilesStorage(new URL(audioFilesSource));
            LOGGER.info("***URL storage has been detected!***");
        } catch (MalformedURLException e) {
            File directory = new File(audioFilesSource);
            if (!directory.exists() || !directory.canWrite()) {
                throw new ExceptionInInitializerError("Cannot initialize a songs storage. Please, correct application.yaml file!");
            }
            filesStorage = new DirectoryFilesStorage(directory);
            LOGGER.info("***Directory storage has been detected!***");
        }
    }

    public Page<Song> findByPlaylist(Playlist playlist, Pageable pageable) {
        return songRepo.findAllByPlaylist(playlist, pageable);
    }

    public Page<Song> findByUser(String username, Pageable pageable) {
        return songRepo.findAllByUser_Username(username, pageable);
    }

    public Song save(MultipartFile audioFile, Playlist playlist, String title, String artist) throws IllegalStateException, IOException {
        File uploadedFile = null;
        // TODO: Need security implementing
        if (!audioFile.isEmpty() && audioFile.getOriginalFilename() != null) {
            if (!MP3_CONTENT_TYPE.equals(audioFile.getContentType())) {
                throw new IncorrectAudioException("Please, upload correct MP3 file!");
            }
            if (filesStorage instanceof DirectoryFilesStorage) {
                uploadedFile = (File) filesStorage.write(audioFile, playlist.getId());
            } else if (filesStorage instanceof RemoteURLFilesStorage) {

            }
        } else {
            throw new IncorrectAudioException("Uploading file is empty!");
        }

        Song song = new Song();
        song.setPlaylist(playlist);
        if (StringUtils.isEmpty(artist) || StringUtils.isEmpty(title)) {
            try {
                Mp3File mp3file = new Mp3File(uploadedFile);
                if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                    artist = StringUtils.isEmpty(artist) ? id3v2Tag.getArtist() : artist;
                    title = StringUtils.isEmpty(title) ? id3v2Tag.getTitle() : title;
                } else if (mp3file.hasId3v1Tag()) {
                    ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                    artist = StringUtils.isEmpty(artist) ? id3v1Tag.getArtist() : artist;
                    title = StringUtils.isEmpty(title) ? id3v1Tag.getTitle() : title;
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

    public ByteArrayResource getMP3File(Song requestingSong) throws IOException {
        return new ByteArrayResource(filesStorage.load(requestingSong.getPath()));
    }

    public void delete(Song song) {
        songRepo.delete(song);
    }
}