package cbporganizer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
                Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
            } catch (Exception e) {
                if (e instanceof FileAlreadyExistsException) {
                    throw new RuntimeException("A file of that name already exists.");
                }

                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
