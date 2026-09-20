package com.audigo.audigo_back.service.implement;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.audigo.audigo_back.service.FileService;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.path}")
    private String filePath;

    @Value("${file.url}")
    private String fileUrl;

    @Override
    public String upload(MultipartFile file) {
        if (file.isEmpty())
            return null;

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString(); //새로운 random ID
        String saveFileName = uuid + extension;     // 202008234234.png
        String savePath = filePath + saveFileName;  // D:/appFiles/202008234234.png

        try {
            file.transferTo(new File(savePath));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        String url = fileUrl + saveFileName;// http://localhost:8080/file/202008234234.png

        return url;
    }

    @Override
    public Resource getImage(String fileName) {
        Resource resource = null;

        try {
            resource = new UrlResource("file:" + filePath + fileName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return resource;
    }

}
