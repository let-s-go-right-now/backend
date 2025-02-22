package com.lets.go.right.now.global.s3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client s3Client;

    @Value("${s3.bucket}")
    private String bucket;

    // 파일 업로드 메서드
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            // S3에 파일 업로드 시도
            s3Client.putObject(bucket, fileName, inputStream, metadata);
        } catch (AmazonServiceException e) {
            // S3와 관련된 오류 처리
            throw new GeneralException(ErrorStatus._S3_UPLOAD_FAIL);
        } catch (SdkClientException e) {
            // SDK 관련 오류 처리
            throw new GeneralException(ErrorStatus._S3_CLIENT_ERROR);
        } catch (IOException e) {
            // 파일 입출력 관련 오류 처리
            throw new GeneralException(ErrorStatus._S3_FILE_PROCESSING_ERROR);
        }

        // S3에 업로드된 파일 URL 반환
        URL fileUrl = s3Client.getUrl(bucket, fileName);
        return fileUrl.toString();
    }

    // S3주소로 파일 삭제
    public void deleteFileByURL(String s3URL) {
        // <Multipart Image>
        // 1. 이미지가 존재할 경우 -> 프로필 변경 요청에 해당 ; 기존 이미지 삭제, 새 이미지 저장
        // 2. 이미지가 존재하지 않을 경우 -> 기본 프로필 변경에 해당 ; 기존 이미지 삭제
        if (s3URL != null && !s3URL.isEmpty()) {
            String fileName = extractFileNameFromUrl(s3URL);
            deleteFile(fileName);
        }

    }


    // 파일 삭제
    public void deleteFile(String fileName) {
        try {
            s3Client.deleteObject(bucket, fileName);
        } catch (AmazonServiceException e) {
            throw new GeneralException(ErrorStatus._S3_REMOVE_FAIL);
        } catch (SdkClientException e) {
            throw new GeneralException(ErrorStatus._S3_CLIENT_ERROR);
        }
    }


    // 파일 다운로드 메서드 - 직접 다운로드 -> 이미지를 파일로 다운할 수 있는 다운로드 링크를 제공
    public byte[] downloadFile(String fileName) throws IOException {
        return s3Client.getObject(bucket, fileName).getObjectContent().readAllBytes();
    }

    // 파일 URL 반환 메서드 - 프론트에서 랜더링 -> 프론트엔드에서 이미지를 렌더링 할 수 있도록, S3 링크를 제공
    public String getFileUrl(String fileName) {
        return s3Client.getUrl(bucket, fileName).toString();
    }


    // URL에서 파일명 추출
    private String extractFileNameFromUrl(String url) {
        try {
            // URL에서 파일명 부분만 추출 후, +를 공백으로 되돌리기
            String decodedUrl = java.net.URLDecoder.decode(url, "UTF-8");  // URL 디코딩
            return decodedUrl.substring(decodedUrl.lastIndexOf("/") + 1);  // 파일명 추출
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }
}