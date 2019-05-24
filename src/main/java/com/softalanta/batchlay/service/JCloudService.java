package com.softalanta.batchlay.service;

import com.google.common.io.ByteSource;
import com.softalanta.batchlay.domain.FileResource;
import com.softalanta.batchlay.repository.FileResourceRepository;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Service
public class JCloudService {

    private final FileResourceRepository fileResourceRepository;

    private BlobStoreContext blobStoreContext;

    private BlobStore blobStore;

    @Autowired
    public JCloudService(FileResourceRepository fileResourceRepository) {
        this.fileResourceRepository = fileResourceRepository;
    }

    @PostConstruct
    public void init(){

        //TODO get configuration for blob store from config file

        Properties properties = new Properties();
        properties.setProperty(FilesystemConstants.PROPERTY_BASEDIR, "./local/filesystemstorage");

         blobStoreContext = ContextBuilder.newBuilder("filesystem")
                .credentials("", "")
                .overrides(properties)
                .buildView(BlobStoreContext.class);

         blobStore = blobStoreContext.getBlobStore();
    }

    @PreDestroy
    public void cleanUp(){
        blobStoreContext.close();

    }

    public Long saveFileResource(FileResource fileResource, File file){

        //      Save to db
        fileResourceRepository.save(fileResource);

        String containerName = fileResource.getDomain().getContainerName();
        blobStore.createContainerInLocation(null, containerName);


        Blob blob = blobStore.blobBuilder(fileResource.getUid().toString())
                .payload(file)
                .contentType(fileResource.getContentType())
                .contentDisposition("filename=" + fileResource.getName())
                .build();

        blobStore.putBlob(containerName, blob);

        return fileResource.getUid();

    }

    public ByteSource getFileContent(String container, String fileName) throws IOException {
        Blob blob =blobStore.getBlob(container,fileName);
        final ByteSource byteSource = new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return blob.getPayload().openStream();
            }
        };
        return  byteSource;
    }
}
