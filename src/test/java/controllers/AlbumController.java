package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import server.Runner;
import server.models.Album;
import server.models.User;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import java.io.IOException;
import java.nio.charset.Charset;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Runner.class)
@ActiveProfiles("test")
@Ignore
public class AlbumController {
    private User user1, user2;
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    // @PostMapping("/api/album") // C
    // @GetMapping("/api/album/{id}") // R
    // @GetMapping("/api/user/{username}/albums") // R - user albums
    // @PutMapping("/api/album/{id}") // U
    // @DeleteMapping(value = "/api/album/{id}") // D
    @Test
    public void createAlbumTest() throws Exception {
        Album album1 = new Album();
        album1.setName("Мой первый альбом.");
        album1.setDescription("Мой первый альбом - полностью открыт для вас!");
        album1.setInternal(false);

        Album album2 = new Album();
        album2.setName("Мой приватный альбом.");
        album2.setDescription("Мой второй альбом - скрыт от любопытного взора!");
        album2.setInternal(false);

        mockMvc.perform(post("/api/album").contentType(contentType)
            .content(convertObjectToJsonString(album2)))
            .andExpect(status().isOk());
    }

    public String convertObjectToJsonString(Object o) throws IOException {
        return (new ObjectMapper()).writeValueAsString(o);
    }
}
