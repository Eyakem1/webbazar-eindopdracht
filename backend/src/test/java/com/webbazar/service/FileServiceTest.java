package com.webbazar.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class FileServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void constructor_createsUploadAndBooksDirectories() {
        Path uploadDir = tempDir.resolve("uploads");

        new FileService(uploadDir.toString());

        assertThat(Files.isDirectory(uploadDir)).isTrue();
        assertThat(Files.isDirectory(uploadDir.resolve("books"))).isTrue();
    }

    @Test
    void constructor_whenUploadDirIsFile_wrapsIOException() throws Exception {
        Path uploadFile = tempDir.resolve("uploads-file");
        Files.writeString(uploadFile, "not a directory");

        assertThatThrownBy(() -> new FileService(uploadFile.toString()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Kan upload mappen niet aanmaken");
    }

    @Test
    void store_withValidPdf_savesFileAndReturnsStorageKey() {
        FileService fileService = new FileService(tempDir.resolve("uploads").toString());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "../book.pdf",
                "application/pdf",
                "pdf-content".getBytes()
        );

        String storageKey = fileService.store(file);

        assertThat(storageKey)
                .startsWith("uploads/books/")
                .endsWith(".pdf");

        assertThat(Files.exists(tempDir.resolve(storageKey))).isTrue();
    }

    @Test
    void store_withNullFile_throwsIllegalArgument() {
        FileService fileService = new FileService(tempDir.resolve("uploads").toString());

        assertThatThrownBy(() -> fileService.store(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Geen bestand");
    }

    @Test
    void store_withEmptyFile_throwsIllegalArgument() {
        FileService fileService = new FileService(tempDir.resolve("uploads").toString());

        MockMultipartFile empty = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );

        assertThatThrownBy(() -> fileService.store(empty))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Geen bestand");
    }

    @Test
    void store_withTooLargeFile_throwsIllegalArgument() {
        FileService fileService = new FileService(tempDir.resolve("uploads").toString());

        MockMultipartFile tooLarge = new MockMultipartFile(
                "file",
                "large.pdf",
                "application/pdf",
                new byte[25 * 1024 * 1024 + 1]
        );

        assertThatThrownBy(() -> fileService.store(tooLarge))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("te groot");
    }

    @Test
    void store_withInvalidExtension_throwsIllegalArgument() {
        FileService fileService = new FileService(tempDir.resolve("uploads").toString());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "book.txt",
                "text/plain",
                "content".getBytes()
        );

        assertThatThrownBy(() -> fileService.store(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bestandstype");
    }

    @Test
    void store_withoutOriginalFilename_throwsBecauseExtensionIsMissing() {
        FileService fileService = new FileService(tempDir.resolve("uploads").toString());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                null,
                "application/pdf",
                "content".getBytes()
        );

        assertThatThrownBy(() -> fileService.store(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bestandstype");
    }

    @Test
    void store_withInputStreamError_wrapsIOException() throws Exception {
        FileService fileService = new FileService(tempDir.resolve("uploads").toString());

        MultipartFile file = Mockito.mock(MultipartFile.class);

        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getSize()).thenReturn(10L);
        Mockito.when(file.getOriginalFilename()).thenReturn("book.pdf");
        Mockito.when(file.getInputStream()).thenThrow(new IOException("boom"));

        assertThatThrownBy(() -> fileService.store(file))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Opslaan van bestand mislukt");
    }

    @Test
    void read_supportsStorageKeyWithUploadsPrefix() {
        FileService fileService = new FileService(tempDir.resolve("uploads").toString());

        String key = fileService.store(
                new MockMultipartFile(
                        "file",
                        "book.pdf",
                        "application/pdf",
                        "hello".getBytes()
                )
        );

        byte[] bytes = fileService.read(key);

        assertThat(new String(bytes)).isEqualTo("hello");
    }

    @Test
    void read_supportsStorageKeyWithoutUploadsPrefix() throws Exception {
        Path uploadDir = tempDir.resolve("uploads");

        FileService fileService = new FileService(uploadDir.toString());

        Files.writeString(uploadDir.resolve("manual.pdf"), "manual");

        byte[] bytes = fileService.read("manual.pdf");

        assertThat(new String(bytes)).isEqualTo("manual");
    }

    @Test
    void read_supportsBooksPrefixWithoutUploadsPrefix() throws Exception {
        Path uploadDir = tempDir.resolve("uploads");

        FileService fileService = new FileService(uploadDir.toString());

        Files.writeString(uploadDir.resolve("books/manual.pdf"), "book");

        byte[] bytes = fileService.read("books/manual.pdf");

        assertThat(new String(bytes)).isEqualTo("book");
    }

    @Test
    void read_withNullKey_throwsIllegalArgument() {
        FileService fileService = new FileService(tempDir.resolve("uploads").toString());

        assertThatThrownBy(() -> fileService.read(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Lege storage key");
    }

    @Test
    void read_withBlankKey_throwsIllegalArgument() {
        FileService fileService = new FileService(tempDir.resolve("uploads").toString());

        assertThatThrownBy(() -> fileService.read("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Lege storage key");
    }

    @Test
    void read_withPathTraversal_throwsIllegalArgument() {
        FileService fileService = new FileService(tempDir.resolve("uploads").toString());

        assertThatThrownBy(() -> fileService.read("../secret.pdf"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ongeldige storage key");
    }

    @Test
    void read_whenFileMissing_wrapsIOException() {
        FileService fileService = new FileService(tempDir.resolve("uploads").toString());

        assertThatThrownBy(() -> fileService.read("missing.pdf"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Bestand niet gevonden");
    }

    @Test
    void read_withAbsolutePathOutsideUploadRoot_throwsIllegalArgument() {
        FileService fileService = new FileService(tempDir.resolve("uploads").toString());

        assertThatThrownBy(() -> fileService.read("/tmp/secret.pdf"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pad valt buiten toegestane map");
    }

    @Test
    void contentType_forPdfFile_returnsPdfContentType() throws Exception {
        Path uploadDir = tempDir.resolve("uploads");

        FileService fileService = new FileService(uploadDir.toString());

        Files.writeString(uploadDir.resolve("manual.pdf"), "pdf");

        String type = fileService.contentType("manual.pdf");

        assertThat(type).isEqualTo("application/pdf");
    }

    @Test
    void contentType_withUploadsPrefix_returnsPdfContentType() throws Exception {
        Path uploadDir = tempDir.resolve("uploads");

        FileService fileService = new FileService(uploadDir.toString());

        Files.createDirectories(uploadDir.resolve("books"));
        Files.writeString(uploadDir.resolve("books/manual.pdf"), "pdf");

        String type = fileService.contentType("uploads/books/manual.pdf");

        assertThat(type).isEqualTo("application/pdf");
    }

    @Test
    void contentType_whenPdfFileMissing_returnsPdfContentType() {
        FileService fileService = new FileService(tempDir.resolve("uploads").toString());

        String type = fileService.contentType("missing.pdf");

        assertThat(type).isEqualTo("application/pdf");
    }

    @Test
    void contentType_whenTypeCannotBeDetected_returnsFallback() throws Exception {
        Path uploadDir = tempDir.resolve("uploads");

        FileService fileService = new FileService(uploadDir.toString());

        Files.writeString(uploadDir.resolve("file-without-extension"), "content");

        String type = fileService.contentType("file-without-extension");

        assertThat(type).isEqualTo("application/octet-stream");
    }

    @Test
    void contentType_forXmlFile_returnsXmlContentType() throws Exception {
        Path uploadDir = tempDir.resolve("uploads");

        FileService fileService = new FileService(uploadDir.toString());

        Files.writeString(uploadDir.resolve("invoice.xml"), "<invoice></invoice>");

        String type = fileService.contentType("invoice.xml");

        assertThat(type).isEqualTo("application/xml");
    }

    @Test
    void contentType_forOtherFile_returnsOctetStream() throws Exception {
        Path uploadDir = tempDir.resolve("uploads");

        FileService fileService = new FileService(uploadDir.toString());

        Files.writeString(uploadDir.resolve("file.txt"), "text");

        String type = fileService.contentType("file.txt");

        assertThat(type).isEqualTo("application/octet-stream");
    }
}