package com.audigo.audigo_back.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.audigo.audigo_back.service.FileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Tag(name = "File API", description = "File transfer API")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * fileUpload
     * @param mtpFile
     * @return
     */
    @Operation(summary = "file upload", description = "file upload.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "DBE", description = "DATABASE_ERROR", content = @Content(mediaType = "application/json"))
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String fileUpload(@Parameter(description = "업로드할 파일"
                                         , required = true
                                         , content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
                                                               , schema = @Schema(type = "string", format = "binary"))
                                       )
                             @RequestParam("file") MultipartFile mtpFile) {
        String url = fileService.upload(mtpFile);
        return url;
    }

    /**
     * getImage
     * @param fileName
     * @return
     */
    @Operation(summary = "Get Image File", description = "get image files.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "성공", content = @Content(mediaType = "IMAGE_JPEG_VALUE")),
        @ApiResponse(responseCode = "DBE", description = "DATABASE_ERROR", content = @Content(mediaType = "IMAGE_PNG_VALUE"))
    })
    @GetMapping(value = "{fileName}", produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    public Resource getImage(@PathVariable("fileName") String fileName) {
        Resource resource = fileService.getImage(fileName);
        return resource;
    }

}
