package org.telegram.bot.beldtp.model;

import javax.persistence.*;

@Entity
@Table(name = "media")
public class AttachmentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long uploadDate;

    @Lob
    private String caption;

    private AttachmentFileType attachmentFileType;

    @ManyToOne
    @JoinColumn(name = "incident_id")
    private Incident incident;

    private String fileId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "resources_id")
    private Resource resource;

    public AttachmentFile() {
    }

    public AttachmentFile(String caption, AttachmentFileType attachmentFileType, String fileId) {
        this.caption = caption;
        this.attachmentFileType = attachmentFileType;
        this.fileId = fileId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public AttachmentFileType getAttachmentFileType() {
        return attachmentFileType;
    }

    public void setAttachmentFileType(AttachmentFileType attachmentFileType) {
        this.attachmentFileType = attachmentFileType;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public long getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(long uploadDate) {
        this.uploadDate = uploadDate;
    }
}
