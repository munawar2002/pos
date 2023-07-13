package com.mjtech.pos.executor;

import com.mjtech.pos.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;

@Component
@Slf4j
public class DatabaseBackupExecutor implements Executor {

    @Autowired
    @Qualifier("shellScriptExecutor")
    private Executor shellScriptExecutor;

    @Override
    public void execute(Map<String, String> params) {
        String outputFile = null;
        try {
            params.put(Constants.SCRIPT_FILE, "script/backup_script.sh");
            shellScriptExecutor.execute(params);

            outputFile = params.get(Constants.OUTPUT_FILE);

            if (outputFile == null) {
                log.error("Output file is null. Mysql backup script failed");
                return;
            }

            // todo: upload file to google drive.
        } catch (Exception e) {

        } finally {
            if (outputFile != null) {
                File file = new File(outputFile);
                file.deleteOnExit();
            }
        }
    }
}
