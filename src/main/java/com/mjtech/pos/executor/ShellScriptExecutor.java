package com.mjtech.pos.executor;

import com.mjtech.pos.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Slf4j
@Component
public class ShellScriptExecutor implements Executor {

    @Override
    public void execute(Map<String, String> params) {
        try {
            String fileName = params.get(Constants.SCRIPT_FILE);

            if(fileName == null) {
                log.error("File is null. File is required to execute the script.");
                return;
            }

            // Path to the shell script
            String scriptPath = getResourceFilePath("classpath:"+fileName);
            // Create the process builder
            ProcessBuilder processBuilder = new ProcessBuilder(scriptPath);

            // Start the process
            Process process = processBuilder.start();

            // Read the output of the process
            String output = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output = line;
            }

            // Read the error stream
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String stdErrorLine;
            StringBuilder errorOutput = new StringBuilder();
            while ((stdErrorLine = stdError.readLine()) != null) {
                errorOutput.append(stdErrorLine).append("\n");
            }
            log.error("Error: "+ errorOutput);

            // Wait for the process to complete
            int exitCode = process.waitFor();


            if(exitCode ==0) {
                log.info("Script execution completed successfully with exit code: " + exitCode);
                params.put(Constants.OUTPUT_FILE, output);
            } else {
                log.error("Script execution completed with failure with exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            log.error("Failed while executing shell script", e);
            e.printStackTrace();
        }
    }

    public String getResourceFilePath(String resourcePath) throws IOException {
        File file = ResourceUtils.getFile(resourcePath);
        return file.getAbsolutePath();
    }
}
