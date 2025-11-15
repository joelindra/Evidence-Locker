package burp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * Panel showing detailed view of selected evidence entry
 */
public class EvidenceDetailPanel extends JPanel {
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    
    private JTextField titleField;
    private JComboBox<EvidenceEntry.Severity> severityCombo;
    private JTextArea descriptionArea;
    private JTextArea impactArea;
    private JTextArea remediationArea;
    private JTextField cvssField;
    private JTextField tagsField;
    private JTextPane requestPane;
    private JTextPane responsePane;
    private JLabel timestampLabel;
    private JLabel sourceLabel;
    private JLabel urlLabel;
    
    private EvidenceEntry currentEntry;
    
    public EvidenceDetailPanel(IBurpExtenderCallbacks callbacks, IExtensionHelpers helpers) {
        this.callbacks = callbacks;
        this.helpers = helpers;
        
        setLayout(new BorderLayout());
        initializeComponents();
    }
    
    private void initializeComponents() {
        JPanel formPanel = createFormPanel();
        JPanel requestResponsePanel = createRequestResponsePanel();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, requestResponsePanel);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Finding Title
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(new JLabel("Finding Title *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        titleField = new JTextField();
        titleField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
        });
        fieldsPanel.add(titleField, gbc);
        
        // Severity
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Severity:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        severityCombo = new JComboBox<>(EvidenceEntry.Severity.values());
        severityCombo.addActionListener(e -> updateEntry());
        fieldsPanel.add(severityCombo, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Description/Notes:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
        });
        fieldsPanel.add(new JScrollPane(descriptionArea), gbc);
        
        // Impact
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(new JLabel("Impact:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        impactArea = new JTextArea(3, 30);
        impactArea.setLineWrap(true);
        impactArea.setWrapStyleWord(true);
        impactArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
        });
        fieldsPanel.add(new JScrollPane(impactArea), gbc);
        
        // Remediation
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(new JLabel("Remediation:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        remediationArea = new JTextArea(3, 30);
        remediationArea.setLineWrap(true);
        remediationArea.setWrapStyleWord(true);
        remediationArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
        });
        fieldsPanel.add(new JScrollPane(remediationArea), gbc);
        
        // CVSS Score
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(new JLabel("CVSS Score:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cvssField = new JTextField();
        cvssField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
        });
        fieldsPanel.add(cvssField, gbc);
        
        // Tags
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Tags (comma-separated):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        tagsField = new JTextField();
        tagsField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateEntry(); }
        });
        fieldsPanel.add(tagsField, gbc);
        
        // Metadata
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        JPanel metadataPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timestampLabel = new JLabel();
        sourceLabel = new JLabel();
        urlLabel = new JLabel();
        metadataPanel.add(new JLabel("Timestamp:"));
        metadataPanel.add(timestampLabel);
        metadataPanel.add(new JLabel(" | Source:"));
        metadataPanel.add(sourceLabel);
        metadataPanel.add(new JLabel(" | URL:"));
        metadataPanel.add(urlLabel);
        fieldsPanel.add(metadataPanel, gbc);
        
        panel.add(fieldsPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createRequestResponsePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Request panel
        JPanel requestPanel = new JPanel(new BorderLayout());
        requestPanel.setBorder(new TitledBorder("Request"));
        requestPane = new JTextPane();
        requestPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        requestPane.setEditable(false);
        requestPanel.add(new JScrollPane(requestPane), BorderLayout.CENTER);
        
        // Response panel
        JPanel responsePanel = new JPanel(new BorderLayout());
        responsePanel.setBorder(new TitledBorder("Response"));
        responsePane = new JTextPane();
        responsePane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        responsePane.setEditable(false);
        responsePanel.add(new JScrollPane(responsePane), BorderLayout.CENTER);
        
        panel.add(requestPanel);
        panel.add(responsePanel);
        
        return panel;
    }
    
    public void displayEvidence(EvidenceEntry entry) {
        this.currentEntry = entry;
        
        if (entry == null) {
            clearFields();
            return;
        }
        
        titleField.setText(entry.getFindingTitle() != null ? entry.getFindingTitle() : "");
        severityCombo.setSelectedItem(entry.getSeverity());
        descriptionArea.setText(entry.getDescription() != null ? entry.getDescription() : "");
        impactArea.setText(entry.getImpact() != null ? entry.getImpact() : "");
        remediationArea.setText(entry.getRemediation() != null ? entry.getRemediation() : "");
        cvssField.setText(entry.getCvssScore() != null ? entry.getCvssScore() : "");
        
        // Tags
        if (entry.getTags() != null && !entry.getTags().isEmpty()) {
            tagsField.setText(String.join(", ", entry.getTags()));
        } else {
            tagsField.setText("");
        }
        
        // Metadata
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timestampLabel.setText(entry.getTimestamp() != null ? sdf.format(entry.getTimestamp()) : "");
        sourceLabel.setText(entry.getSource() != null ? entry.getSource() : "");
        urlLabel.setText(entry.getRequestUrl() != null ? entry.getRequestUrl() : "");
        
        // Request/Response
        if (entry.getRequest() != null) {
            IRequestInfo requestInfo = helpers.analyzeRequest(entry.getRequest());
            String requestText = new String(entry.getRequest());
            requestPane.setText(requestText);
        } else {
            requestPane.setText("");
        }
        
        if (entry.getResponse() != null) {
            IResponseInfo responseInfo = helpers.analyzeResponse(entry.getResponse());
            String responseText = new String(entry.getResponse());
            responsePane.setText(responseText);
        } else {
            responsePane.setText("");
        }
    }
    
    private void clearFields() {
        titleField.setText("");
        severityCombo.setSelectedIndex(0);
        descriptionArea.setText("");
        impactArea.setText("");
        remediationArea.setText("");
        cvssField.setText("");
        tagsField.setText("");
        timestampLabel.setText("");
        sourceLabel.setText("");
        urlLabel.setText("");
        requestPane.setText("");
        responsePane.setText("");
    }
    
    private void updateEntry() {
        if (currentEntry == null) return;
        
        currentEntry.setFindingTitle(titleField.getText());
        currentEntry.setSeverity((EvidenceEntry.Severity) severityCombo.getSelectedItem());
        currentEntry.setDescription(descriptionArea.getText());
        currentEntry.setImpact(impactArea.getText());
        currentEntry.setRemediation(remediationArea.getText());
        currentEntry.setCvssScore(cvssField.getText());
        
        // Parse tags
        String tagsText = tagsField.getText();
        if (tagsText != null && !tagsText.trim().isEmpty()) {
            String[] tags = tagsText.split(",");
            java.util.List<String> tagList = new java.util.ArrayList<>();
            for (String tag : tags) {
                String trimmed = tag.trim();
                if (!trimmed.isEmpty()) {
                    tagList.add(trimmed);
                }
            }
            currentEntry.setTags(tagList);
        } else {
            currentEntry.setTags(new java.util.ArrayList<>());
        }
    }
}

