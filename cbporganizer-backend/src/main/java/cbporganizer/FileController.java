package cbporganizer;

import cbporganizer.service.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    FilesStorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        storageService.save(file);
        return ResponseEntity.status(HttpStatus.OK).body("Uploaded the file successfully: " + file.getOriginalFilename());
    }

    @GetMapping("/")
    public ResponseEntity<List<String>> getFiles() {
        List<String> fileList = storageService.getFiles();
        return new ResponseEntity<>(fileList, HttpStatus.OK);
    }
}
