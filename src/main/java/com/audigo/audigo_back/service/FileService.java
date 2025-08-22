package com.audigo.audigo_back.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    // Image URL 반환
    String upload(MultipartFile file);

    Resource getImage(String fileName);

}
