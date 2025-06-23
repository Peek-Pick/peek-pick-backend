package org.beep.sbpp.util;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
public class FileUploadUtil {

    @Value("${nginx.root-dir}")
    private String uploadDir;

    //uploadDir 경로 초기화 및 폴더 생성
    @PostConstruct
    public void ready()throws Exception {
        log.info("---------------post construct---------------");

        File uploadDirFile = new File(uploadDir);

        if(!uploadDirFile.exists()){
            uploadDirFile.mkdirs();
        }
    }

    // 1개 업로드용
    public String uploadFile(String subFolder, MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 업로드할 수 없음");
        }

        // 디렉토리 생성
        Path targetDir = Paths.get(uploadDir, subFolder);
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // 파일명 생성
        String saveFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = targetDir.resolve(saveFileName);

        // 저장
        Files.copy(file.getInputStream(), filePath);

        // 이미지면 썸네일 생성
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("image")) {
            Path thumbnailPath = targetDir.resolve("s_" + saveFileName);
            Thumbnails.of(filePath.toFile())
                    .size(400, 400)
                    .toFile(thumbnailPath.toFile());
        }

        // 예: "uuid_파일명.jpg" 형식
        return saveFileName;
    }
    
    // 여러개 업로드용 - 저장한 파일명들을 List<String>으로 반환
    public List<String> uploadFiles (String subFolder, List<MultipartFile> files) throws Exception{

        List<String> uploadedFileNames = new ArrayList<>();

        // 하위 경로 포함한 실제 저장 경로 만들기
        Path targetDir = Paths.get(uploadDir, subFolder);
        File targetDirFile = targetDir.toFile();

        if (!targetDirFile.exists()) {
            targetDirFile.mkdirs(); // 디렉토리 없으면 생성
        }

        if(files != null && !files.isEmpty()){

            for(MultipartFile file : files){

                String saveFileName = UUID.randomUUID().toString()+"_" + file.getOriginalFilename();

                // 파일을 서버의 디렉토리(uploadDir)에 저장
                Path path = targetDir.resolve(saveFileName);

                //FileCopyUtils.copy(file.getBytes(), path.toFile());
                Files.copy(file.getInputStream(), path);

                String contentType = file.getContentType();

                if(contentType != null && contentType.startsWith("image")){ //이미지여부 확인

                    Path thumbnailPath = targetDir.resolve("s_" + saveFileName);

                    Thumbnails.of(path.toFile())
                            .size(400,400)
                            .toFile(thumbnailPath.toFile());
                }

                uploadedFileNames.add(subFolder + "/" + saveFileName);

            }//end for

        }//end if


        return uploadedFileNames;
    }

    //저장된 파일 가져오기 (ResponseEntity<Resource> 반환)
    public ResponseEntity<Resource> getFile(String fileName) {

        Resource resource = new FileSystemResource(uploadDir+ File.separator + fileName);

        if(!resource.exists()) {

            resource = new FileSystemResource(uploadDir+ File.separator + "default.jpeg");

        }

        HttpHeaders headers = new HttpHeaders();

        try{
            headers.add("Content-Type", Files.probeContentType( resource.getFile().toPath() ));
        } catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    //원본 + 썸네일 삭제
    public void deleteFile(String fileName) {

        try{
            File originalFile = new File(uploadDir+ File.separator + fileName);
            originalFile.delete();
        }catch(Exception e){
            //e.printStackTrace(); //사용금지
            log.error(e.getMessage());
        }
        try{
            File thumbFile = new File(uploadDir+ File.separator + "s_" + fileName);
            thumbFile.delete();
        }catch(Exception e){
            //e.printStackTrace(); //사용금지
            log.error(e.getMessage());
        }

    }



}