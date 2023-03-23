package cbporganizer.service;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Service
public class FilesStorageServiceImpl implements FilesStorageService{

    private final Path root = Paths.get("cbpFiles");
    private final String validationResultFileName = "validated_study.html";

    @Override
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public void save(MultipartFile file, String userId) {
        Path userDir = getUserPath(userId);
        try {
            if (!Files.exists(userDir)) {
                Files.createDirectory(userDir);
            }
            Path filePath = userDir.resolve(file.getOriginalFilename());

            Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            extractGZip(filePath.toFile(), userId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Resolves the path to the user's directory by the userId.
     */
    private Path getUserPath(String userId) {
        return root.resolve(userId);
    }

    private void extractGZip(File inputFile, String userId) {
        Path userDir = getUserPath(userId);
        try {
            TarArchiveInputStream tarInput = new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(inputFile)));

            TarArchiveEntry entry;
            while((entry = tarInput.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    new File(userDir.resolve(entry.getName()).toString()).mkdirs();
                } else {
                    byte[] buffer = new byte[1024];
                    File outFile = userDir.resolve(entry.getName()).toFile();
                    outFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        int len;
                        while ((len = tarInput.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getFiles(String userId) {
        Path userDir = getUserPath(userId);
        List<String> ret = new LinkedList<>();
        try {
            ret = Files.walk(userDir)
                    .filter(Files::isRegularFile)
                    .map(file -> file.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Could not list the files!");
        } finally {
            return ret;
        }
    }

    @Override
    public byte[] getValidationResult(String userId) {
        Path userDir = getUserPath(userId);
        ClassLoader classLoader = getClass().getClassLoader();
        File validateScript = new File(classLoader.getResource("importer/validateData.py").getFile());
        File outFileHtml = new File(userDir.toFile(), validationResultFileName);

        String studyDir = "";
        // get the directory under root
        for (File f : userDir.toFile().listFiles()) {
            if (f.isDirectory()) {
                studyDir = f.getName();
                break;
            }
        }
        File studyDirFile = new File(userDir.toFile(), studyDir);

        ProcessBuilder processBuilder = new ProcessBuilder("python", validateScript.getAbsolutePath(),
                "-s", studyDirFile.getAbsolutePath(), "-n", "-html", outFileHtml.getAbsolutePath());

        byte[] ret = null;
        processBuilder.redirectError(new File(userDir.toFile(), "errorOut.txt"));
        try {
            Process p = processBuilder.start();
            int exitCode = p.waitFor();
            if (exitCode != 0) {
                ret = Files.readAllBytes(outFileHtml.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            return ret;
        }
    }

    @Override
    public byte[] getReport(String userId) {
        Path userDir = getUserPath(userId);
        File outFileHtml = new File(userDir.toFile(), validationResultFileName);

        byte[] ret = null;
        try {
            ret = Files.readAllBytes(outFileHtml.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return ret;
        }
    }

    @Override
    public String getReportAsString(String userId) {
        String ret = "";
        Path userDir = getUserPath(userId);
        File outFileHtml = new File(userDir.toFile(), validationResultFileName);

        try {
            String htmlContent = StreamUtils.copyToString(outFileHtml.toURL().openStream(), StandardCharsets.UTF_8);
            ret = htmlContent;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return ret;
        }
    }
}
