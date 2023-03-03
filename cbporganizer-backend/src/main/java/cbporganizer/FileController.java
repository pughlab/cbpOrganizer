package cbporganizer;

import cbporganizer.service.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    FilesStorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, HttpSession session) {
        String userId = getUserIdFromSession(session);

        storageService.save(file, userId);
        return ResponseEntity.status(HttpStatus.OK).body("Uploaded the file successfully: " + file.getOriginalFilename());
    }

    @GetMapping("/")
    public ResponseEntity<List<String>> getFiles(HttpSession session) {
        String userId = getUserIdFromSession(session);

        List<String> fileList = storageService.getFiles(userId);
        return new ResponseEntity<>(fileList, HttpStatus.OK);
    }

    private String getUserIdFromSession(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            throw HttpClientErrorException.Unauthorized.create(HttpStatus.UNAUTHORIZED, "Unauthorized", HttpHeaders.EMPTY, null, null);
        }
        return userId;
    }
}
