package server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.models.Album;
import server.models.User;

@Repository
public interface AlbumRepository extends JpaRepository<Album, String> {
    Page<Album> findAllByUser(User user, Pageable pageable);
}