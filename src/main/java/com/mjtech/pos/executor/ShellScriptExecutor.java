package com.mjtech.pos.executor;

import com.mjtech.pos.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Map;

@Slf4j
@Component
public class ShellScriptExecutor implements Executor {


    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public void execute(Map<String, String> params) throws Exception {
        try {
            String fileName = params.get(Constants.SCRIPT_FILE);

            if(fileName == null) {
                log.error("File is null. File is required to execute the script.");
                return;
            }

            // Extract the script file from the JAR to a temporary file
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
            File tempFile = File.createTempFile("script", ".sh");
            tempFile.deleteOnExit();

            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // Provide executable permissions to the temporary file (optional but may be necessary)
            tempFile.setExecutable(true);
            log.info("Executing bash {}",tempFile.getAbsolutePath()+" "+Constants.CLIENT_NAME);
            // Create the process builder
            ProcessBuilder processBuilder = new ProcessBuilder("bash", tempFile.getAbsolutePath());

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
            throw e;
        }
    }

}
