package burp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog for editing evidence entry details
 */
public class EvidenceEntryDialog extends JDialog {
    private EvidenceEntry entry;
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private boolean saved = false;
    
    private JTextField titleField;
    private JComboBox<EvidenceEntry.Severity> severityCombo;
    private JTextArea descriptionArea;
    private JTextArea impactArea;
    private JTextArea remediationArea;
    private JTextField cvssField;
    private JTextField tagsField;
    
    public EvidenceEntryDialog(Frame parent, EvidenceEntry entry, 
                              IBurpExtenderCallbacks callbacks, IExtensionHelpers helpers) {
        super(parent, "Evidence Entry Details", true);
        this.entry = entry;
        this.callbacks = callbacks;
        this.helpers = helpers;
        
        initializeComponents();
        loadEntryData();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Finding Title
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Finding Title *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        titleField = new JTextField(30);
        formPanel.add(titleField, gbc);
        
        // Severity
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Severity:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        severityCombo = new JComboBox<>(EvidenceEntry.Severity.values());
        formPanel.add(severityCombo, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Description/Notes:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);
        
        // Impact
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JLabel("Impact:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        impactArea = new JTextArea(3, 30);
        impactArea.setLineWrap(true);
        impactArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(impactArea), gbc);
        
        // Remediation
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JLabel("Remediation:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        remediationArea = new JTextArea(3, 30);
        remediationArea.setLineWrap(true);
        remediationArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(remediationArea), gbc);
        
        // CVSS Score
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JLabel("CVSS Score:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cvssField = new JTextField(30);
        formPanel.add(cvssField, gbc);
        
        // Tags
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        formPanel.add(new JLabel("Tags (comma-separated):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        tagsField = new JTextField(30);
        formPanel.add(tagsField, gbc);
        
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> {
            if (saveEntry()) {
                saved = true;
                dispose();
            }
        });
        
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(getParent());
    }
    
    private void loadEntryData() {
        if (entry == null) return;
        
        titleField.setText(entry.getFindingTitle() != null ? entry.getFindingTitle() : "");
        severityCombo.setSelectedItem(entry.getSeverity());
        descriptionArea.setText(entry.getDescription() != null ? entry.getDescription() : "");
        impactArea.setText(entry.getImpact() != null ? entry.getImpact() : "");
        remediationArea.setText(entry.getRemediation() != null ? entry.getRemediation() : "");
        cvssField.setText(entry.getCvssScore() != null ? entry.getCvssScore() : "");
        
        if (entry.getTags() != null && !entry.getTags().isEmpty()) {
            tagsField.setText(String.join(", ", entry.getTags()));
        }
    }
    
    private boolean saveEntry() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Finding Title is mandatory!", "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        entry.setFindingTitle(title);
        entry.setSeverity((EvidenceEntry.Severity) severityCombo.getSelectedItem());
        entry.setDescription(descriptionArea.getText());
        entry.setImpact(impactArea.getText());
        entry.setRemediation(remediationArea.getText());
        entry.setCvssScore(cvssField.getText());
        
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
            entry.setTags(tagList);
        } else {
            entry.setTags(new java.util.ArrayList<>());
        }
        
        return true;
    }
    
    public boolean isSaved() {
        return saved;
    }
}

