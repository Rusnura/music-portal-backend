package server.services;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import server.exceptions.IncorrectAudioException;
import server.exceptions.ResourceNotFoundException;
import server.storage.FileStorageFactory;
import server.storage.IFileStorageReader;
import server.storage.IFilesStorageWriter;
import server.models.Playlist;
import server.models.Song;
import server.repositories.SongRepository;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SongService extends AbstractService<Song> {
  private static final Logger LOGGER = Logger.getLogger(SongService.class.getName());
  public static final String MP3_CONTENT_TYPE = "audio/mp3";
  public static final String MP3_MPEG_CONTENT_TYPE = "audio/mpeg";

  private IFilesStorageWriter filesStorage;

  @Value("${music.storage.type}")
  private String audioFilesSourceType;

  @Value("${music.storage.uri}")
  private String audioFilesSource;

  @Autowired
  private SongRepository songRepo;

  @PostConstruct
  private void init() {
    filesStorage = FileStorageFactory.getFileWriter(audioFilesSourceType, audioFilesSource);
    if (filesStorage == null)
      throw new ExceptionInInitializerError("Cannot initialize a songs storage. Please, correct application.yaml file!");
  }

  public Page<Song> findByPlaylist(Playlist playlist, Pageable pageable) {
    return songRepo.findAllByPlaylist(playlist, pageable);
  }

  public Page<Song> findByUser(String username, Pageable pageable) {
    return songRepo.findAllByUser_Username(username, pageable);
  }

  public Song save(MultipartFile audioFile, Playlist playlist, String title, String artist) throws IllegalStateException, IOException {
    if (audioFile.isEmpty() || StringUtils.isEmpty(audioFile.getOriginalFilename()))
      throw new IncorrectAudioException("Uploading file is empty!");

    if (!MP3_CONTENT_TYPE.equals(audioFile.getContentType()) &&
      !MP3_MPEG_CONTENT_TYPE.equals(audioFile.getContentType()))
      throw new IncorrectAudioException("Please, upload correct MP3 file!");

    Song song = new Song();
    song.setPlaylist(playlist);
    String uploadedFile = filesStorage.write(audioFile, playlist.getId());
    if (StringUtils.isEmpty(artist) || StringUtils.isEmpty(title)) {
      try {
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext parseContext = new ParseContext();
        Mp3Parser Mp3Parser = new Mp3Parser();
        Mp3Parser.parse(audioFile.getInputStream(), handler, metadata, parseContext);
        artist = StringUtils.isEmpty(artist) ? metadata.get("creator") : artist;
        title = StringUtils.isEmpty(title) ? metadata.get("title") : title;
      } catch (SAXException | TikaException e) {
        LOGGER.log(Level.WARNING, "Cannot to get mp3 tags! Skipping...");
      }
    }
    song.setUser(playlist.getUser());
    song.setArtist((StringUtils.isEmpty(artist) ? "Unknown" : artist));
    song.setTitle((StringUtils.isEmpty(title) ? "Unknown" : title));
    song.setPath(uploadedFile);
    return save(song);
  }

  public Song get(Playlist playlist, String songId) {
    return songRepo.findByPlaylistAndId(playlist, songId).orElseThrow(() -> new ResourceNotFoundException("Song with ID='" + songId + "' not found!"));
  }

  public ByteArrayResource getMP3File(Song requestingSong) throws IOException {
    String path = requestingSong.getPath();
    LOGGER.info("*** Starting fetching a song: '" + requestingSong + "' ***");
    if (StringUtils.isEmpty(path)) {
      throw new IllegalArgumentException("Songs path was not found!");
    }
    IFileStorageReader fileReader = FileStorageFactory.getFileReader(path);
    return new ByteArrayResource(fileReader.load(path));
  }

  public void delete(Song song) {
    songRepo.delete(song);
  }
}