You are helping me implement a small virtual filesystem project in Java called "NovaLS".
 
Goal:
- Model a mini filesystem with Users, Files, Directories and a recursive search feature ("IntelliSearch").
- Show OOP concepts: inheritance, abstract classes, interfaces, composition/aggregation, associations, Comparable, recursion, and collections.
 
Core classes and interface:
- class User
- abstract class FileSystemEntry implements Comparable<FileSystemEntry>, Searchable
- class File extends FileSystemEntry
- class Directory extends FileSystemEntry
- interface Searchable
- class FileSystemExplorer
 
Concept overview:
 
User:
- Represents the creator of files and directories.
- A User can create many filesystem entries (1-* relation).
- Fields: username (String).
- Methods: constructor, getter, toString().
 
Searchable (interface):
- Declares that an object can be matched by a keyword search.
- Method: boolean matches(String keyword);
 
FileSystemEntry (abstract):
- Base class for File and Directory.
- Fields:
  - name : String
  - createdAt : LocalDateTime
  - createdBy : User
  - parent : Directory (may be null for root)
- Methods:
  - getters/setters
  - getPath() : builds full path from root, e.g. "/root/docs/file.txt"
  - abstract long getSize()
  - toString() : includes path, size, createdBy, createdAt
  - implements Comparable<FileSystemEntry> and compares entries by name (case-insensitive)
  - implements Searchable (but the default implementation can be abstract, overridden in subclasses)
 
File:
- Extends FileSystemEntry and implements Searchable.
- Fields:
  - size : long
  - content : String (optional)
- getSize(): returns its own size.
- matches(keyword):
  - true if the filename contains the keyword (ignore case),
  - OR if the content contains the keyword (if content is not null).
 
Directory:
- Extends FileSystemEntry and implements Searchable.
- Fields:
  - List<FileSystemEntry> children
- Methods:
  - addEntry(FileSystemEntry child) : adds child and sets parent
  - removeEntry(FileSystemEntry child) : removes child and clears parent
  - getChildren() : returns unmodifiable view
  - getSize() : sum of all children's sizes (recursive)
  - printTree() : prints indented directory structure (recursive)
  - matches(keyword) : true if directory name contains keyword (ignore case)
 
FileSystemExplorer:
- Service class that does not store data itself.
- Works with a root Directory.
- Methods:
  - List<FileSystemEntry> search(String keyword)
  - List<FileSystemEntry> search(String keyword, User user)
  - List<FileSystemEntry> searchByUser(User user)
  - void printSearchResults(List<FileSystemEntry> results)
- Implementation details:
  - Uses a private recursive helper method that walks through all directories and files starting from the root.
  - For each entry, calls entry.matches(keyword).
  - For user-filtered searches, only collects entries with createdBy == given user.
  - Uses Collections.sort(results) so the list is sorted by name (via Comparable).
 
Please generate clean, idiomatic Java 17 code, with clear method and class names, good encapsulation and minimal but readable comments.