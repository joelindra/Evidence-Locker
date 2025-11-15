package burp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Main panel for Evidence Locker tab
 */
public class EvidenceLockerPanel extends JPanel {
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private List<EvidenceEntry> evidenceEntries;
    
    private JList<EvidenceEntry> findingsList;
    private DefaultListModel<EvidenceEntry> listModel;
    private EvidenceDetailPanel detailPanel;
    private JButton exportMarkdownBtn;
    private JButton exportJsonBtn;
    private JButton exportHtmlBtn;
    private JButton exportDocxBtn;
    private JButton exportPdfBtn;
    
    public EvidenceLockerPanel(IBurpExtenderCallbacks callbacks, IExtensionHelpers helpers, 
                              List<EvidenceEntry> evidenceEntries) {
        this.callbacks = callbacks;
        this.helpers = helpers;
        this.evidenceEntries = evidenceEntries;
        
        setLayout(new BorderLayout());
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Left panel - Findings list
        JPanel leftPanel = createLeftPanel();
        
        // Right panel - Detail view
        detailPanel = new EvidenceDetailPanel(callbacks, helpers);
        
        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, detailPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.3);
        
        // Bottom panel - Export buttons
        JPanel bottomPanel = createBottomPanel();
        
        add(splitPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JLabel titleLabel = new JLabel("Findings");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        listModel = new DefaultListModel<>();
        findingsList = new JList<>(listModel);
        findingsList.setCellRenderer(new EvidenceListCellRenderer());
        findingsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        findingsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                EvidenceEntry selected = findingsList.getSelectedValue();
                if (selected != null) {
                    detailPanel.displayEvidence(selected);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(findingsList);
        scrollPane.setPreferredSize(new Dimension(300, 0));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        // Add button to create new finding
        JButton newFindingBtn = new JButton("New Finding");
        newFindingBtn.addActionListener(e -> {
            EvidenceEntry newEntry = new EvidenceEntry();
            evidenceEntries.add(newEntry);
            listModel.addElement(newEntry);
            findingsList.setSelectedValue(newEntry, true);
        });
        
        // Delete button
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> deleteSelectedFinding());
        
        buttonPanel.add(newFindingBtn);
        buttonPanel.add(deleteBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        exportMarkdownBtn = new JButton("Export Markdown");
        exportJsonBtn = new JButton("Export JSON");
        exportHtmlBtn = new JButton("Export HTML");
        exportDocxBtn = new JButton("Export DOCX");
        exportPdfBtn = new JButton("Export PDF");
        
        exportMarkdownBtn.addActionListener(e -> exportReport(ReportExporter.ExportFormat.MARKDOWN));
        exportJsonBtn.addActionListener(e -> exportReport(ReportExporter.ExportFormat.JSON));
        exportHtmlBtn.addActionListener(e -> exportReport(ReportExporter.ExportFormat.HTML));
        exportDocxBtn.addActionListener(e -> exportReport(ReportExporter.ExportFormat.DOCX));
        exportPdfBtn.addActionListener(e -> exportReport(ReportExporter.ExportFormat.PDF));
        
        panel.add(exportMarkdownBtn);
        panel.add(exportJsonBtn);
        panel.add(exportHtmlBtn);
        panel.add(exportDocxBtn);
        panel.add(exportPdfBtn);
        
        return panel;
    }
    
    public void addEvidenceEntry(IHttpRequestResponse message, String source) {
        EvidenceEntry entry = new EvidenceEntry(message, source, helpers);
        evidenceEntries.add(entry);
        listModel.addElement(entry);
        findingsList.setSelectedValue(entry, true);
        
        // Open dialog to fill in details
        SwingUtilities.invokeLater(() -> {
            EvidenceEntryDialog dialog = new EvidenceEntryDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), entry, callbacks, helpers);
            dialog.setVisible(true);
            // Refresh list to show updated entry
            int index = listModel.indexOf(entry);
            if (index >= 0) {
                listModel.set(index, entry);
            }
        });
    }
    
    private void deleteSelectedFinding() {
        EvidenceEntry selected = findingsList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a finding to delete.", 
                "Delete Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String title = selected.getFindingTitle();
        if (title == null || title.trim().isEmpty()) {
            title = "Untitled Finding";
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete:\n\"" + title + "\"?\n\nThis action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Remove from list model
            int index = listModel.indexOf(selected);
            if (index >= 0) {
                listModel.remove(index);
            }
            
            // Remove from evidence entries list
            evidenceEntries.remove(selected);
            
            // Clear detail panel if this was the selected entry
            detailPanel.displayEvidence(null);
            
            // Select next item if available, or previous if at end
            if (listModel.getSize() > 0) {
                int newIndex = Math.min(index, listModel.getSize() - 1);
                findingsList.setSelectedIndex(newIndex);
            }
        }
    }
    
    private void exportReport(ReportExporter.ExportFormat format) {
        if (evidenceEntries.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No evidence entries to export.", 
                "Export Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        String extension = format.getExtension();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            format.name() + " files (*." + extension + ")", extension));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                ReportExporter exporter = new ReportExporter(callbacks, helpers);
                exporter.export(evidenceEntries, fileChooser.getSelectedFile(), format);
                JOptionPane.showMessageDialog(this, 
                    "Report exported successfully!", "Export Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting report: " + e.getMessage(), "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Custom cell renderer for findings list with color coding
     */
    private class EvidenceListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof EvidenceEntry) {
                EvidenceEntry entry = (EvidenceEntry) value;
                String title = entry.getFindingTitle();
                if (title == null || title.trim().isEmpty()) {
                    title = "Untitled Finding";
                }
                setText(title);
                
                // Color coding based on severity
                if (!isSelected) {
                    setBackground(entry.getSeverity().getColor());
                    setForeground(Color.BLACK);
                }
            }
            
            return this;
        }
    }
}

