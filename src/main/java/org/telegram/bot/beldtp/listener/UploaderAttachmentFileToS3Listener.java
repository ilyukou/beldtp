package org.telegram.bot.beldtp.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.bot.beldtp.model.*;
import org.telegram.bot.beldtp.model.telegram.api.TelegramFileId;
import org.telegram.bot.beldtp.service.interf.model.AttachmentFileService;
import org.telegram.bot.beldtp.service.interf.model.IncidentService;
import org.telegram.bot.beldtp.service.interf.model.ResourcesService;
import org.telegram.bot.beldtp.service.interf.model.StorageService;
import org.telegram.bot.beldtp.util.GenerateFileNameUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UploaderAttachmentFileToS3Listener {

    private static final long REPEAT_TIME = 60 * 1000; // one minute

    private static final Logger LOGGER = LoggerFactory.getLogger(UploaderAttachmentFileToS3Listener.class);

    @Value("${bot.token}")
    private String token;

    @Autowired
    private AttachmentFileService attachmentFileService;

    @Autowired
    private ResourcesService amazonS3Service;

    @Autowired
    private ResourcesService resourcesService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private GenerateFileNameUtil generateFileNameUtil;

    private RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate = REPEAT_TIME)
    public void downloadFileFromTelegramApi() throws Exception {

        List<Incident> incidents = incidentService.get(IncidentType.BUILD);

        for (Incident incident : incidents) {
            try {
                List<AttachmentFile> attachmentFileList = attachmentFileService.get(incident.getId());

                Set<AttachmentFile> updAttachmentFile = new HashSet<>();

                for (AttachmentFile attachmentFile : attachmentFileList) {

                    if (attachmentFile.getResource() != null) {
                        break;
                    }

                    TelegramFileId telegramFileId = get(token, attachmentFile.getFileId());

                    String filename = generateFileNameUtil.generate(telegramFileId.getResult().getFilePath());

                    byte[] array = getFileAsByteArray(token, telegramFileId.getResult().getFilePath());

                    Resource resource = new Resource();
                    resource.setFileName(filename);

                    resource = amazonS3Service.save(array, resource);

                    resource.setFileName(filename);
                    resource.setAttachmentFile(attachmentFile);
                    resource.setStorage(storageService.get(StorageType.S3));

                    resource = resourcesService.save(resource);

                    attachmentFile.setResource(resource);
                    attachmentFile = attachmentFileService.save(attachmentFile);

                    updAttachmentFile.add(attachmentFile);
                }

                incident.setAttachmentFiles(updAttachmentFile);
                incident.setType(IncidentType.READY);
                incident = incidentService.save(incident);

            } catch (RuntimeException e) {
                LOGGER.warn("Error while get media from Telegram Api and send to S3", e);
            }
        }

    }

    public TelegramFileId get(String token, String fileId) {
        String url = "https://api.telegram.org/bot" + token + "/getFile?file_id=" + fileId;
        return this.restTemplate.getForObject(url, TelegramFileId.class);
    }

    public byte[] getFileAsByteArray(String token, String filePath) {
        String url = "https://api.telegram.org/file/bot" + token + "/" + filePath;
        return this.restTemplate.getForObject(url, byte[].class);
    }
}
