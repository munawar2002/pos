package com.mjtech.pos.executor;

import com.mjtech.pos.constant.Constants;
import com.mjtech.pos.executor.googleDrive.GoogleDriveUploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class DatabaseBackupExecutor implements Executor {

    @Autowired
    @Qualifier("shellScriptExecutor")
    private Executor shellScriptExecutor;

    @Override
    public void execute(Map<String, String> params) {
        File backupFile = null;
        try {
            params.put(Constants.SCRIPT_FILE, "script/backup_script.sh");
            shellScriptExecutor.execute(params);

            String outputFile = params.get(Constants.OUTPUT_FILE);

            if (outputFile == null) {
                log.error("Output file is null. Mysql backup script failed");
                return;
            }

            backupFile = new File(outputFile);
            GoogleDriveUploader drive =  new GoogleDriveUploader();
            drive.uploadBasic(backupFile);
        } catch (Exception e) {
            log.error("Failed while backing up database on "+ new Date(), e);
        } finally {
            if (backupFile != null) {
                backupFile.deleteOnExit();
            }
        }
    }
}
