package com.common.core.util;

import cn.hutool.core.io.FileUtil;
import com.common.core.prop.AmazonProp;
import com.gp.common.base.enums.MimeTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmazonService {

    private S3Client s3client;

    private AmazonProp amazonProp;

//    @PostConstruct
//    private void initializeAmazon() {
//
//    }

//    public String uploadFile(MultipartFile multipartFile) {
//        String fileUrl = "";
//        try {
//            File file = convertMultiPartToFile(multipartFile);
//            String fileName = generateFileName(multipartFile);
//            String dirname=;
//            uploadFileTos3bucket(dirname, fileName, file);
//            file.delete();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return fileUrl;
//    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return System.currentTimeMillis() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

//    public void uploadFileTos3bucket(String dirname, String fileName, File file) {
//        s3client..putObject(new PutObjectRequest(bucketName+ "/" +dirname, fileName, file)
//                .withCannedAcl(CannedAccessControlList.PublicRead));
//    }

//    @SneakyThrows
//    public void uploadFileTos3bucket(String dirname, String fileName, InputStream inputStream) {
//        BufferedInputStream bufferedInputStream=new BufferedInputStream(inputStream);
//        PutObjectResponse putObjectResponse =
//                s3client.putObject(PutObjectRequest.builder().bucket(bucketName).key("media/"+dirname+"/"+fileName)
//                                .build(),
//                        RequestBody.fromInputStream(bufferedInputStream, bufferedInputStream.available()));
//        System.out.println();
//    }

    @SneakyThrows
    public String uploadFileTos3bucket(String fileName, InputStream inputStream) {
        PutObjectResponse putObjectResponse =
                s3client.putObject(PutObjectRequest.builder()
                                .bucket(amazonProp.getBucketName())
                                .key(amazonProp.getDirname() + fileName)
                                .contentType(MimeTypeEnum.getContentType(FileUtil.extName(fileName)))
                                .acl(ObjectCannedACL.PUBLIC_READ)
                                .build(),
                        RequestBody.fromBytes(toByteArray(inputStream)));
        return "/" + amazonProp.getDirname() + fileName;

    }

    /**
     * 发送到二级文件夹
     *
     * @param fileName
     * @param inputStream
     * @param s3Level2Dir
     * @return
     */
    @SneakyThrows
    public String uploadFileToS3Level2Dir(String fileName, InputStream inputStream, String s3Level2Dir) {
        PutObjectResponse putObjectResponse =
                s3client.putObject(PutObjectRequest.builder()
                                .bucket(amazonProp.getBucketName())
                                .key(amazonProp.getDirname() + s3Level2Dir + fileName)
                                .contentType(MimeTypeEnum.getContentType(FileUtil.extName(fileName)))
                                .acl(ObjectCannedACL.PUBLIC_READ)
                                .build(),
                        RequestBody.fromBytes(toByteArray(inputStream)));
        return "/" + amazonProp.getDirname() + s3Level2Dir + fileName;

    }

    /**
     * 直接发送到指定文件夹
     *
     * @param fileName
     * @param inputStream
     * @param s3Dir
     * @return
     */
    @SneakyThrows
    public String uploadFileTos3bucket(String fileName, InputStream inputStream, String s3Dir) {
        PutObjectResponse putObjectResponse =
                s3client.putObject(PutObjectRequest.builder()
                                .bucket(amazonProp.getBucketName())
                                .key(s3Dir + fileName)
                                .contentType(MimeTypeEnum.getContentType(FileUtil.extName(fileName)))
                                .acl(ObjectCannedACL.PUBLIC_READ)
                                .build(),
                        RequestBody.fromBytes(toByteArray(inputStream)));

        return "/" + s3Dir + fileName;

    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

//    @SneakyThrows
//    public void uploadFileTos3bucket(String dirname, String fileName, InputStream inputStream) {
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentLength(inputStream.available());
//        s3client.putObject(new PutObjectRequest(bucketName+ "/media", fileName, inputStream, metadata)
//            .withCannedAcl(CannedAccessControlList.PublicRead));
//    }

//    public String deleteFileFromS3Bucket(String fileUrl) {
//        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
//        s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
//        return "Successfully deleted";
//    }
//
//    public List<String> listFiles() {
//        ListObjectsRequest listObjectsRequest =
//                new ListObjectsRequest()
//                        .withBucketName(bucketName)
//                        .withPrefix("/");
//
//        List<String> keys = new ArrayList<>();
//        ObjectListing objects = s3client.listObjects(listObjectsRequest);
//        while (true) {
//            List<S3ObjectSummary> summaries = objects.getObjectSummaries();
//            if (summaries.size() < 1) {
//                break;
//            }
//            for (S3ObjectSummary item : summaries) {
//                if (!item.getKey().endsWith("/"))
//                    keys.add(item.getKey());
//            }
//
//            objects = s3client.listNextBatchOfObjects(objects);
//        }
//        return keys;
//    }
}
