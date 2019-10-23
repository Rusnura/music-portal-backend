package server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.models.Album;
import server.models.Song;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, String> {
    Optional<Song> findByAlbumAndId(Album album, String id);
    Page<Song> findAllByAlbum(Album album, Pageable pageable);
    Page<Song> findAllByUser_Username(String username, Pageable pageable);
}