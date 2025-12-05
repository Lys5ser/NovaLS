package nova;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a directory in the virtual filesystem.
 */
public class Directory extends FileSystemEntry {
    private final List<FileSystemEntry> children = new ArrayList<>();

    public Directory(String name, User createdBy, Directory parent) {
        super(name, createdBy, parent);
    }

    public void addEntry(FileSystemEntry child) {
        if (child != null && !children.contains(child)) {
            children.add(child);
            child.setParent(this);
        }
    }

    public void removeEntry(FileSystemEntry child) {
        if (child != null && children.remove(child)) {
            child.setParent(null);
        }
    }

    public List<FileSystemEntry> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public long getSize() {
        long total = 0;
        for (FileSystemEntry entry : children) {
            total += entry.getSize();
        }
        return total;
    }

    private void printTree(int indent) {
        System.out.println("  ".repeat(indent) + getName() + "/");
        for (FileSystemEntry entry : children) {
            if (entry instanceof Directory dir) {
                dir.printTree(indent + 1);
            } else {
                System.out.println("  ".repeat(indent + 1) + entry.getName());
            }
        }
    }

    @Override
    public boolean matches(String keyword) {
        if (keyword == null) return false;
        return getName().toLowerCase().contains(keyword.toLowerCase());
    }
}
