package nova;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.List;

// Diese Klasse erstellt eine grafische Benutzeroberfläche (GUI) für das NovaLS-Dateisystem.
// Sie verwendet Swing-Komponenten wie JFrame, JTree und JTextArea, um Dateien und Verzeichnisse anzuzeigen und zu bearbeiten.
public class NovaLSBasicGui extends JFrame {
    // Instanzvariablen für die GUI-Komponenten
    private final Directory root; // Das Wurzelverzeichnis des Dateisystems
    private final DefaultTreeModel treeModel; // Das Modell für den Baum (JTree)
    private final JTree tree; // Der Baum, der die Dateistruktur anzeigt
    private final JTextArea fileContentArea; // Bereich, um den Inhalt von Dateien anzuzeigen
    private JTextField searchField; // Eingabefeld für die Suche

    // Konstruktor: Initialisiert die GUI mit dem Wurzelverzeichnis
    public NovaLSBasicGui(Directory root) {
        super("NovaLS File System"); // Titel des Fensters
        this.root = root;
        this.treeModel = buildTreeModel(root); // Baum-Modell erstellen
        this.tree = new JTree(treeModel); // Baum-Komponente erstellen

        // Anpassung des Renderers für den Baum: Zeigt nur den Namen der Einträge an
        this.tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                    boolean leaf, int row, boolean hasFocus) {
                Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof DefaultMutableTreeNode node) {
                    Object obj = node.getUserObject();
                    if (obj instanceof FileSystemEntry entry) {
                        setText(entry.getName()); // Nur den Namen anzeigen
                    }
                }
                return c;
            }
        });

        this.fileContentArea = new JTextArea(); // Textbereich für Dateiinhalt
        fileContentArea.setEditable(false); // Nicht editierbar

        // Grundlegende Fenstereinstellungen
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Programm beenden beim Schließen
        setSize(700, 400); // Größe des Fensters
        setLocationRelativeTo(null); // Zentriert auf dem Bildschirm
        setLayout(new BorderLayout()); // Layout-Manager für das Layout

        // Suchleiste oben
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField(); // Eingabefeld für Suchbegriffe
        JButton searchButton = new JButton("Search"); // Such-Button
        searchButton.addActionListener(e -> onSearch()); // Listener für Such-Button
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        add(searchPanel, BorderLayout.NORTH);

        // Baum-Panel links
        JScrollPane treeScroll = new JScrollPane(tree); // Scrollbar für den Baum
        treeScroll.setPreferredSize(new Dimension(250, 400));
        add(treeScroll, BorderLayout.WEST);

        // Dateiinhalt-Panel in der Mitte
        JScrollPane contentScroll = new JScrollPane(fileContentArea); // Scrollbar für Textbereich
        add(contentScroll, BorderLayout.CENTER);

        // Button-Panel unten
        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Create"); // Button zum Erstellen
        JButton deleteButton = new JButton("Delete"); // Button zum Löschen
        JButton editButton = new JButton("Edit"); // Button zum Bearbeiten
        buttonPanel.add(createButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event-Listener hinzufügen
        tree.addTreeSelectionListener(e -> onTreeSelection()); // Listener für Baum-Auswahl
        createButton.addActionListener(e -> onCreate()); // Listener für Erstellen-Button
        deleteButton.addActionListener(e -> onDelete()); // Listener für Löschen-Button
        editButton.addActionListener(e -> onEdit()); // Listener für Bearbeiten-Button
    }

    // Erstellt das Baum-Modell aus dem Wurzelverzeichnis
    private DefaultTreeModel buildTreeModel(Directory dir) {
        DefaultMutableTreeNode rootNode = buildTreeNode(dir); // Rekursiv Knoten erstellen
        return new DefaultTreeModel(rootNode);
    }

    // Erstellt einen Baum-Knoten für einen Dateisystem-Eintrag
    private DefaultMutableTreeNode buildTreeNode(FileSystemEntry entry) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry); // Knoten mit Eintrag
        if (entry instanceof Directory dir) { // Wenn es ein Verzeichnis ist
            for (FileSystemEntry child : dir.getChildren()) { // Für jedes Kind
                node.add(buildTreeNode(child)); // Rekursiv hinzufügen
            }
        }
        return node;
    }

    // Aktualisiert den Baum nach Änderungen
    private void refreshTree() {
        treeModel.setRoot(buildTreeNode(root)); // Neue Wurzel setzen
        tree.expandRow(0); // Erste Zeile erweitern
    }

    // Wird aufgerufen, wenn ein Baum-Knoten ausgewählt wird
    private void onTreeSelection() {
        TreePath path = tree.getSelectionPath(); // Ausgewählter Pfad
        if (path == null) {
            fileContentArea.setText(""); // Leeren, wenn nichts ausgewählt
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object obj = node.getUserObject();
        if (obj instanceof File file) { // Wenn es eine Datei ist
            fileContentArea.setText(file.getContent() == null ? "" : file.getContent()); // Inhalt anzeigen
            fileContentArea.setEditable(false);
        } else {
            fileContentArea.setText(""); // Leeren für Verzeichnisse
            fileContentArea.setEditable(false);
        }
    }

    // Wird aufgerufen, wenn der Erstellen-Button gedrückt wird
    private void onCreate() {
        TreePath path = tree.getSelectionPath(); // Aktuelle Auswahl
        Directory parent = root; // Standard: Wurzel
        if (path != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            Object obj = node.getUserObject();
            if (obj instanceof Directory dir) {
                parent = dir; // Ausgewähltes Verzeichnis als Parent
            } else if (obj instanceof FileSystemEntry entry && entry.getParent() != null) {
                parent = entry.getParent(); // Parent des Eintrags
            }
        }
        // Dialog: Datei oder Verzeichnis erstellen?
        String[] options = { "File", "Directory" };
        int choice = JOptionPane.showOptionDialog(this, "Create File or Directory?", "Create",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == 0) { // Datei erstellen
            String name = JOptionPane.showInputDialog(this, "File name:"); // Name eingeben
            if (name != null && !name.isBlank()) {
                String content = JOptionPane.showInputDialog(this, "File content:"); // Inhalt eingeben
                File file = new File(name, new User("gui"), parent, content == null ? 0 : content.length(), content); // Neue
                                                                                                                      // Datei
                parent.addEntry(file); // Zum Parent hinzufügen
                refreshTree(); // Baum aktualisieren
            }
        } else if (choice == 1) { // Verzeichnis erstellen
            String name = JOptionPane.showInputDialog(this, "Directory name:"); // Name eingeben
            if (name != null && !name.isBlank()) {
                Directory dir = new Directory(name, new User("gui"), parent); // Neues Verzeichnis
                parent.addEntry(dir); // Zum Parent hinzufügen
                refreshTree(); // Baum aktualisieren
            }
        }
    }

    // Wird aufgerufen, wenn der Löschen-Button gedrückt wird
    private void onDelete() {
        TreePath path = tree.getSelectionPath(); // Aktuelle Auswahl
        if (path == null)
            return;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object obj = node.getUserObject();
        if (obj instanceof FileSystemEntry entry && entry.getParent() != null) {
            // Bestätigungsdialog
            int confirm = JOptionPane.showConfirmDialog(this, "Delete '" + entry.getName() + "'?", "Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                entry.getParent().removeEntry(entry); // Eintrag entfernen
                refreshTree(); // Baum aktualisieren
            }
        }
    }

    // Wird aufgerufen, wenn der Such-Button gedrückt wird
    private void onSearch() {
        String keyword = searchField.getText().trim().toLowerCase(); // Suchbegriff
        if (keyword.isEmpty()) {
            fileContentArea.setText("Please enter a search term."); // Hinweis, wenn leer
            return;
        }
        List<FileSystemEntry> results = new java.util.ArrayList<>(); // Liste für Ergebnisse
        searchRecursive(root, keyword, results); // Rekursive Suche
        if (results.isEmpty()) {
            fileContentArea.setText("No results found."); // Keine Ergebnisse
            treeModel.setRoot(buildTreeNode(root)); // Zurück zum normalen Baum
        } else {
            // Suchergebnisse im Baum anzeigen
            DefaultMutableTreeNode searchRoot = new DefaultMutableTreeNode("Search Results");
            for (FileSystemEntry entry : results) {
                searchRoot.add(new DefaultMutableTreeNode(entry)); // Ergebnisse hinzufügen
            }
            treeModel.setRoot(searchRoot); // Suchbaum setzen
            tree.expandRow(0); // Erweitern
            fileContentArea.setText(""); // Textbereich leeren
        }
    }

    // Rekursive Suche nach Einträgen, die das Keyword enthalten
    private void searchRecursive(FileSystemEntry entry, String keyword, List<FileSystemEntry> results) {
        if (entry.getName().toLowerCase().contains(keyword)) { // Name enthält Keyword?
            results.add(entry); // Zur Liste hinzufügen
        }
        if (entry instanceof Directory dir) { // Wenn Verzeichnis
            for (FileSystemEntry child : dir.getChildren()) { // Kinder durchsuchen
                searchRecursive(child, keyword, results);
            }
        }
    }

    // Wird aufgerufen, wenn der Bearbeiten-Button gedrückt wird
    private void onEdit() {
        TreePath path = tree.getSelectionPath(); // Aktuelle Auswahl
        if (path == null)
            return;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object obj = node.getUserObject();
        if (obj instanceof File file) { // Wenn Datei
            String newContent = JOptionPane.showInputDialog(this, "Edit file content:", file.getContent()); // Neuer
                                                                                                            // Inhalt
            if (newContent != null) {
                file.setContent(newContent); // Inhalt setzen
                file.setSize(newContent.length()); // Größe aktualisieren
                fileContentArea.setText(newContent); // Anzeigen
            }
        } else if (obj instanceof Directory dir) { // Wenn Verzeichnis
            String newName = JOptionPane.showInputDialog(this, "Edit directory name:", dir.getName()); // Neuer Name
            if (newName != null && !newName.isBlank()) {
                dir.setName(newName); // Name setzen
                refreshTree(); // Baum aktualisieren
            }
        }
    }

    // Hauptmethode zum Starten der Anwendung
    public static void main(String[] args) {
        // Beispiel-Daten erstellen
        User alice = new User("alice");
        User bob = new User("bob");
        Directory root = new Directory("root", alice, null); // Wurzelverzeichnis

        // GUI in Event-Dispatch-Thread starten (Swing-Thread-Sicherheit)
        SwingUtilities.invokeLater(() -> new NovaLSBasicGui(root).setVisible(true));
    }
}
