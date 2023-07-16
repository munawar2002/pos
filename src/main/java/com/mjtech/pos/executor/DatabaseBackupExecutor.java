package com.mjtech.pos.executor;

import com.mjtech.pos.constant.Constants;
import com.mjtech.pos.constant.DatabaseBackupStatus;
import com.mjtech.pos.executor.googleDrive.GoogleDriveUploader;
import com.mjtech.pos.service.DatabaseBackupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class DatabaseBackupExecutor implements Executor {

    @Autowired
    @Qualifier("shellScriptExecutor")
    private Executor shellScriptExecutor;

    @Autowired
    private DatabaseBackupService databaseBackupService;

    @Override
    public void execute(Map<String, String> params) throws Exception {
        File backupFile = null;
        try {
            params.put(Constants.SCRIPT_FILE, "script/backup_script.sh");
            var scriptStatus = DatabaseBackupStatus.PASS;
            var scriptResponse = new StringBuilder();
            try {
                shellScriptExecutor.execute(params);
            } catch (Exception e) {
                scriptResponse.append(e.getMessage()).append(Arrays.toString(e.getStackTrace()));
            }

            String outputFile = params.get(Constants.OUTPUT_FILE);

            if (outputFile == null) {
                scriptStatus = DatabaseBackupStatus.FAILED;
                var response = Map.of("scriptStatus", scriptStatus.name(),
                        "backupResponse", scriptResponse.toString(),
                        "uploadStatus", DatabaseBackupStatus.FAILED.name(),
                        "sharingStatus", DatabaseBackupStatus.FAILED.name());

                databaseBackupService.createDatabaseBackup("", response);
                log.error("Output file is null. Mysql backup script failed");
                return;
            }

            backupFile = new File(outputFile);
            GoogleDriveUploader drive =  new GoogleDriveUploader();
            var response = drive.uploadBasic(backupFile);
            response.put("scriptStatus", scriptStatus.name());
            databaseBackupService.createDatabaseBackup(outputFile, response);
        } catch (Exception e) {
            log.error("Failed while backing up database on "+ new Date(), e);
        } finally {
            if (backupFile != null) {
                backupFile.deleteOnExit();
            }
        }
    }
}
