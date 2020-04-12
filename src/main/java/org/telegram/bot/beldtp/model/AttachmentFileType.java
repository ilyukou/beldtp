package org.telegram.bot.beldtp.model;

public enum AttachmentFileType {
    PHOTO("jpeg", "image/jpeg"),
    VIDEO("mp4","video/mp4"),

    PHOTO_PNG("png", "image/png"),
    PHOTO_JPG("jpg", "image/jpeg"),
    PDF("pdf", "application/pdf"),
    DOCX("docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    private String fileExtension;
    private String mimeType;

    AttachmentFileType(String fileExtension, String mimeType) {
        this.fileExtension = fileExtension;
        this.mimeType = mimeType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getMimeType() {
        return mimeType;
    }
}
