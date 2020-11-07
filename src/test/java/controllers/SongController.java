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
import server.models.Playlist;
import server.models.Song;
import server.models.User;
import server.repositories.PlaylistRepository;
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
  private Playlist rtu1Playlist1, rtu1Playlist2;
  private Playlist rtu2Playlist1, rtu2Playlist2;
  private Song rtu1Playlist1Song, rtu1Playlist2Song;
  private Song rtu2Playlist1Song, rtu2Playlist2Song;
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
  private PlaylistRepository playlistRepo;

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
  public static void tearDownAfterClass() throws Exception {
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

    Playlist playlist = new Playlist();
    playlist.setName("rtu1 playlist - public");
    playlist.setDescription("rtu1 playlist - public");
    playlist.setUser(user1);
    playlist.setInternal(false);
    rtu1Playlist1 = playlistRepo.save(playlist);

    playlist = new Playlist();
    playlist.setName("rtu1 playlist - private");
    playlist.setDescription("rtu1 playlist - private");
    playlist.setUser(user1);
    playlist.setInternal(true);
    rtu1Playlist2 = playlistRepo.save(playlist);

    playlist = new Playlist();
    playlist.setName("rtu2 playlist - public");
    playlist.setDescription("rtu2 playlist - public");
    playlist.setUser(user2);
    playlist.setInternal(false);
    rtu2Playlist1 = playlistRepo.save(playlist);

    playlist = new Playlist();
    playlist.setName("rtu2 playlist - private");
    playlist.setDescription("rtu2 playlist - private");
    playlist.setUser(user2);
    playlist.setInternal(true);
    rtu2Playlist2 = playlistRepo.save(playlist);

    MockMultipartFile mp3File = new MockMultipartFile("audio", "mp3.mp3", "audio/mp3", new FileInputStream(mp3correctFile));
    Song song = new Song();
    song.setArtist("art1");
    song.setTitle("rtu1-playlist1-song");
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/playlist/{playlistId}/song", rtu1Playlist1.getId())
      .file(mp3File)
      .content(convertObjectToJsonString(song)).contentType("multipart/form-data")
      .principal(principal1))
      .andExpect(status().isOk()).andReturn();
    rtu1Playlist1Song = objectMapper.readValue(result.getResponse().getContentAsString(), Song.class);

