package server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.models.Playlist;
import server.models.Song;

import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, String> {
  Optional<Song> findByPlaylistAndId(Playlist playlist, String id);
  Page<Song> findAllByPlaylist(Playlist playlist, Pageable pageable);
  Page<Song> findAllByUser_Username(String username, Pageable pageable);
}