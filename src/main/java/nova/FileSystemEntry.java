package nova;

import java.time.LocalDateTime;

/**
 * Base class for File and Directory entries in the filesystem.
 */
public abstract class FileSystemEntry implements Comparable<FileSystemEntry>, Searchable {
    private String name;
    private final LocalDateTime createdAt;
    private final User createdBy;
    private Directory parent;

    public FileSystemEntry(String name, User createdBy, Directory parent) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public String getPath() {
        if (parent == null) {
            return "/" + name;
        } else {
            return parent.getPath() + "/" + name;
        }
    }

    public abstract long getSize();

    @Override
    public int compareTo(FileSystemEntry other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        return String.format("%s [path=%s, size=%d, createdBy=%s, createdAt=%s]",
                getClass().getSimpleName(), getPath(), getSize(), createdBy, createdAt);
    }

    // Searchable implementation left abstract for subclasses
    @Override
    public abstract boolean matches(String keyword);
}
