package org.telegram.bot.beldtp.repository.interf;

import org.telegram.bot.beldtp.model.AttachmentFile;

import java.util.List;

public interface AttachmentFileRepository {
    List<AttachmentFile> get(Long incidentId);

    AttachmentFile save(AttachmentFile attachmentFile);

    void delete(AttachmentFile attachmentFile);

    List<AttachmentFile> getAll();
}
