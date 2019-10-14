package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import server.models.Album;
import server.models.Song;
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
        adminUser.setName("Admin");
        adminUser.setLastname("SuperUser");
        adminUser.setUsername("admin");
        adminUser.setPassword("$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6");
        userService.save(adminUser);

        User rusnuraUser = new User();
        rusnuraUser.setName("Руслан");
        rusnuraUser.setLastname("Тумасов");
        rusnuraUser.setUsername("rusnura");
        rusnuraUser.setPassword("$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6");
        userService.save(rusnuraUser);

        // Public and private albums for admin
        Album adminPrivateAlbum = new Album();
        adminPrivateAlbum.setId("a4bdbd85-db1d-49ef-889b-a9b8a6774962");
        adminPrivateAlbum.setName("Приватный альбом admin");
        adminPrivateAlbum.setDescription("Приватный альбом admin");
        adminPrivateAlbum.setInternal(true);
        adminPrivateAlbum.setUser(adminUser);
        albumService.save(adminPrivateAlbum);

        Album adminPublicAlbum = new Album();
        adminPublicAlbum.setId("c5672c71-f09b-48bf-8f79-850a3e2627b2");
        adminPublicAlbum.setName("Публичный альбом admin");
        adminPublicAlbum.setDescription("Публичный альбом admin");
        adminPublicAlbum.setInternal(false);
        adminPublicAlbum.setUser(adminUser);
        albumService.save(adminPublicAlbum);

        // Public and private albums for rusnura
        Album rusnuraPrivateAlbum = new Album();
        rusnuraPrivateAlbum.setId("6df3b3ff-6d22-441a-be82-e749e2e2b251");
        rusnuraPrivateAlbum.setName("Приватный альбом rusnura");
        rusnuraPrivateAlbum.setDescription("Приватный альбом rusnura");
        rusnuraPrivateAlbum.setInternal(true);
        rusnuraPrivateAlbum.setUser(rusnuraUser);
        albumService.save(rusnuraPrivateAlbum);

        Album rusnuraPublicAlbum = new Album();
        rusnuraPublicAlbum.setId("3f0d7123-782f-4531-93ef-73288f45bbdc");
        rusnuraPublicAlbum.setName("Публичный альбом rusnura");
        rusnuraPublicAlbum.setDescription("Публичный альбом rusnura");
        rusnuraPublicAlbum.setInternal(false);
        rusnuraPublicAlbum.setUser(rusnuraUser);
        albumService.save(rusnuraPublicAlbum);

        // Add private songs for admin
        Song privateAlbumSong = new Song();
        privateAlbumSong.setId("d54f8821-aaeb-4f3f-a450-dbde60c2efef");
        privateAlbumSong.setArtist("Серебро");
        privateAlbumSong.setTitle("Мама люба");
        privateAlbumSong.setAlbum(adminPrivateAlbum);
        privateAlbumSong.setPath("D:\\Music\\Server\\a4bdbd85-db1d-49ef-889b-a9b8a6774962\\Serebro - Мама Люба.mp3");
        songService.save(privateAlbumSong);

        // Add public songs for admin
        Song publicAlbumSong = new Song();
        publicAlbumSong.setId("c5672c71-f09b-48bf-8f79-850a3e2627b2");
        publicAlbumSong.setArtist("NINTENDO");
        publicAlbumSong.setTitle("Буду погибать молодым");
        publicAlbumSong.setAlbum(adminPublicAlbum);
        publicAlbumSong.setPath("D:\\Music\\Server\\a4bdbd85-db1d-49ef-889b-a9b8a6774962\\NINTENDO - Буду погибать мАло. дым.mp3");
        songService.save(publicAlbumSong);

        return ResponseEntity.ok(sb.toString());
    }
}
