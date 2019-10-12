package server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import server.models.AbstractEntity;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractService<T extends AbstractEntity> {
    private static final Logger LOGGER = Logger.getLogger(AbstractService.class.getName());

    @Autowired
    private JpaRepository<T, String> repository;

    public List<T> findAll() {
        return repository.findAll();
    }

    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public T findById(String id) {
        return repository.findById(id).orElseThrow(() -> new IllegalStateException("Object with ID='" + id + "' not found!"));
    }

    public T save(T entity) {
        LOGGER.info("Saving: " + entity + "(" + entity.getClass().getSimpleName() + ")");
        return repository.save(entity);
    }
}
