package burp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data model for an evidence entry
 */
public class EvidenceEntry {
    private String id;
    private String findingTitle;
    private Severity severity;
    private String description;
    private String impact;
    private String remediation;
    private String cvssScore;
    private List<String> tags;
    private Date timestamp;
    private String source; // Repeater, Proxy, Scanner
    private byte[] request;
    private byte[] response;
    private String requestUrl;
    
    public EvidenceEntry() {
        this.id = generateId();
        this.timestamp = new Date();
        this.tags = new ArrayList<>();
        this.severity = Severity.INFO;
    }
    
    public EvidenceEntry(IHttpRequestResponse message, String source, IExtensionHelpers helpers) {
        this();
        this.source = source;
        if (message != null && helpers != null) {
            this.request = message.getRequest();
            this.response = message.getResponse();
            IRequestInfo requestInfo = helpers.analyzeRequest(message);
            this.requestUrl = requestInfo.getUrl().toString();
        }
    }
    
    private String generateId() {
        return "EVID-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFindingTitle() { return findingTitle; }
    public void setFindingTitle(String findingTitle) { this.findingTitle = findingTitle; }
    
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImpact() { return impact; }
    public void setImpact(String impact) { this.impact = impact; }
    
    public String getRemediation() { return remediation; }
    public void setRemediation(String remediation) { this.remediation = remediation; }
    
    public String getCvssScore() { return cvssScore; }
    public void setCvssScore(String cvssScore) { this.cvssScore = cvssScore; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public byte[] getRequest() { return request; }
    public void setRequest(byte[] request) { this.request = request; }
    
    public byte[] getResponse() { return response; }
    public void setResponse(byte[] response) { this.response = response; }
    
    public String getRequestUrl() { return requestUrl; }
    public void setRequestUrl(String requestUrl) { this.requestUrl = requestUrl; }
    
    public enum Severity {
        CRITICAL("Critical", Color.RED),
        HIGH("High", new Color(255, 140, 0)), // Orange
        MEDIUM("Medium", Color.YELLOW),
        LOW("Low", Color.BLUE),
        INFO("Info", Color.GRAY);
        
        private final String label;
        private final Color color;
        
        Severity(String label, Color color) {
            this.label = label;
            this.color = color;
        }
        
        public String getLabel() { return label; }
        public Color getColor() { return color; }
    }
}

