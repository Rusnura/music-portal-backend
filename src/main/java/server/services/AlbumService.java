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

    public Page<Album> findByUser(User user, Pageable pageable) {
        return albumRepo.findAllByUser(user, pageable);
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
