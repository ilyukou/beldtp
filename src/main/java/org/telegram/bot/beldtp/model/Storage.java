package org.telegram.bot.beldtp.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "storage")
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private StorageType type;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "storage")
    private List<Resource> resources = new ArrayList<>();

    public Storage(StorageType type) {
        this.type = type;
    }

    public Storage() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StorageType getType() {
        return type;
    }

    public void setType(StorageType type) {
        this.type = type;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Storage{" +
                "id=" + id +
                ", type=" + type);

        if(resources != null){
            builder.append(", resources=" + resources
                    .stream()
                    .map(Resource::getId).collect(Collectors.toList()));
        }

        return builder.toString();
    }
}
