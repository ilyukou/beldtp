package org.telegram.bot.beldtp.model;

import javax.persistence.*;

@Entity
@Table(name = "resource")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "resource")
    private AttachmentFile attachmentFile;

    @ManyToOne
    @JoinColumn(name = "storage_id", nullable = false)
    private Storage storage;

    private String fileName;

    public Resource(Storage storage, String fileName) {
        this.storage = storage;
        this.fileName = fileName;
    }

    public Resource() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AttachmentFile getAttachmentFile() {
        return attachmentFile;
    }

    public void setAttachmentFile(AttachmentFile attachmentFile) {
        this.attachmentFile = attachmentFile;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Resource{" +
                "id=" + id);

        if(attachmentFile != null){
            builder.append(", attachmentFile=" + attachmentFile);
        }

        if(storage != null){
            builder.append(", storage=" + storage);
        }

        builder.append(", fileName='" + fileName + '\'');

        builder.append('}');

        return builder.toString();
    }
}

