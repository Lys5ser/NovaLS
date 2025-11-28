package nova;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service class for searching and exploring the filesystem.
 */
public class FileSystemExplorer {
    private final Directory root;

    public FileSystemExplorer(Directory root) {
        this.root = root;
    }

    public List<FileSystemEntry> search(String keyword) {
        List<FileSystemEntry> results = new ArrayList<>();
        searchRecursive(root, keyword, null, results);
        Collections.sort(results);
        return results;
    }

    public List<FileSystemEntry> search(String keyword, User user) {
        List<FileSystemEntry> results = new ArrayList<>();
        searchRecursive(root, keyword, user, results);
        Collections.sort(results);
        return results;
    }

    public List<FileSystemEntry> searchByUser(User user) {
        List<FileSystemEntry> results = new ArrayList<>();
        searchRecursive(root, null, user, results);
        Collections.sort(results);
        return results;
    }

    public void printSearchResults(List<FileSystemEntry> results) {
        if (results.isEmpty()) {
            System.out.println("No results found.");
        } else {
            for (FileSystemEntry entry : results) {
                System.out.println(entry);
            }
        }
    }

    // Private recursive helper
    private void searchRecursive(FileSystemEntry entry, String keyword, User user, List<FileSystemEntry> results) {
        boolean matchesKeyword = keyword == null || entry.matches(keyword);
        boolean matchesUser = user == null || entry.getCreatedBy().equals(user);
        if (matchesKeyword && matchesUser) {
            results.add(entry);
        }
        if (entry instanceof Directory dir) {
            for (FileSystemEntry child : dir.getChildren()) {
                searchRecursive(child, keyword, user, results);
            }
        }
    }
}
