package org.telegram.bot.beldtp.listener;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class BackupListener {

    @Value("${s3.bucketName}")
    private String bucketName;

    @Value("${beldtp-api.ip}")
    String beldtpApi = "http://142.93.35.133:8080";

    private final String BACKUP_URL = beldtpApi + "/backup";

    private static final String BACKUP_FILE_NAME = "backup.json";

    private static final long REPEAT_TIME = 60 * 60 * 1000; // 1 hour

    @Autowired
    private AmazonS3 amazonS3;


    private RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate = REPEAT_TIME)
    public void updateBackup(){
        InputStream inputStream = new ByteArrayInputStream(getBackup().getBytes());
        amazonS3.putObject(bucketName, BACKUP_FILE_NAME, inputStream, null);
        amazonS3.setObjectAcl(bucketName, BACKUP_FILE_NAME, CannedAccessControlList.PublicRead);
        try {
            inputStream.close();
        } catch (IOException e) {
            // ignore
        }
    }

    public  String getBackup() {
        return restTemplate.getForObject(BACKUP_URL, String.class);
    }
}
