package in.devopsbuddy.web.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.servicequotas.model.IllegalArgumentException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.devopsbuddy.web.service.S3Service;

@Service
public class S3ServiceImpl implements S3Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(S3ServiceImpl.class);

    private static final String PROFILE_IMAGE_FILENAME_PREFIX = "profileImage";

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${image.store.tmp.location}")
    private String tempLocation;

    @Autowired
    private AmazonS3 amazonS3;

    @Override
    public String uploadProfileImage(MultipartFile imageFile, String username) throws IOException {
        String profileImageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            byte[] bytes = imageFile.getBytes();
            File tempDir = new File(tempLocation + File.separatorChar + username);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            File tempImageFile = new File(tempDir.getAbsolutePath() + File.separatorChar + PROFILE_IMAGE_FILENAME_PREFIX
                    + "." + FilenameUtils.getExtension(imageFile.getOriginalFilename()));
            try (BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(new File(tempImageFile.getAbsolutePath())))) {
                stream.write(bytes);
            }

            profileImageUrl = this.storeProfileImageToS3(tempImageFile, username);
            tempImageFile.delete();
        }
        return profileImageUrl;
    }

    private String storeProfileImageToS3(File resource, String username) {
        var resourceUrl = "";
        if (!resource.exists()) {
            LOGGER.error("File {} does not exists!", resource.getAbsolutePath());
            throw new IllegalArgumentException("File " + resource.getAbsolutePath() + " does not exists");
        }
        var rootBucketUrl = this.ensureBucketExists(bucketName);
        if (null == rootBucketUrl) {
            LOGGER.error("The bucket {} does not exists and the application was not able to create it", rootBucketUrl);
        } else {
            var acl = new AccessControlList();
            acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
            var key = username + "/" + PROFILE_IMAGE_FILENAME_PREFIX + "."
                    + FilenameUtils.getExtension(resource.getName());
            try {
                this.amazonS3.putObject(new PutObjectRequest(bucketName, key, resource).withAccessControlList(acl));
                resourceUrl = this.amazonS3.getBucketLocation(bucketName);
            } catch (AmazonClientException e) {
                LOGGER.error(e.getMessage());
            }
        }
        return resourceUrl;
    }

    private String ensureBucketExists(String bname) {
        var bucketUrl = "";
        try {
            if (!this.amazonS3.doesBucketExistV2(bname)) {
                this.amazonS3.createBucket(bname);
            }
            bucketUrl = this.amazonS3.getBucketLocation(bname) + bname;
        } catch (AmazonClientException e) {
            System.err.println(e);
        }
        return bucketUrl;
    }
}
