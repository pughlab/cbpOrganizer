package cbporganizer.service;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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

    /*
     * Returns a list of files in the user's directory.
     */
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
    public List<String> getFolders(String userId) {
        Path userDir = getUserPath(userId);
        if (!Files.exists(userDir)) {
            return null;
        }
        List<String> ret = new LinkedList<>();
        try {
            ret = Files.walk(userDir, 1) //only one level for study names
                    .filter(path -> path.toFile().isDirectory())
                    .map(folder -> folder.getFileName().toString())
                    .collect(Collectors.toList());
            // remove the user's directory
            ret.remove(0);
        } catch (IOException e) {
            throw new RuntimeException("Could not list folders!");
        }
        return ret;
    }

    @Override
    public List<String> getFilesInFolder(String userId, String folderName) {
        Path folderPath = getUserPath(userId).resolve(folderName);
        List<String> ret = new LinkedList<>();
        try {
            ret = Files.list(folderPath)
                    .filter(Files::isRegularFile)
                    .map(file -> file.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Could not list the files in the folder!");
        }
        return ret;
    }

    @Override
    public byte[] getValidationResult(String userId, String folderName) {
        Path userDir = getUserPath(userId).resolve(folderName);
        String scriptName = "validateData.py";

        byte[] ret = null;
        try {
            // Create a temporary directory to extract the script
            Path tempDir = Files.createTempDirectory("python-script");

            // Extract the script to the temporary directory
            Path importerDir = tempDir.resolve("importer");
            Path scriptFile = importerDir.resolve(scriptName);
            copyScriptsToTempDir(importerDir);

            File outFileHtml = new File(userDir.toFile(), validationResultFileName);

            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptFile.toString(),
                    "-s", userDir.toFile().getAbsolutePath(), "-n", "-html", outFileHtml.getAbsolutePath());
            processBuilder.directory(tempDir.toFile());

            processBuilder.redirectError(new File(userDir.toFile(), "errorOut.txt"));

            Process p = processBuilder.start();
            int exitCode = p.waitFor();
            if (exitCode != 0) {
                ret = Files.readAllBytes(outFileHtml.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return ret;
        }
    }

    private String[] listValidatorFileNames() {
        // todo: traverse the directory and get all the file names
        String[] fileNames = {"__init__.py",
                "allowed_data_types.txt",
                "cbioportal_common.py",
                "cbioportalImporter.py",
                "chromosome_sizes.json",
                "cropped_validation_report_template.html.jinja",
                "data_cna_pd_annotations.txt",
                "importOncokbDiscreteCNA.py",
                "importOncokbMutation.py",
                "libImportOncokb.py",
                "metaImport.py",
                "updateOncokbAnnotations.py",
                "validateData.py",
                "validateStudies.py",
                "validation_report_template.html.jinja"};
        return fileNames;
    }

    private void copyScriptsToTempDir(Path tempDir) throws IOException {
        for (String fileName : listValidatorFileNames()) {
            copyToTempDir(tempDir, fileName);
        }
    }

    private void copyToTempDir(Path importerDir, String fileName) throws IOException {
        // Create the importer subdirectory if it doesn't exist
        Files.createDirectories(importerDir);

        Path scriptFile = importerDir.resolve(fileName);
        try (InputStream is = getClass().getResourceAsStream("/importer/" + fileName)) {
            Files.copy(is, scriptFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public byte[] getReport(String userId, String folderName) throws FileNotFoundException {
        Path userDir = getUserPath(userId).resolve(folderName);
        File outFileHtml = new File(userDir.toFile(), validationResultFileName);

        if (!outFileHtml.exists()) {
            throw new FileNotFoundException("Validation report not found! Please validate the data first.");
        }

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
    public String getReportAsString(String userId, String folderName) {
        String ret = "";
        Path userDir = getUserPath(userId).resolve(folderName);
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

    @Override
    public void deleteFiles(String userId) {
        Path userDir = getUserPath(userId);
        try {
            // Delete all files in the user's directory
            Files.walk(userDir)
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(File::delete);
            // Return success response
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
