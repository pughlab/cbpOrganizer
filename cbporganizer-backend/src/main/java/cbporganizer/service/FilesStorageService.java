package cbporganizer.service;

import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {

    void init();

    void save(MultipartFile file);
}
