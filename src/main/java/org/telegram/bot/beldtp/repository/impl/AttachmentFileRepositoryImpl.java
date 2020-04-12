package org.telegram.bot.beldtp.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.telegram.bot.beldtp.model.AttachmentFile;
import org.telegram.bot.beldtp.repository.interf.AttachmentFileRepository;
import org.telegram.bot.beldtp.repository.jpa.AttachmentFileJpaRepository;

import java.util.List;

@Repository
public class AttachmentFileRepositoryImpl implements AttachmentFileRepository {

    @Autowired
    private AttachmentFileJpaRepository attachmentFileJpaRepository;

    @Override
    public List<AttachmentFile> get(Long incidentId) {
        return attachmentFileJpaRepository.findByIncident_Id(incidentId);
    }

    @Override
    public AttachmentFile save(AttachmentFile attachmentFile) {
        return attachmentFileJpaRepository.save(attachmentFile);
    }

    @Override
    public void delete(AttachmentFile attachmentFile) {
        attachmentFileJpaRepository.delete(attachmentFile);
    }

    @Override
    public List<AttachmentFile> getAll() {
        return attachmentFileJpaRepository.findAll();
    }
}