//    result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/playlist/{playlistId}/song", rtu1Playlist2.getId())
//            .file(mp3File)
//            .principal(principal1)
//            .content()
//            .param("artist", "art2")
//            .param("title", "rtu1-playlist2-song"))
//            .andExpect(status().isOk()).andReturn();
//    rtu1Playlist2Song = objectMapper.readValue(result.getResponse().getContentAsString(), Song.class);
//
//    result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/playlist/{playlistId}/song", rtu2Playlist1.getId())
//            .file(mp3File)
//            .principal(principal2)
//            .param("artist", "art2")
//            .param("title", "rtu2-playlist1-song"))
//            .andExpect(status().isOk()).andReturn();
//    rtu2Playlist1Song = objectMapper.readValue(result.getResponse().getContentAsString(), Song.class);
//
//    result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/playlist/{playlistId}/song", rtu2Playlist2.getId())
//            .file(mp3File)
//            .principal(principal2)
//            .param("artist", "art2")
//            .param("title", "song2"))
//            .andExpect(status().isOk()).andReturn();
//    rtu2Playlist2Song = objectMapper.readValue(result.getResponse().getContentAsString(), Song.class);
  }

  @Test
  public void createSongTest() throws Exception {
    MockMultipartFile mp3File = new MockMultipartFile("audio", "mp3.mp3", "audio/mp3", new FileInputStream(mp3correctFile));
    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/playlist/{playlistId}/song", rtu1Playlist1.getId())
      .file(mp3File)
      .principal(principal1)
      .param("artist", "art1")
      .param("title", "song1"))
      .andExpect(status().isOk());

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/playlist/{playlistId}/song", rtu1Playlist2.getId())
      .file(mp3File)
      .principal(principal1)
      .param("artist", "art2")
      .param("title", "song2"))
      .andExpect(status().isOk());

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/playlist/{playlistId}/song", rtu2Playlist1.getId())
      .file(mp3File)
      .principal(principal2)
      .param("artist", "art2")
      .param("title", "song2"))
      .andExpect(status().isOk());

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/playlist/{playlistId}/song", rtu2Playlist2.getId())
      .file(mp3File)
      .principal(principal2)
      .param("artist", "art2")
      .param("title", "song2"))
      .andExpect(status().isOk());

    // Try to upload to another user playlist
    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/playlist/{playlistId}/song", rtu1Playlist1.getId())
      .file(mp3File)
      .principal(principal2)
      .param("artist", "art2")
      .param("title", "song2"))
      .andExpect(status().isNotFound());

    // Try to upload empty file
    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/playlist/{playlistId}/song", rtu1Playlist1.getId())
      //.file(mp3File)
      .principal(principal2)
      .param("artist", "art2")
      .param("title", "song2"))
      .andExpect(status().isBadRequest());

    // Try to upload a wrong file
    MockMultipartFile incorrectMp3File = new MockMultipartFile("audio", "wrong.mp3", "image/jpg", new FileInputStream(mp3incorrectFile));
    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/playlist/{playlistId}/song", rtu1Playlist1.getId())
      .file(incorrectMp3File)
      .principal(principal1)
      .param("artist", "art2")
      .param("title", "song2"))
      .andExpect(status().isNotAcceptable());

    // Try to upload a empty file (with correct headers)
    MockMultipartFile emptyMp3File = new MockMultipartFile("audio", "empty.mp3", "audio/mp3", new FileInputStream(mp3emptyFile));
    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/playlist/{playlistId}/song", rtu1Playlist1.getId())
      .file(emptyMp3File)
      .principal(principal1)
      .param("artist", "art2")
      .param("title", "song2"))
      .andExpect(status().isNotAcceptable());
  }

  // @PutMapping(value = "/api/playlist/{playlistId}/song/{id}") // U
  // @DeleteMapping(value = "/api/playlist/{playlistId}/song/{id}") // D
  @Test
  public void getSongs() throws Exception {
    mockMvc.perform(get("/api/playlist/{playlistId}/song/{id}", rtu1Playlist1.getId(), rtu1Playlist1Song.getId()).contentType(contentType)
      .principal(principal1))
      .andExpect(status().isOk());

    mockMvc.perform(get("/api/playlist/{playlistId}/song/{id}", rtu1Playlist2.getId(), rtu1Playlist2Song.getId()).contentType(contentType)
      .principal(principal1))
      .andExpect(status().isOk());

    // Try to get song from public playlist from another user
    mockMvc.perform(get("/api/playlist/{playlistId}/song/{id}", rtu1Playlist1.getId(), rtu1Playlist1Song.getId()).contentType(contentType)
      .principal(principal2))
      .andExpect(status().isOk());

    // Try to get song from public playlist from another user
    mockMvc.perform(get("/api/playlist/{playlistId}/song/{id}", rtu1Playlist2.getId(), rtu1Playlist2Song.getId()).contentType(contentType)
      .principal(principal2))
      .andExpect(status().isNotFound());

    // Try to get non-existing song from public playlist from owner user
    mockMvc.perform(get("/api/playlist/{playlistId}/song/{id}", rtu1Playlist1.getId(), rtu2Playlist1Song.getId()).contentType(contentType)
      .principal(principal1))
      .andExpect(status().isNotFound());

    // Try to get non-existing song from public playlist from another user
    mockMvc.perform(get("/api/playlist/{playlistId}/song/{id}", rtu1Playlist1.getId(), rtu2Playlist1Song.getId()).contentType(contentType)
      .principal(principal2))
      .andExpect(status().isNotFound());
  }

  @Test
  public void getMP3File() throws Exception {
    mockMvc.perform(get("/api/playlist/{playlistId}/song/{id}/mp3", rtu1Playlist1.getId(), rtu1Playlist1Song.getId()).contentType(contentType)
      .principal(principal1))
      .andExpect(status().isOk());

    mockMvc.perform(get("/api/playlist/{playlistId}/song/{id}/mp3", rtu1Playlist2.getId(), rtu1Playlist2Song.getId()).contentType(contentType)
      .principal(principal1))
      .andExpect(status().isOk());

    // Try to get song from public playlist from another user
    mockMvc.perform(get("/api/playlist/{playlistId}/song/{id}/mp3", rtu1Playlist1.getId(), rtu1Playlist1Song.getId()).contentType(contentType)
      .principal(principal2))
      .andExpect(status().isOk());

    // Try to get song from public playlist from another user
    mockMvc.perform(get("/api/playlist/{playlistId}/song/{id}/mp3", rtu1Playlist2.getId(), rtu1Playlist2Song.getId()).contentType(contentType)
      .principal(principal2))
      .andExpect(status().isNotFound());

    // Try to get non-existing song from public playlist from owner user
    mockMvc.perform(get("/api/playlist/{playlistId}/song/{id}/mp3", rtu1Playlist1.getId(), rtu2Playlist1Song.getId()).contentType(contentType)
      .principal(principal1))
      .andExpect(status().isNotFound());

    // Try to get non-existing song from public playlist from another user
    mockMvc.perform(get("/api/playlist/{playlistId}/song/{id}/mp3", rtu1Playlist1.getId(), rtu2Playlist1Song.getId()).contentType(contentType)
      .principal(principal2))
      .andExpect(status().isNotFound());
  }

  @Test
  public void getPlaylistSongs() throws Exception {
    // Try to get playlist's song from public playlist by owner
    mockMvc.perform(get("/api/playlist/{playlistId}/songs", rtu1Playlist1.getId()).contentType(contentType)
      .principal(principal1))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(1)));

    // Try to get playlist's song from private playlist by owner
    mockMvc.perform(get("/api/playlist/{playlistId}/songs", rtu1Playlist2.getId()).contentType(contentType)
      .principal(principal1))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(1)));

    // Try to get playlist's song from public playlist by another user
    mockMvc.perform(get("/api/playlist/{playlistId}/songs", rtu2Playlist1.getId()).contentType(contentType)
      .principal(principal1))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(1)));

    // Try to get playlist's song from private playlist by another user
    mockMvc.perform(get("/api/playlist/{playlistId}/songs", rtu2Playlist2.getId()).contentType(contentType)
      .principal(principal1))
      .andExpect(status().isNotFound());
  }

  @Test
  public void update() throws Exception {
    // Try to change song from public playlist by song owner
    Song song = new Song();
    song.setId(rtu1Playlist1Song.getId());
    song.setArtist("art1-edited");
    song.setTitle("rtu1-playlist1-song-edited");

    mockMvc.perform(put("/api/playlist/{playlistId}/song/{id}", rtu1Playlist1.getId(), rtu1Playlist1Song.getId()).contentType(contentType).principal(principal1)
      .content(convertObjectToJsonString(song)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id", is(rtu1Playlist1Song.getId())))
      .andExpect(jsonPath("$.title", is(song.getTitle())))
      .andExpect(jsonPath("$.artist", is(song.getArtist())));

    // Try to change song from private playlist by song owner
    song = new Song();
    song.setId(rtu1Playlist1Song.getId());
    song.setArtist("art1-edited");
    song.setTitle("rtu1-playlist2-song-edited");
    mockMvc.perform(put("/api/playlist/{playlistId}/song/{id}", rtu1Playlist2.getId(), rtu1Playlist2Song.getId()).contentType(contentType).principal(principal1)
      .content(convertObjectToJsonString(song)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id", is(rtu1Playlist2Song.getId())))
      .andExpect(jsonPath("$.title", is(song.getTitle())))
      .andExpect(jsonPath("$.artist", is(song.getArtist())));


    // Try to change song from public playlist by another user
    song = new Song();
    song.setId(rtu1Playlist1Song.getId());
    song.setArtist("art1-edited");
    song.setTitle("rtu2-playlist1-song-edited");

    mockMvc.perform(put("/api/playlist/{playlistId}/song/{id}", rtu1Playlist1.getId(), rtu1Playlist1Song.getId()).contentType(contentType).principal(principal2)
      .content(convertObjectToJsonString(song)))
      .andExpect(status().isNotFound());

    // Try to change song from private playlist by another user
    song = new Song();
    song.setId(rtu1Playlist2Song.getId());
    song.setArtist("art1-edited");
    song.setTitle("rtu2-playlist2-song-edited");
    mockMvc.perform(put("/api/playlist/{playlistId}/song/{id}", rtu1Playlist2.getId(), rtu1Playlist2Song.getId()).contentType(contentType).principal(principal2)
      .content(convertObjectToJsonString(song)))
      .andExpect(status().isNotFound());

    // Try to change song from another playlist
    song = new Song();
    song.setId(rtu2Playlist1Song.getId());
    song.setArtist("art1-edited");
    song.setTitle("rtu2-playlist2-song-edited");
    mockMvc.perform(put("/api/playlist/{playlistId}/song/{id}", rtu1Playlist1.getId(), rtu2Playlist1Song.getId()).contentType(contentType).principal(principal2)
      .content(convertObjectToJsonString(song)))
      .andExpect(status().isNotFound());

    // Try to change song to bad id
    song = new Song();
    song.setId(UUID.randomUUID().toString());
    song.setArtist("art1-edited");
    song.setTitle("rtu2-playlist2-song-edited");
    mockMvc.perform(put("/api/playlist/{playlistId}/song/{id}", rtu2Playlist2.getId(), rtu2Playlist2Song.getId()).contentType(contentType).principal(principal2)
      .content(convertObjectToJsonString(song)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id", is(rtu2Playlist2Song.getId())))
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
    playlistRepo.deleteAll();
    songRepo.deleteAll();
  }

  public String convertObjectToJsonString(Object o) throws IOException {
    return (new ObjectMapper()).writeValueAsString(o);
  }
}