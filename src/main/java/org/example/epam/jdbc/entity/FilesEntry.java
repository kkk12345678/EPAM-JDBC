package org.example.epam.jdbc.entity;

public class FilesEntry {
    private final int id;
    private final String name;
    private final Integer parentId;
    private final boolean isDirectory;
    private final Long discUsage;

    public FilesEntry(int id, String name, Integer parentId, boolean isDirectory, Long discUsage) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.isDirectory = isDirectory;
        this.discUsage = discUsage;
    }

    @Override
    public String toString() {
        return "CatalogueEntry{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                ", isDirectory=" + isDirectory +
                ", discUsage=" + discUsage +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public Long getDiscUsage() {
        return discUsage;
    }
}

