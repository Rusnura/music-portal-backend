package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import server.models.Album;
import server.models.User;
import server.services.AlbumService;
import server.services.SongService;
import server.services.UserService;

@RestController
public class PrepareController {
    @Autowired
    private UserService userService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private SongService songService;

    @GetMapping("/prepare")
    public ResponseEntity<?> prepare() {
        StringBuilder sb = new StringBuilder();

        User adminUser = new User();
        adminUser.setName("Руслан");
        adminUser.setLastname("Тумасов");
        adminUser.setUsername("admin");
        adminUser.setPassword("$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6");
        userService.save(adminUser);

//        Album firstAlbum = new Album();
//        firstAlbum.setId("admin");
//        firstAlbum.setName("First");
//        firstAlbum.setDescription("Мой первый альбом!");
//        firstAlbum.setUser(adminUser);
//        albumService.save(firstAlbum);
//        sb.append(firstAlbum.getName()).append(" был добавлен!").append("\n");

//        Album nostalgyAlbum = new Album();
//        nostalgyAlbum.setName("Ностальгический");
//        nostalgyAlbum.setDescription("Альбом моей молодости!");
//        nostalgyAlbum.setUser(adminUser);
//        albumService.save(nostalgyAlbum);
//        sb.append(nostalgyAlbum.getName()).append(" был добавлен!").append("\n");
//
//        Song song1 = new Song();
//        song1.setTitle("Это Сан Франциско");
//        song1.setArtist("Кар-Мэн");
//        song1.setAlbum(nostalgyAlbum);
//        songService.save(song1);
//        sb.append(song1.getTitle()).append(" была добавлен!").append("\n");
//
//        Song song2 = new Song();
//        song2.setTitle("HAYOT AUT");
//        song2.setArtist("SHAHZODA");
//        song2.setAlbum(firstAlbum);
//        songService.save(song2);
//        sb.append(song2.getTitle()).append(" была добавлен!").append("\n");
//
//        Song song3 = new Song();
//        song3.setTitle("Love Me (Original Mix)");
//        song3.setArtist("Morandi");
//        song3.setAlbum(nostalgyAlbum);
//        songService.save(song3);
//        sb.append(song3.getTitle()).append(" была добавлен!").append("\n");

        return ResponseEntity.ok(sb.toString());
    }
}
