package server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.models.Album;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, String> {
    Optional<Album> findByIdAndUser_Username(String id, String username);
    Page<Album> findAllByUser_Username(String username, Pageable pageable);
    Page<Album> findAllByUser_UsernameAndInternal(String username, boolean isPublic, Pageable pageable);
}