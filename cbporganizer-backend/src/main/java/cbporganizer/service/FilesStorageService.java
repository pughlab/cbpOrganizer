package cbporganizer.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FilesStorageService {

    void init();

    void save(MultipartFile file, String userId);

    List<String> getFiles(String userId);
}
