package nova;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.List;


public class NovaLSBasicGui extends JFrame {
    private final Directory root;
    private final DefaultTreeModel treeModel;
    private final JTree tree;
    private final JTextArea fileContentArea;
    private JTextField searchField;

    public NovaLSBasicGui(Directory root) {
        super("NovaLS File System");
        this.root = root;
        this.treeModel = buildTreeModel(root);
        this.tree = new JTree(treeModel);
        this.tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof DefaultMutableTreeNode node) {
                    Object obj = node.getUserObject();
                    if (obj instanceof FileSystemEntry entry) {
                        setText(entry.getName());
                    }
                }
                return c;
            }
        });
        this.fileContentArea = new JTextArea();
        fileContentArea.setEditable(false);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> onSearch());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        add(searchPanel, BorderLayout.NORTH);

        // Tree panel
        JScrollPane treeScroll = new JScrollPane(tree);
        treeScroll.setPreferredSize(new Dimension(250, 400));
        add(treeScroll, BorderLayout.WEST);

        // File content panel
        JScrollPane contentScroll = new JScrollPane(fileContentArea);
        add(contentScroll, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Create");
        JButton deleteButton = new JButton("Delete");
        JButton editButton = new JButton("Edit");
        buttonPanel.add(createButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Listeners
        tree.addTreeSelectionListener(e -> onTreeSelection());
        createButton.addActionListener(e -> onCreate());
        deleteButton.addActionListener(e -> onDelete());
        editButton.addActionListener(e -> onEdit());
    }

    private DefaultTreeModel buildTreeModel(Directory dir) {
        DefaultMutableTreeNode rootNode = buildTreeNode(dir);
        return new DefaultTreeModel(rootNode);
    }

    private DefaultMutableTreeNode buildTreeNode(FileSystemEntry entry) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry);
        if (entry instanceof Directory dir) {
            for (FileSystemEntry child : dir.getChildren()) {
                node.add(buildTreeNode(child));
            }
        }
        return node;
    }

    private void refreshTree() {
        treeModel.setRoot(buildTreeNode(root));
        tree.expandRow(0);
    }

    private void onTreeSelection() {
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            fileContentArea.setText("");
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object obj = node.getUserObject();
        if (obj instanceof File file) {
            fileContentArea.setText(file.getContent() == null ? "" : file.getContent());
            fileContentArea.setEditable(false);
        } else {
            fileContentArea.setText("");
            fileContentArea.setEditable(false);
        }
    }

    private void onCreate() {
        TreePath path = tree.getSelectionPath();
        Directory parent = root;
        if (path != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            Object obj = node.getUserObject();
            if (obj instanceof Directory dir) {
                parent = dir;
            } else if (obj instanceof FileSystemEntry entry && entry.getParent() != null) {
                parent = entry.getParent();
            }
        }
        String[] options = {"File", "Directory"};
        int choice = JOptionPane.showOptionDialog(this, "Create File or Directory?", "Create", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == 0) { // File
            String name = JOptionPane.showInputDialog(this, "File name:");
            if (name != null && !name.isBlank()) {
                String content = JOptionPane.showInputDialog(this, "File content:");
                File file = new File(name, new User("gui"), parent, content == null ? 0 : content.length(), content);
                parent.addEntry(file);
                refreshTree();
            }
        } else if (choice == 1) { // Directory
            String name = JOptionPane.showInputDialog(this, "Directory name:");
            if (name != null && !name.isBlank()) {
                Directory dir = new Directory(name, new User("gui"), parent);
                parent.addEntry(dir);
                refreshTree();
            }
        }
    }

    private void onDelete() {
        TreePath path = tree.getSelectionPath();
        if (path == null) return;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object obj = node.getUserObject();
        if (obj instanceof FileSystemEntry entry && entry.getParent() != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete '" + entry.getName() + "'?", "Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                entry.getParent().removeEntry(entry);
                refreshTree();
            }
        }
    }


    private void onSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            fileContentArea.setText("Please enter a search term.");
            return;
        }
        List<FileSystemEntry> results = new java.util.ArrayList<>();
        searchRecursive(root, keyword, results);
        if (results.isEmpty()) {
            fileContentArea.setText("No results found.");
            treeModel.setRoot(buildTreeNode(root));
        } else {
            // Build search results tree
            DefaultMutableTreeNode searchRoot = new DefaultMutableTreeNode("Search Results");
            for (FileSystemEntry entry : results) {
                searchRoot.add(new DefaultMutableTreeNode(entry));
            }
            treeModel.setRoot(searchRoot);
            tree.expandRow(0);
            fileContentArea.setText("");
        }
    }

    private void searchRecursive(FileSystemEntry entry, String keyword, List<FileSystemEntry> results) {
        if (entry.getName().toLowerCase().contains(keyword)) {
            results.add(entry);
        }
        if (entry instanceof Directory dir) {
            for (FileSystemEntry child : dir.getChildren()) {
                searchRecursive(child, keyword, results);
            }
        }
    }

    private void onEdit() {
        TreePath path = tree.getSelectionPath();
        if (path == null) return;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object obj = node.getUserObject();
        if (obj instanceof File file) {
            String newContent = JOptionPane.showInputDialog(this, "Edit file content:", file.getContent());
            if (newContent != null) {
                file.setContent(newContent);
                file.setSize(newContent.length());
                fileContentArea.setText(newContent);
            }
        } else if (obj instanceof Directory dir) {
            String newName = JOptionPane.showInputDialog(this, "Edit directory name:", dir.getName());
            if (newName != null && !newName.isBlank()) {
                dir.setName(newName);
                refreshTree();
            }
        }
    }

    public static void main(String[] args) {
        // Sample data
        User alice = new User("alice");
        User bob = new User("bob");
        Directory root = new Directory("root", alice, null);
        
        SwingUtilities.invokeLater(() -> new NovaLSBasicGui(root).setVisible(true));
    }
}
