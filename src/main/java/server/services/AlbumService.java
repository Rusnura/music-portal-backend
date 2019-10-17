package server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import server.exceptions.ResourceNotFoundException;
import server.models.Album;
import server.repositories.AlbumRepository;
import java.util.logging.Logger;

@Service
public class AlbumService extends AbstractService<Album> {
    private static final Logger LOGGER = Logger.getLogger(AlbumService.class.getName());

    @Autowired
    private AlbumRepository albumRepo;

    public Album get(String albumId) {
        return albumRepo.findById(albumId).orElseThrow(() -> new ResourceNotFoundException("Album with ID='" + albumId + "' not found!"));
    }

    public Album checkAccessAndGet(String albumId, String username) throws IllegalStateException {
        Album album = get(albumId);
        if (!album.isInternal()) {
            return album;
        }

        if (album.getUser().getUsername().equals(username)) {
            return album;
        }
        throw new ResourceNotFoundException("Album with ID='" + albumId + "' not found!");
    }

    public Album getByIdAndUser(String albumId, String username) {
        return albumRepo.findByIdAndUser_Username(albumId, username).orElseThrow(() -> new ResourceNotFoundException("Album with ID='" + albumId + "' not found!"));
    }

    public Page<Album> getByUser(String username, Pageable pageable) {
        return albumRepo.findAllByUser_Username(username, pageable);
    }

    public Page<Album> getByUser(String username, boolean isPrivate, Pageable pageable) {
        return albumRepo.findAllByUser_UsernameAndInternal(username, isPrivate, pageable);
    }

    public Album save(Album album) throws IllegalStateException {
        return super.save(album);
    }

    public void delete(Album album) {
        albumRepo.delete(album);
    }
}
