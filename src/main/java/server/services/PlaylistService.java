package server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import server.exceptions.ResourceNotFoundException;
import server.models.Playlist;
import server.repositories.PlaylistRepository;

import java.util.logging.Logger;

@Service
public class PlaylistService extends AbstractService<Playlist> {
  private static final Logger LOGGER = Logger.getLogger(PlaylistService.class.getName());

  @Autowired
  private PlaylistRepository playlistRepo;

  public Playlist get(String playlistId) {
    return playlistRepo.findById(playlistId).orElseThrow(() -> new ResourceNotFoundException("Playlist with ID='" + playlistId + "' not found!"));
  }

  public Playlist checkAccessAndGet(String playlistId, String username) throws IllegalStateException {
    Playlist playlist = get(playlistId);
    if (!playlist.isInternal()) {
      return playlist;
    }

    if (playlist.getUser().getUsername().equals(username)) {
      return playlist;
    }
    throw new ResourceNotFoundException("Playlist with ID='" + playlistId + "' not found!");
  }

  public Playlist getByIdAndUser(String playlistId, String username) {
    return playlistRepo.findByIdAndUser_Username(playlistId, username).orElseThrow(() -> new ResourceNotFoundException("Playlist with ID='" + playlistId + "' not found!"));
  }

  public Page<Playlist> getByUser(String username, Pageable pageable) {
    return playlistRepo.findAllByUser_Username(username, pageable);
  }

  public Page<Playlist> getByUser(String username, boolean isPrivate, Pageable pageable) {
    return playlistRepo.findAllByUser_UsernameAndInternal(username, isPrivate, pageable);
  }

  public Playlist save(Playlist playlist) throws IllegalStateException {
    return super.save(playlist);
  }

  public void delete(Playlist playlist) {
    playlistRepo.delete(playlist);
  }
}
