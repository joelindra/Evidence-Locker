package burp;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

/**
 * Handles export of evidence entries to various formats
 */
public class ReportExporter {
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    
    public ReportExporter(IBurpExtenderCallbacks callbacks, IExtensionHelpers helpers) {
        this.callbacks = callbacks;
        this.helpers = helpers;
    }
    
    public enum ExportFormat {
        MARKDOWN("md"),
        JSON("json"),
        HTML("html"),
        DOCX("docx"),
        PDF("pdf");
        
        private final String extension;
        
        ExportFormat(String extension) {
            this.extension = extension;
        }
        
        public String getExtension() {
            return extension;
        }
    }
    
    public void export(List<EvidenceEntry> entries, File outputFile, ExportFormat format) throws IOException {
        switch (format) {
            case MARKDOWN:
                exportMarkdown(entries, outputFile);
                break;
            case JSON:
                exportJson(entries, outputFile);
                break;
            case HTML:
                exportHtml(entries, outputFile);
                break;
            case DOCX:
                exportDocx(entries, outputFile);
                break;
            case PDF:
                exportPdf(entries, outputFile);
                break;
        }
    }
    
    private void exportMarkdown(List<EvidenceEntry> entries, File outputFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("# Penetration Testing Report - Evidence Locker");
            writer.println();
            writer.println("Generated: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            writer.println();
            writer.println("---");
            writer.println();
            
            for (int i = 0; i < entries.size(); i++) {
                EvidenceEntry entry = entries.get(i);
                writer.println("## Finding #" + (i + 1) + ": " + 
                    (entry.getFindingTitle() != null ? entry.getFindingTitle() : "Untitled"));
                writer.println();
                
                writer.println("**Severity:** " + entry.getSeverity().getLabel());
                writer.println();
                
                if (entry.getCvssScore() != null && !entry.getCvssScore().trim().isEmpty()) {
                    writer.println("**CVSS Score:** " + entry.getCvssScore());
                    writer.println();
                }
                
                if (entry.getDescription() != null && !entry.getDescription().trim().isEmpty()) {
                    writer.println("### Description");
                    writer.println();
                    writer.println(entry.getDescription());
                    writer.println();
                }
                
                if (entry.getImpact() != null && !entry.getImpact().trim().isEmpty()) {
                    writer.println("### Impact");
                    writer.println();
                    writer.println(entry.getImpact());
                    writer.println();
                }
                
                if (entry.getRemediation() != null && !entry.getRemediation().trim().isEmpty()) {
                    writer.println("### Remediation");
                    writer.println();
                    writer.println(entry.getRemediation());
                    writer.println();
                }
                
                if (entry.getTags() != null && !entry.getTags().isEmpty()) {
                    writer.println("**Tags:** " + String.join(", ", entry.getTags()));
                    writer.println();
                }
                
                writer.println("**Source:** " + (entry.getSource() != null ? entry.getSource() : "Unknown"));
                writer.println();
                writer.println("**Timestamp:** " + 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entry.getTimestamp()));
                writer.println();
                
                if (entry.getRequestUrl() != null) {
                    writer.println("**URL:** " + entry.getRequestUrl());
                    writer.println();
                }
                
                if (entry.getRequest() != null) {
                    writer.println("### Request");
                    writer.println();
                    writer.println("```http");
                    writer.println(new String(entry.getRequest()));
                    writer.println("```");
                    writer.println();
                }
                
                if (entry.getResponse() != null) {
                    writer.println("### Response");
                    writer.println();
                    writer.println("```http");
                    writer.println(new String(entry.getResponse()));
                    writer.println("```");
                    writer.println();
                }
                
                writer.println("---");
                writer.println();
            }
        }
    }
    
    private void exportJson(List<EvidenceEntry> entries, File outputFile) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"reportTitle\": \"Penetration Testing Report - Evidence Locker\",\n");
        json.append("  \"generated\": \"").append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date())).append("\",\n");
        json.append("  \"findings\": [\n");
        
        for (int i = 0; i < entries.size(); i++) {
            EvidenceEntry entry = entries.get(i);
            json.append("    {\n");
            json.append("      \"id\": \"").append(escapeJson(entry.getId())).append("\",\n");
            json.append("      \"findingTitle\": \"").append(escapeJson(entry.getFindingTitle())).append("\",\n");
            json.append("      \"severity\": \"").append(entry.getSeverity().getLabel()).append("\",\n");
            json.append("      \"description\": \"").append(escapeJson(entry.getDescription())).append("\",\n");
            json.append("      \"impact\": \"").append(escapeJson(entry.getImpact())).append("\",\n");
            json.append("      \"remediation\": \"").append(escapeJson(entry.getRemediation())).append("\",\n");
            json.append("      \"cvssScore\": \"").append(escapeJson(entry.getCvssScore())).append("\",\n");
            json.append("      \"tags\": ").append(listToJsonArray(entry.getTags())).append(",\n");
            json.append("      \"timestamp\": \"").append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(entry.getTimestamp())).append("\",\n");
            json.append("      \"source\": \"").append(escapeJson(entry.getSource())).append("\",\n");
            json.append("      \"requestUrl\": \"").append(escapeJson(entry.getRequestUrl())).append("\",\n");
            json.append("      \"request\": \"").append(escapeJson(new String(entry.getRequest() != null ? entry.getRequest() : new byte[0]))).append("\",\n");
            json.append("      \"response\": \"").append(escapeJson(new String(entry.getResponse() != null ? entry.getResponse() : new byte[0]))).append("\"\n");
            json.append("    }");
            if (i < entries.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        
        json.append("  ]\n");
        json.append("}\n");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.print(json.toString());
        }
    }
    
    private void exportHtml(List<EvidenceEntry> entries, File outputFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head>");
            writer.println("  <meta charset=\"UTF-8\">");
            writer.println("  <title>Penetration Testing Report - Evidence Locker</title>");
            writer.println("  <style>");
            writer.println("    body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }");
            writer.println("    .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
            writer.println("    h1 { color: #333; border-bottom: 3px solid #4CAF50; padding-bottom: 10px; }");
            writer.println("    h2 { color: #555; margin-top: 30px; }");
            writer.println("    h3 { color: #777; }");
            writer.println("    .finding { border: 1px solid #ddd; margin: 20px 0; padding: 20px; border-radius: 5px; }");
            writer.println("    .severity-critical { border-left: 5px solid #f44336; }");
            writer.println("    .severity-high { border-left: 5px solid #ff9800; }");
            writer.println("    .severity-medium { border-left: 5px solid #ffeb3b; }");
            writer.println("    .severity-low { border-left: 5px solid #2196F3; }");
            writer.println("    .severity-info { border-left: 5px solid #9e9e9e; }");
            writer.println("    .metadata { color: #666; font-size: 0.9em; margin: 10px 0; }");
            writer.println("    pre { background: #f4f4f4; padding: 15px; border-radius: 4px; overflow-x: auto; }");
            writer.println("    code { font-family: 'Courier New', monospace; }");
            writer.println("    .tags { display: inline-block; background: #e0e0e0; padding: 3px 8px; margin: 2px; border-radius: 3px; font-size: 0.85em; }");
            writer.println("  </style>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("  <div class=\"container\">");
            writer.println("    <h1>Penetration Testing Report - Evidence Locker</h1>");
            writer.println("    <p><strong>Generated:</strong> " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "</p>");
            writer.println();
            
            for (int i = 0; i < entries.size(); i++) {
                EvidenceEntry entry = entries.get(i);
                String severityClass = "severity-" + entry.getSeverity().name().toLowerCase();
                
                writer.println("    <div class=\"finding " + severityClass + "\">");
                writer.println("      <h2>Finding #" + (i + 1) + ": " + 
                    escapeHtml(entry.getFindingTitle() != null ? entry.getFindingTitle() : "Untitled") + "</h2>");
                writer.println("      <p><strong>Severity:</strong> " + entry.getSeverity().getLabel() + "</p>");
                
                if (entry.getCvssScore() != null && !entry.getCvssScore().trim().isEmpty()) {
                    writer.println("      <p><strong>CVSS Score:</strong> " + escapeHtml(entry.getCvssScore()) + "</p>");
                }
                
                if (entry.getDescription() != null && !entry.getDescription().trim().isEmpty()) {
                    writer.println("      <h3>Description</h3>");
                    writer.println("      <p>" + escapeHtml(entry.getDescription()).replace("\n", "<br>") + "</p>");
                }
                
                if (entry.getImpact() != null && !entry.getImpact().trim().isEmpty()) {
                    writer.println("      <h3>Impact</h3>");
                    writer.println("      <p>" + escapeHtml(entry.getImpact()).replace("\n", "<br>") + "</p>");
                }
                
                if (entry.getRemediation() != null && !entry.getRemediation().trim().isEmpty()) {
                    writer.println("      <h3>Remediation</h3>");
                    writer.println("      <p>" + escapeHtml(entry.getRemediation()).replace("\n", "<br>") + "</p>");
                }
                
                if (entry.getTags() != null && !entry.getTags().isEmpty()) {
                    writer.println("      <p><strong>Tags:</strong> ");
                    for (String tag : entry.getTags()) {
                        writer.println("        <span class=\"tags\">" + escapeHtml(tag) + "</span>");
                    }
                    writer.println("      </p>");
                }
                
                writer.println("      <div class=\"metadata\">");
                writer.println("        <strong>Source:</strong> " + escapeHtml(entry.getSource() != null ? entry.getSource() : "Unknown") + " | ");
                writer.println("        <strong>Timestamp:</strong> " + 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entry.getTimestamp()));
                if (entry.getRequestUrl() != null) {
                    writer.println("        | <strong>URL:</strong> " + escapeHtml(entry.getRequestUrl()));
                }
                writer.println("      </div>");
                
                if (entry.getRequest() != null) {
                    writer.println("      <h3>Request</h3>");
                    writer.println("      <pre><code>" + escapeHtml(new String(entry.getRequest())) + "</code></pre>");
                }
                
                if (entry.getResponse() != null) {
                    writer.println("      <h3>Response</h3>");
                    writer.println("      <pre><code>" + escapeHtml(new String(entry.getResponse())) + "</code></pre>");
                }
                
                writer.println("    </div>");
            }
            
            writer.println("  </div>");
            writer.println("</body>");
            writer.println("</html>");
        }
    }
    
    private void exportDocx(List<EvidenceEntry> entries, File outputFile) throws IOException {
        // DOCX is a complex format, we'll create a simple HTML-like structure
        // For a full DOCX implementation, you would need Apache POI library
        // For now, we'll create a basic text-based representation
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("PENETRATION TESTING REPORT - EVIDENCE LOCKER");
            writer.println(repeat("=", 60));
            writer.println();
            writer.println("Generated: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            writer.println();
            writer.println(repeat("=", 60));
            writer.println();
            
            for (int i = 0; i < entries.size(); i++) {
                EvidenceEntry entry = entries.get(i);
                writer.println("FINDING #" + (i + 1) + ": " + 
                    (entry.getFindingTitle() != null ? entry.getFindingTitle() : "Untitled"));
                writer.println(repeat("-", 60));
                writer.println();
                writer.println("Severity: " + entry.getSeverity().getLabel());
                writer.println();
                
                if (entry.getCvssScore() != null && !entry.getCvssScore().trim().isEmpty()) {
                    writer.println("CVSS Score: " + entry.getCvssScore());
                    writer.println();
                }
                
                if (entry.getDescription() != null && !entry.getDescription().trim().isEmpty()) {
                    writer.println("Description:");
                    writer.println(entry.getDescription());
                    writer.println();
                }
                
                if (entry.getImpact() != null && !entry.getImpact().trim().isEmpty()) {
                    writer.println("Impact:");
                    writer.println(entry.getImpact());
                    writer.println();
                }
                
                if (entry.getRemediation() != null && !entry.getRemediation().trim().isEmpty()) {
                    writer.println("Remediation:");
                    writer.println(entry.getRemediation());
                    writer.println();
                }
                
                if (entry.getTags() != null && !entry.getTags().isEmpty()) {
                    writer.println("Tags: " + String.join(", ", entry.getTags()));
                    writer.println();
                }
                
                writer.println("Source: " + (entry.getSource() != null ? entry.getSource() : "Unknown"));
                writer.println("Timestamp: " + 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entry.getTimestamp()));
                if (entry.getRequestUrl() != null) {
                    writer.println("URL: " + entry.getRequestUrl());
                }
                writer.println();
                
                if (entry.getRequest() != null) {
                    writer.println("Request:");
                    writer.println(repeat("-", 60));
                    writer.println(new String(entry.getRequest()));
                    writer.println();
                }
                
                if (entry.getResponse() != null) {
                    writer.println("Response:");
                    writer.println(repeat("-", 60));
                    writer.println(new String(entry.getResponse()));
                    writer.println();
                }
                
                writer.println(repeat("=", 60));
                writer.println();
            }
        }
        
        // Note: For proper DOCX support, you would need to add Apache POI dependency
        // and implement proper DOCX generation
    }
    
    private void exportPdf(List<EvidenceEntry> entries, File outputFile) throws IOException {
        // PDF generation requires additional libraries like iText or Apache PDFBox
        // For now, we'll create a text file that can be converted to PDF
        // In a production environment, you would use a PDF library
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("PENETRATION TESTING REPORT - EVIDENCE LOCKER");
            writer.println(repeat("=", 60));
            writer.println();
            writer.println("Generated: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            writer.println();
            writer.println(repeat("=", 60));
            writer.println();
            
            for (int i = 0; i < entries.size(); i++) {
                EvidenceEntry entry = entries.get(i);
                writer.println("FINDING #" + (i + 1) + ": " + 
                    (entry.getFindingTitle() != null ? entry.getFindingTitle() : "Untitled"));
                writer.println(repeat("-", 60));
                writer.println();
                writer.println("Severity: " + entry.getSeverity().getLabel());
                writer.println();
                
                if (entry.getCvssScore() != null && !entry.getCvssScore().trim().isEmpty()) {
                    writer.println("CVSS Score: " + entry.getCvssScore());
                    writer.println();
                }
                
                if (entry.getDescription() != null && !entry.getDescription().trim().isEmpty()) {
                    writer.println("Description:");
                    writer.println(entry.getDescription());
                    writer.println();
                }
                
                if (entry.getImpact() != null && !entry.getImpact().trim().isEmpty()) {
                    writer.println("Impact:");
                    writer.println(entry.getImpact());
                    writer.println();
                }
                
                if (entry.getRemediation() != null && !entry.getRemediation().trim().isEmpty()) {
                    writer.println("Remediation:");
                    writer.println(entry.getRemediation());
                    writer.println();
                }
                
                if (entry.getTags() != null && !entry.getTags().isEmpty()) {
                    writer.println("Tags: " + String.join(", ", entry.getTags()));
                    writer.println();
                }
                
                writer.println("Source: " + (entry.getSource() != null ? entry.getSource() : "Unknown"));
                writer.println("Timestamp: " + 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entry.getTimestamp()));
                if (entry.getRequestUrl() != null) {
                    writer.println("URL: " + entry.getRequestUrl());
                }
                writer.println();
                
                if (entry.getRequest() != null) {
                    writer.println("Request:");
                    writer.println(repeat("-", 60));
                    writer.println(new String(entry.getRequest()));
                    writer.println();
                }
                
                if (entry.getResponse() != null) {
                    writer.println("Response:");
                    writer.println(repeat("-", 60));
                    writer.println(new String(entry.getResponse()));
                    writer.println();
                }
                
                writer.println(repeat("=", 60));
                writer.println();
            }
        }
        
        // Note: For proper PDF support, you would need to add iText or Apache PDFBox
        // and implement proper PDF generation
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    private String escapeHtml(String str) {
        if (str == null) return "";
        return str.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
    
    private String listToJsonArray(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(escapeJson(list.get(i))).append("\"");
            if (i < list.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    private String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}

