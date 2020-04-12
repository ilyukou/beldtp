package org.telegram.bot.beldtp.service.interf.model;

import org.telegram.bot.beldtp.model.AttachmentFile;

import java.util.List;

public interface AttachmentFileService {
    AttachmentFile save(AttachmentFile attachmentFile);

    List<AttachmentFile> get(Long incidentId);

    List<AttachmentFile> getAll();

    void delete(AttachmentFile attachmentFile);
}
