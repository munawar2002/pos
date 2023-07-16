package com.mjtech.pos.service;

import com.mjtech.pos.constant.Constants;
import com.mjtech.pos.constant.DatabaseBackupStatus;
import com.mjtech.pos.entity.DatabaseBackup;
import com.mjtech.pos.repository.DatabaseBackupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DatabaseBackupService {

    @Autowired
    private DatabaseBackupRepository databaseBackupRepository;

    public boolean isBackupExist(Date backupDate) {
        List<DatabaseBackup> databaseBackups = databaseBackupRepository.findByBackupDateOnly(backupDate);
        if(databaseBackups.stream().anyMatch(backup -> backup.getUploadStatus() == DatabaseBackupStatus.PASS)) {
            log.debug("Backup already exist for date {}. Skipping the process!", backupDate);
            return true;
        } else if (databaseBackups.size() >= 3) {
            log.warn("Last backup try was failed for date {}. Total Retries {}, Not Gonna retry backup db again!", backupDate, databaseBackups.size());
            return true;
        } else if (databaseBackups.size() > 0) {
            log.warn("Last backup try was failed for date {}. Total Retries {} are less than 3, Gonna retry backup db again!", backupDate, databaseBackups.size());
        }

        return false;
    }

    public void createDatabaseBackup(String outputFile, Map<String, String> response) {
        DatabaseBackup databaseBackup = DatabaseBackup.builder()
                .backupFileName(outputFile)
                .backupDate(new Date())
                .clientName(Constants.CLIENT_NAME)
                .googleDriveFileId(response.get("fileId"))
                .uploadStatus(DatabaseBackupStatus.valueOf(response.get("uploadStatus")))
                .sharingStatus(DatabaseBackupStatus.valueOf(response.get("sharingStatus")))
                .backupResponse(response.get("backupResponse"))
                .scriptStatus(DatabaseBackupStatus.valueOf(response.get("scriptStatus")))
                .build();

        databaseBackupRepository.save(databaseBackup);
    }
}
