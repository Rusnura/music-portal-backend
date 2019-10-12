package server.models;

import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.util.UUID;

@MappedSuperclass
@EntityListeners({ AbstractEntity.AbstractEntityListener.class })
public abstract class AbstractEntity {
    @Id
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        return (o == this || (o instanceof AbstractEntity && uid().equals(((AbstractEntity) o).uid())));
    }

    @Override
    public int hashCode() {
        return uid().hashCode();
    }

    private String uid() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    public static class AbstractEntityListener {
        @PrePersist
        public void onPrePersist(AbstractEntity abstractEntity) {
            abstractEntity.uid();
        }
    }
}
