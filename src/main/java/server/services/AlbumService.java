package server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import server.models.Album;
import server.models.User;
import server.repositories.AlbumRepository;
import java.util.logging.Logger;

@Service
public class AlbumService extends AbstractService<Album> {
    private static final Logger LOGGER = Logger.getLogger(AlbumService.class.getName());

    @Autowired
    private AlbumRepository albumRepo;

    public Album get(String albumId) {
        return albumRepo.findById(albumId).orElseThrow(() -> new IllegalStateException("Album with ID='" + albumId + "' not found!"));
    }

    public Album checkAccessAndGet(String albumId, User user) throws IllegalStateException {
        Album album = get(albumId);
        if (!album.isInternal()) {
            return album;
        }

        if (album.getUser().equals(user)) {
            return album;
        }
        throw new IllegalStateException("Album with ID='" + albumId + "' not found!");
    }

    public Album getByIdAndUser(String albumId, User user) {
        return albumRepo.findByIdAndUser(albumId, user).orElseThrow(() -> new IllegalStateException("Album with ID='" + albumId + "' not found!"));
    }

    public Page<Album> getByUser(User user, Pageable pageable) {
        return albumRepo.findAllByUser(user, pageable);
    }

    public Page<Album> getByUser(User user, boolean isPrivate, Pageable pageable) {
        return albumRepo.findAllByUserAndInternal(user, isPrivate, pageable);
    }

    public Album save(Album album) throws IllegalStateException {
        if (StringUtils.isEmpty(album.getName())) {
            throw new IllegalStateException("Album name is empty");
        }
        return super.save(album);
    }

    public void delete(Album album) {
        albumRepo.delete(album);
    }
}
