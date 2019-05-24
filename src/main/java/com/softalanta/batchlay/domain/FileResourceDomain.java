package com.softalanta.batchlay.domain;

public enum FileResourceDomain
{
    DOCUMENT("documents");

    public String getContainerName() {
        return containerName;
    }

    private String containerName;

    FileResourceDomain(String containerName){
        this.containerName = containerName;
    }
}