package com.productengine.test.task3.model;

public class FileObject {
    private int depth;
    private String path;

    public FileObject(int depth, String path) {
        this.depth = depth;
        this.path = path;
    }

    public int getDepth() {
        return depth;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return path;
    }
}
