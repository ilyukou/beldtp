package org.telegram.bot.beldtp.service.impl.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.bot.beldtp.model.AttachmentFile;
import org.telegram.bot.beldtp.repository.interf.AttachmentFileRepository;
import org.telegram.bot.beldtp.service.interf.model.AttachmentFileService;

import java.util.List;

@Service
public class AttachmentFileServiceImpl implements AttachmentFileService {

    @Autowired
    private AttachmentFileRepository attachmentFileRepository;

    @Override
    public AttachmentFile save(AttachmentFile attachmentFile) {
        return attachmentFileRepository.save(attachmentFile);
    }

    @Override
    public List<AttachmentFile> get(Long incidentId) {
        return attachmentFileRepository.get(incidentId);
    }

    @Override
    public List<AttachmentFile> getAll() {
        return attachmentFileRepository.getAll();
    }

    @Override
    public void delete(AttachmentFile attachmentFile) {
        attachmentFileRepository.delete(attachmentFile);
    }
}
