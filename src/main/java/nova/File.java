package nova;

/**
 * Represents a file in the virtual filesystem.
 */
public class File extends FileSystemEntry {
    private long size;
    private String content;

    public File(String name, User createdBy, Directory parent, long size, String content) {
        super(name, createdBy, parent);
        this.size = size;
        this.content = content;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean matches(String keyword) {
        if (keyword == null) return false;
        String lowerKeyword = keyword.toLowerCase();
        boolean nameMatches = getName().toLowerCase().contains(lowerKeyword);
        boolean contentMatches = content != null && content.toLowerCase().contains(lowerKeyword);
        return nameMatches || contentMatches;
    }
}
