package server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.models.Playlist;

import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {
  Optional<Playlist> findByIdAndUser_Username(String id, String username);
  Page<Playlist> findAllByUser_Username(String username, Pageable pageable);
  Page<Playlist> findAllByUser_UsernameAndInternal(String username, boolean isPublic, Pageable pageable);
}