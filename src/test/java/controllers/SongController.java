package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import server.Runner;
import server.models.Album;
import server.models.Song;
import server.models.User;
import server.repositories.AlbumRepository;
import server.repositories.SongRepository;
import server.repositories.UserRepository;
import server.services.SongService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.UUID;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Runner.class)
@ActiveProfiles("test")
@Ignore
public class SongController {
  private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
  private MockMvc mockMvc;
  private User user1, user2;
  private Album rtu1Album1, rtu1Album2;
  private Album rtu2Album1, rtu2Album2;
  private Song rtu1Album1Song, rtu1Album2Song;
  private Song rtu2Album1Song, rtu2Album2Song;
  private Principal principal1 = new UsernamePasswordAuthenticationToken("rtu1", "rtuPass1");
  private Principal principal2 = new UsernamePasswordAuthenticationToken("rtu2", "rtuPass2");
  private Principal nonExistPrincipal = new UsernamePasswordAuthenticationToken("non-existing", "p");
  private File mp3correctFile = new File(SongController.class.getResource("/mp3/MP3_700KB.mp3").getFile());
  private File mp3incorrectFile = new File(SongController.class.getResource("/mp3/wrong.mp3").getFile());
  private File mp3emptyFile = new File(SongController.class.getResource("/mp3/empty.mp3").getFile());
  private static File tempFolder = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());

  @Autowired
  private SongService songService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private AlbumRepository albumRepo;

  @Autowired
  private SongRepository songRepo;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeClass
  public static void setUpBeforeClass() {
    if (tempFolder != null && !tempFolder.exists()) {
      tempFolder.mkdirs();
    }
  }

  @AfterClass
  public static void  tearDownAfterClass() throws Exception {
    if (tempFolder != null && tempFolder.exists()) {
      tempFolder.delete();
    }
    tempFolder = null;
  }

  @Before
  public void setup() throws Exception {
    this.mockMvc = webAppContextSetup(webApplicationContext).build();

    // mock config
    ReflectionTestUtils.setField(songService, "audioFilesDirectoryPath", tempFolder.getPath());
    ReflectionTestUtils.setField(songService, "audioFilesDirectory", tempFolder);

    user1 = new User();
    user1.setUsername("rtu1");
    user1.setPassword("rtuPass1");
    user1.setName("Rus");
    user1.setLastname("Tum");
    userRepo.save(user1);

    user2 = new User();
    user2.setUsername("rtu2");
    user2.setPassword("rtuPass2");
    user2.setName("Rus");
    user2.setLastname("Tum");
    userRepo.save(user2);

    Album album = new Album();
    album.setName("rtu1 album - public");
    album.setDescription("rtu1 album - public");
    album.setUser(user1);
    album.setInternal(false);
    rtu1Album1 = albumRepo.save(album);

    album = new Album();
    album.setName("rtu1 album - private");
    album.setDescription("rtu1 album - private");
    album.setUser(user1);
    album.setInternal(true);
    rtu1Album2 = albumRepo.save(album);

    album = new Album();
    album.setName("rtu2 album - public");
    album.setDescription("rtu2 album - public");
    album.setUser(user2);
    album.setInternal(false);
    rtu2Album1 = albumRepo.save(album);

    album = new Album();
    album.setName("rtu2 album - private");
    album.setDescription("rtu2 album - private");
    album.setUser(user2);
    album.setInternal(true);
    rtu2Album2 = albumRepo.save(album);

    MockMultipartFile mp3File = new MockMultipartFile("audio", "mp3.mp3", "audio/mp3", new FileInputStream(mp3correctFile));
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/album/{albumId}/song", rtu1Album1.getId())
            .file(mp3File)
            .principal(principal1)
            .param("artist", "art1")
            .param("title", "rtu1-album1-song"))
            .andExpect(status().isOk()).andReturn();
    rtu1Album1Song = objectMapper.readValue(result.getResponse().getContentAsString(), Song.class);

    result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/album/{albumId}/song", rtu1Album2.getId())
            .file(mp3File)
            .principal(principal1)
            .param("artist", "art2")
            .param("title", "rtu1-album2-song"))
            .andExpect(status().isOk()).andReturn();
    rtu1Album2Song = objectMapper.readValue(result.getResponse().getContentAsString(), Song.class);

    result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/album/{albumId}/song", rtu2Album1.getId())
            .file(mp3File)
            .principal(principal2)
            .param("artist", "art2")
            .param("title", "rtu2-album1-song"))
            .andExpect(status().isOk()).andReturn();
    rtu2Album1Song = objectMapper.readValue(result.getResponse().getContentAsString(), Song.class);

    result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/album/{albumId}/song", rtu2Album2.getId())
            .file(mp3File)
            .principal(principal2)
            .param("artist", "art2")
            .param("title", "song2"))
            .andExpect(status().isOk()).andReturn();
    rtu2Album2Song = objectMapper.readValue(result.getResponse().getContentAsString(), Song.class);
  }

  @Test
  public void createSongTest() throws Exception {
    MockMultipartFile mp3File = new MockMultipartFile("audio", "mp3.mp3", "audio/mp3", new FileInputStream(mp3correctFile));
    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/album/{albumId}/song", rtu1Album1.getId())
            .file(mp3File)
            .principal(principal1)
            .param("artist", "art1")
            .param("title", "song1"))
            .andExpect(status().isOk());

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/album/{albumId}/song", rtu1Album2.getId())
            .file(mp3File)
            .principal(principal1)
            .param("artist", "art2")
            .param("title", "song2"))
            .andExpect(status().isOk());

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/album/{albumId}/song", rtu2Album1.getId())
            .file(mp3File)
            .principal(principal2)
            .param("artist", "art2")
            .param("title", "song2"))
            .andExpect(status().isOk());

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/album/{albumId}/song", rtu2Album2.getId())
            .file(mp3File)
            .principal(principal2)
            .param("artist", "art2")
            .param("title", "song2"))
            .andExpect(status().isOk());

    // Try to upload to another user album
    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/album/{albumId}/song", rtu1Album1.getId())
            .file(mp3File)
            .principal(principal2)
            .param("artist", "art2")
            .param("title", "song2"))
            .andExpect(status().isNotFound());

    // Try to upload empty file
    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/album/{albumId}/song", rtu1Album1.getId())
            //.file(mp3File)
            .principal(principal2)
            .param("artist", "art2")
            .param("title", "song2"))
            .andExpect(status().isBadRequest());

    // Try to upload a wrong file
    MockMultipartFile incorrectMp3File = new MockMultipartFile("audio", "wrong.mp3", "image/jpg", new FileInputStream(mp3incorrectFile));
    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/album/{albumId}/song", rtu1Album1.getId())
            .file(incorrectMp3File)
            .principal(principal1)
            .param("artist", "art2")
            .param("title", "song2"))
            .andExpect(status().isNotAcceptable());

    // Try to upload a empty file (with correct headers)
    MockMultipartFile emptyMp3File = new MockMultipartFile("audio", "empty.mp3", "audio/mp3", new FileInputStream(mp3emptyFile));
    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/album/{albumId}/song", rtu1Album1.getId())
            .file(emptyMp3File)
            .principal(principal1)
            .param("artist", "art2")
            .param("title", "song2"))
            .andExpect(status().isNotAcceptable());
  }

  // @PutMapping(value = "/api/album/{albumId}/song/{id}") // U
  // @DeleteMapping(value = "/api/album/{albumId}/song/{id}") // D
  @Test
  public void getSongs() throws Exception {
    mockMvc.perform(get("/api/album/{albumId}/song/{id}", rtu1Album1.getId(), rtu1Album1Song.getId()).contentType(contentType)
            .principal(principal1))
            .andExpect(status().isOk());

    mockMvc.perform(get("/api/album/{albumId}/song/{id}", rtu1Album2.getId(), rtu1Album2Song.getId()).contentType(contentType)
            .principal(principal1))
            .andExpect(status().isOk());

    // Try to get song from public album from another user
    mockMvc.perform(get("/api/album/{albumId}/song/{id}", rtu1Album1.getId(), rtu1Album1Song.getId()).contentType(contentType)
            .principal(principal2))
            .andExpect(status().isOk());

    // Try to get song from public album from another user
    mockMvc.perform(get("/api/album/{albumId}/song/{id}", rtu1Album2.getId(), rtu1Album2Song.getId()).contentType(contentType)
            .principal(principal2))
            .andExpect(status().isNotFound());

    // Try to get non-existing song from public album from owner user
    mockMvc.perform(get("/api/album/{albumId}/song/{id}", rtu1Album1.getId(), rtu2Album1Song.getId()).contentType(contentType)
            .principal(principal1))
            .andExpect(status().isNotFound());

    // Try to get non-existing song from public album from another user
    mockMvc.perform(get("/api/album/{albumId}/song/{id}", rtu1Album1.getId(), rtu2Album1Song.getId()).contentType(contentType)
            .principal(principal2))
            .andExpect(status().isNotFound());
  }

  @Test
  public void getMP3File() throws Exception {
    mockMvc.perform(get("/api/album/{albumId}/song/{id}/mp3", rtu1Album1.getId(), rtu1Album1Song.getId()).contentType(contentType)
            .principal(principal1))
            .andExpect(status().isOk());

    mockMvc.perform(get("/api/album/{albumId}/song/{id}/mp3", rtu1Album2.getId(), rtu1Album2Song.getId()).contentType(contentType)
            .principal(principal1))
            .andExpect(status().isOk());

    // Try to get song from public album from another user
    mockMvc.perform(get("/api/album/{albumId}/song/{id}/mp3", rtu1Album1.getId(), rtu1Album1Song.getId()).contentType(contentType)
            .principal(principal2))
            .andExpect(status().isOk());

    // Try to get song from public album from another user
    mockMvc.perform(get("/api/album/{albumId}/song/{id}/mp3", rtu1Album2.getId(), rtu1Album2Song.getId()).contentType(contentType)
            .principal(principal2))
            .andExpect(status().isNotFound());

    // Try to get non-existing song from public album from owner user
    mockMvc.perform(get("/api/album/{albumId}/song/{id}/mp3", rtu1Album1.getId(), rtu2Album1Song.getId()).contentType(contentType)
            .principal(principal1))
            .andExpect(status().isNotFound());

    // Try to get non-existing song from public album from another user
    mockMvc.perform(get("/api/album/{albumId}/song/{id}/mp3", rtu1Album1.getId(), rtu2Album1Song.getId()).contentType(contentType)
            .principal(principal2))
            .andExpect(status().isNotFound());
  }

  @Test
  public void getAlbumSongs() throws Exception {
    // Try to get album's song from public album by owner
    mockMvc.perform(get("/api/album/{albumId}/songs", rtu1Album1.getId()).contentType(contentType)
            .principal(principal1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));

    // Try to get album's song from private album by owner
    mockMvc.perform(get("/api/album/{albumId}/songs", rtu1Album2.getId()).contentType(contentType)
            .principal(principal1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));

    // Try to get album's song from public album by another user
    mockMvc.perform(get("/api/album/{albumId}/songs", rtu2Album1.getId()).contentType(contentType)
            .principal(principal1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));

    // Try to get album's song from private album by another user
    mockMvc.perform(get("/api/album/{albumId}/songs", rtu2Album2.getId()).contentType(contentType)
            .principal(principal1))
            .andExpect(status().isNotFound());
  }

  @Test
  public void update() throws Exception {
    // Try to change song from public album by song owner
    Song song = new Song();
    song.setId(rtu1Album1Song.getId());
    song.setArtist("art1-edited");
    song.setTitle("rtu1-album1-song-edited");

    mockMvc.perform(put("/api/album/{albumId}/song/{id}", rtu1Album1.getId(), rtu1Album1Song.getId()).contentType(contentType).principal(principal1)
            .content(convertObjectToJsonString(song)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(rtu1Album1Song.getId())))
            .andExpect(jsonPath("$.title", is(song.getTitle())))
            .andExpect(jsonPath("$.artist", is(song.getArtist())));

    // Try to change song from private album by song owner
    song = new Song();
    song.setId(rtu1Album1Song.getId());
    song.setArtist("art1-edited");
    song.setTitle("rtu1-album2-song-edited");
    mockMvc.perform(put("/api/album/{albumId}/song/{id}", rtu1Album2.getId(), rtu1Album2Song.getId()).contentType(contentType).principal(principal1)
            .content(convertObjectToJsonString(song)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(rtu1Album2Song.getId())))
            .andExpect(jsonPath("$.title", is(song.getTitle())))
            .andExpect(jsonPath("$.artist", is(song.getArtist())));


    // Try to change song from public album by another user
    song = new Song();
    song.setId(rtu1Album1Song.getId());
    song.setArtist("art1-edited");
    song.setTitle("rtu2-album1-song-edited");

    mockMvc.perform(put("/api/album/{albumId}/song/{id}", rtu1Album1.getId(), rtu1Album1Song.getId()).contentType(contentType).principal(principal2)
            .content(convertObjectToJsonString(song)))
            .andExpect(status().isNotFound());

    // Try to change song from private album by another user
    song = new Song();
    song.setId(rtu1Album2Song.getId());
    song.setArtist("art1-edited");
    song.setTitle("rtu2-album2-song-edited");
    mockMvc.perform(put("/api/album/{albumId}/song/{id}", rtu1Album2.getId(), rtu1Album2Song.getId()).contentType(contentType).principal(principal2)
            .content(convertObjectToJsonString(song)))
            .andExpect(status().isNotFound());

    // Try to change song from another album
    song = new Song();
    song.setId(rtu2Album1Song.getId());
    song.setArtist("art1-edited");
    song.setTitle("rtu2-album2-song-edited");
    mockMvc.perform(put("/api/album/{albumId}/song/{id}", rtu1Album1.getId(), rtu2Album1Song.getId()).contentType(contentType).principal(principal2)
            .content(convertObjectToJsonString(song)))
            .andExpect(status().isNotFound());

    // Try to change song to bad id
    song = new Song();
    song.setId(UUID.randomUUID().toString());
    song.setArtist("art1-edited");
    song.setTitle("rtu2-album2-song-edited");
    mockMvc.perform(put("/api/album/{albumId}/song/{id}", rtu2Album2.getId(), rtu2Album2Song.getId()).contentType(contentType).principal(principal2)
            .content(convertObjectToJsonString(song)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(rtu2Album2Song.getId())))
            .andExpect(jsonPath("$.title", is(song.getTitle())))
            .andExpect(jsonPath("$.artist", is(song.getArtist())));
  }

  @Test
  public void delete() {
    // TODO: Release delete test
  }

  @After
  public void destroy() {
    userRepo.deleteAll();
    albumRepo.deleteAll();
    songRepo.deleteAll();
  }

  public String convertObjectToJsonString(Object o) throws IOException {
    return (new ObjectMapper()).writeValueAsString(o);
  }
}