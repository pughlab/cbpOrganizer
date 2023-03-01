package cbporganizer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
    public void save(MultipartFile file) {
        if (file != null) {
            try {
                Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<String> getFiles() {
        List<String> ret = new LinkedList<>();
        try {
            ret = Files.walk(root)
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
