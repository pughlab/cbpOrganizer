package cbporganizer.service;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Service
public class FilesStorageServiceImpl implements FilesStorageService{

    private final Path root = Paths.get("cbpFiles");

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
}
