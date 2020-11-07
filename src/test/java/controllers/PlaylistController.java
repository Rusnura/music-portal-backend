package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import server.Runner;
import server.models.Playlist;
import server.models.User;
import server.repositories.PlaylistRepository;
import server.repositories.UserRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Runner.class)
@ActiveProfiles("test")
public class PlaylistController {
  private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
  private MockMvc mockMvc;
  private User user1, user2;
  private Playlist rtu1Playlist1, rtu1Playlist2;
  private Playlist rtu2Playlist1, rtu2Playlist2;
  private final Principal principal1 = new UsernamePasswordAuthenticationToken("rtu1", "rtuPass1");
  private final Principal principal2 = new UsernamePasswordAuthenticationToken("rtu2", "rtuPass2");
  private final Principal nonExistPrincipal = new UsernamePasswordAuthenticationToken("non-existing", "p");

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private PlaylistRepository playlistRepo;

  @Autowired
  private ObjectMapper objectMapper;

  @Before
  public void setup() throws Exception {
    this.mockMvc = webAppContextSetup(webApplicationContext).build();

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

    // Playlist for first rtu1
    Playlist playlist = new Playlist();
    playlist.setName("Мой первый альбом. [rtu1]");
    playlist.setDescription("Мой первый альбом - полностью открыт для вас! [rtu1]");
    playlist.setInternal(false);

    MvcResult result = mockMvc.perform(post("/api/playlist").contentType(contentType)
      .principal(principal1)
      .content(convertObjectToJsonString(playlist)))
      .andExpect(status().isOk()).andReturn();
    rtu1Playlist1 = objectMapper.readValue(result.getResponse().getContentAsString(), Playlist.class);

    playlist = new Playlist();
    playlist.setName("Мой приватный альбом. [rtu1]");
    playlist.setDescription("Мой второй альбом - скрыт от любопытного взора! [rtu1]");
    playlist.setInternal(true);

    result = mockMvc.perform(post("/api/playlist").contentType(contentType)
      .principal(principal1)
      .content(convertObjectToJsonString(playlist)))
      .andExpect(status().isOk()).andReturn();
    rtu1Playlist2 = objectMapper.readValue(result.getResponse().getContentAsString(), Playlist.class);

    // Playlist for first rtu2
    playlist = new Playlist();
    playlist.setName("Мой первый альбом. [rtu2]");
    playlist.setDescription("Мой первый альбом - полностью открыт для вас! [rtu2]");
    playlist.setInternal(false);

    result = mockMvc.perform(post("/api/playlist").contentType(contentType)
      .principal(principal2)
      .content(convertObjectToJsonString(playlist)))
      .andExpect(status().isOk()).andReturn();
    rtu2Playlist1 = objectMapper.readValue(result.getResponse().getContentAsString(), Playlist.class);

    playlist = new Playlist();
    playlist.setName("Мой приватный альбом. [rtu2]");
    playlist.setDescription("Мой второй альбом - скрыт от любопытного взора! [rtu2]");
    playlist.setInternal(true);

    result = mockMvc.perform(post("/api/playlist").contentType(contentType)
      .principal(principal2)
      .content(convertObjectToJsonString(playlist)))
      .andExpect(status().isOk()).andReturn();
    rtu2Playlist2 = objectMapper.readValue(result.getResponse().getContentAsString(), Playlist.class);
  }

  @After
  public void destroy() {
    userRepo.deleteAll();
    playlistRepo.deleteAll();
  }

  // @PutMapping("/api/playlist/{id}") // U
  // @DeleteMapping(value = "/api/playlist/{id}") // D

  @Test
  public void createTest() throws Exception {
    // Try to create as non-existing user
    Playlist playlist = new Playlist();
    playlist.setName("Мой первый альбом. [nonExistPrincipal]");
    playlist.setDescription("Мой первый альбом - полностью открыт для вас! [nonExistPrincipal]");
    playlist.setInternal(false);

    mockMvc.perform(post("/api/playlist").contentType(contentType)
      .principal(nonExistPrincipal)
      .content(convertObjectToJsonString(playlist)))
      .andExpect(status().isNotFound());
  }

  @Test
  public void getPlaylistById() throws Exception {
    // Check access for rtu1
    mockMvc.perform(get("/api/playlist/{id}", rtu1Playlist1.getId()).contentType(contentType)
      .principal(principal1))
      .andExpect(status().isOk());

    mockMvc.perform(get("/api/playlist/{id}", rtu1Playlist2.getId()).contentType(contentType)
      .principal(principal1))
      .andExpect(status().isOk());

    mockMvc.perform(get("/api/playlist/{id}", rtu2Playlist1.getId()).contentType(contentType)
      .principal(principal1))
      .andExpect(status().isOk());

    mockMvc.perform(get("/api/playlist/{id}", rtu2Playlist2.getId()).contentType(contentType)
      .principal(principal1))
      .andExpect(status().isNotFound());

    // Check access for rtu2
    mockMvc.perform(get("/api/playlist/{id}", rtu1Playlist1.getId()).contentType(contentType)
      .principal(principal2))
      .andExpect(status().isOk());

    mockMvc.perform(get("/api/playlist/{id}", rtu1Playlist2.getId()).contentType(contentType)
      .principal(principal2))
      .andExpect(status().isNotFound());

    mockMvc.perform(get("/api/playlist/{id}", rtu2Playlist1.getId()).contentType(contentType)
      .principal(principal2))
      .andExpect(status().isOk());

    mockMvc.perform(get("/api/playlist/{id}", rtu2Playlist2.getId()).contentType(contentType)
      .principal(principal2))
      .andExpect(status().isOk());
  }

  @Test
  public void getUserPlaylists() throws Exception {
    // Check access for rtu1
    mockMvc.perform(get("/api/user/{user}/playlists", user1.getUsername()).contentType(contentType)
      .principal(principal1))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(2)));

    mockMvc.perform(get("/api/user/{user}/playlists", user2.getUsername()).contentType(contentType)
      .principal(principal1))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(1)));

    // Check access for rtu2
    mockMvc.perform(get("/api/user/{user}/playlists", user1.getUsername()).contentType(contentType)
      .principal(principal2))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(1)));

    mockMvc.perform(get("/api/user/{user}/playlists", user2.getUsername()).contentType(contentType)
      .principal(principal2))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(2)));

    // Check non-existing user's playlists
    mockMvc.perform(get("/api/user/non-existing-user/playlists").contentType(contentType)
      .principal(principal1))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(0)));
  }

  @Test
  public void editPlaylist() throws Exception {
    // TODO: Release test
  }

  public String convertObjectToJsonString(Object o) throws IOException {
    return (new ObjectMapper()).writeValueAsString(o);
  }
}
