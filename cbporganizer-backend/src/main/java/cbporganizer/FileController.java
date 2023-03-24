package cbporganizer;

import cbporganizer.service.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
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

    @GetMapping("/folders")
    public ResponseEntity<List<String>> getFolders(HttpSession session) {
        String userId = getUserIdFromSession(session);

        List<String> folderList = storageService.getFolders(userId);
        return new ResponseEntity<>(folderList, HttpStatus.OK);
    }

    @GetMapping("/files/{folderName}")
    public ResponseEntity<List<String>> getFilesInFolder(@PathVariable String folderName, HttpSession session) {
        String userId = getUserIdFromSession(session);

        List<String> fileList = storageService.getFilesInFolder(userId, folderName);
        return new ResponseEntity<>(fileList, HttpStatus.OK);
    }


    /*
     * Execute the python validation script and return the report template html
     */
    @GetMapping("/validate")
    public ResponseEntity<HttpStatus> getValidationResult(HttpSession session) {
        String userId = getUserIdFromSession(session);

        byte[] bytes = storageService.getValidationResult(userId);
        if (bytes != null) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/validation-report-blob")
    public ResponseEntity<byte[]> getReportAsBlob(HttpSession session) {
        String userId = getUserIdFromSession(session);

        byte[] bytes = storageService.getReport(userId);
        if (bytes != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.setContentDispositionFormData("inline", "validated_study.html");
            headers.setContentLength(bytes.length);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/validation-report-html")
    public ResponseEntity<String> getReportAsHtml(HttpSession session) {
        String userId = getUserIdFromSession(session);

        String htmlContent = storageService.getReportAsString(userId);
        if (htmlContent == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(htmlContent, HttpStatus.OK);
        }

    }

    private String getUserIdFromSession(HttpSession session) {
//        String userId = (String) session.getAttribute("userId");
//        if (userId == null) {
//            throw HttpClientErrorException.Unauthorized.create(HttpStatus.UNAUTHORIZED, "Unauthorized", HttpHeaders.EMPTY, null, null);
//        }
//        return userId;
        return "user-local";
    }
}
