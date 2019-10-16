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
import server.models.Album;
import server.models.User;
import server.repositories.AlbumRepository;
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
public class AlbumController {
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
    private MockMvc mockMvc;
    private User user1, user2;
    private Album rtu1Album1, rtu1Album2;
    private Album rtu2Album1, rtu2Album2;
    private Principal principal1 = new UsernamePasswordAuthenticationToken("rtu1", "rtuPass1");
    private Principal principal2 = new UsernamePasswordAuthenticationToken("rtu2", "rtuPass2");
    private Principal nonExistPrincipal = new UsernamePasswordAuthenticationToken("non-existing", "p");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AlbumRepository albumRepo;

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

        // Album for first rtu1
        Album album = new Album();
        album.setName("Мой первый альбом. [rtu1]");
        album.setDescription("Мой первый альбом - полностью открыт для вас! [rtu1]");
        album.setInternal(false);

        MvcResult result = mockMvc.perform(post("/api/album").contentType(contentType)
                .principal(principal1)
                .content(convertObjectToJsonString(album)))
                .andExpect(status().isOk()).andReturn();
        rtu1Album1 = objectMapper.readValue(result.getResponse().getContentAsString(), Album.class);

        album = new Album();
        album.setName("Мой приватный альбом. [rtu1]");
        album.setDescription("Мой второй альбом - скрыт от любопытного взора! [rtu1]");
        album.setInternal(true);

        result = mockMvc.perform(post("/api/album").contentType(contentType)
                .principal(principal1)
                .content(convertObjectToJsonString(album)))
                .andExpect(status().isOk()).andReturn();
        rtu1Album2 = objectMapper.readValue(result.getResponse().getContentAsString(), Album.class);

        // Album for first rtu2
        album = new Album();
        album.setName("Мой первый альбом. [rtu2]");
        album.setDescription("Мой первый альбом - полностью открыт для вас! [rtu2]");
        album.setInternal(false);

        result = mockMvc.perform(post("/api/album").contentType(contentType)
                .principal(principal2)
                .content(convertObjectToJsonString(album)))
                .andExpect(status().isOk()).andReturn();
        rtu2Album1 = objectMapper.readValue(result.getResponse().getContentAsString(), Album.class);

        album = new Album();
        album.setName("Мой приватный альбом. [rtu2]");
        album.setDescription("Мой второй альбом - скрыт от любопытного взора! [rtu2]");
        album.setInternal(true);

        result = mockMvc.perform(post("/api/album").contentType(contentType)
                .principal(principal2)
                .content(convertObjectToJsonString(album)))
                .andExpect(status().isOk()).andReturn();
        rtu2Album2 = objectMapper.readValue(result.getResponse().getContentAsString(), Album.class);
    }

    @After
    public void destroy() {
        userRepo.deleteAll();
        albumRepo.deleteAll();
    }

    // @PutMapping("/api/album/{id}") // U
    // @DeleteMapping(value = "/api/album/{id}") // D

    @Test
    public void createTest() throws Exception {
        // Try to create as non-existing user
        Album album = new Album();
        album.setName("Мой первый альбом. [nonExistPrincipal]");
        album.setDescription("Мой первый альбом - полностью открыт для вас! [nonExistPrincipal]");
        album.setInternal(false);

        mockMvc.perform(post("/api/album").contentType(contentType)
                .principal(nonExistPrincipal)
                .content(convertObjectToJsonString(album)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAlbumById() throws Exception {
        // Check access for rtu1
        mockMvc.perform(get("/api/album/{id}", rtu1Album1.getId()).contentType(contentType)
                .principal(principal1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/album/{id}", rtu1Album2.getId()).contentType(contentType)
                .principal(principal1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/album/{id}", rtu2Album1.getId()).contentType(contentType)
                .principal(principal1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/album/{id}", rtu2Album2.getId()).contentType(contentType)
                .principal(principal1))
                .andExpect(status().isNotFound());

        // Check access for rtu2
        mockMvc.perform(get("/api/album/{id}", rtu1Album1.getId()).contentType(contentType)
                .principal(principal2))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/album/{id}", rtu1Album2.getId()).contentType(contentType)
                .principal(principal2))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/album/{id}", rtu2Album1.getId()).contentType(contentType)
                .principal(principal2))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/album/{id}", rtu2Album2.getId()).contentType(contentType)
                .principal(principal2))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserAlbums() throws Exception {
        // Check access for rtu1
        mockMvc.perform(get("/api/user/{user}/albums", user1.getUsername()).contentType(contentType)
                .principal(principal1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        mockMvc.perform(get("/api/user/{user}/albums", user2.getUsername()).contentType(contentType)
                .principal(principal1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        // Check access for rtu2
        mockMvc.perform(get("/api/user/{user}/albums", user1.getUsername()).contentType(contentType)
                .principal(principal2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        mockMvc.perform(get("/api/user/{user}/albums", user2.getUsername()).contentType(contentType)
                .principal(principal2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        // Check non-existing user's albums
        mockMvc.perform(get("/api/user/non-existing-user/albums").contentType(contentType)
                .principal(principal1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    public void editAlbum() throws Exception {
        // TODO: Release test
    }

    public String convertObjectToJsonString(Object o) throws IOException {
        return (new ObjectMapper()).writeValueAsString(o);
    }
}
