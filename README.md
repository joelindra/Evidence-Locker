# Evidence Locker - Burp Suite Extension

A Burp Suite extension designed to simplify penetration testing documentation with comprehensive features for storing, managing, and exporting evidence.

## 1. Extension Overview

**Evidence Locker** is a Burp Suite extension specifically designed to help penetration testers and security auditors systematically and efficiently document security findings. This extension integrates directly into the Burp Suite workflow, allowing you to quickly capture, organize, and export evidence from various Burp Suite tools such as Proxy, Repeater, and Scanner.

### Key Benefits:

- **Time Saving**: No more manual copy-pasting of requests/responses to separate documents. All evidence is captured automatically with a single click.
- **Documentation Consistency**: Standard format for all findings ensures consistent and professional documentation.
- **Better Organization**: All findings are centralized in one place with filtering capabilities and color-coding based on severity.
- **Flexible Export**: Generate reports in various formats (Markdown, JSON, HTML, DOCX, PDF) according to team or client needs.
- **Integrated Workflow**: Work directly from within Burp Suite without needing to switch to external applications.

### Core Functionality:

This extension provides a complete evidence management system, from automatic capture of requests/responses from various Burp Suite tools, filling in finding metadata (severity, description, impact, remediation, CVSS score, tags), to exporting professional reports in various formats. All data is stored in memory during the active Burp Suite session, allowing you to edit, delete, and organize findings before exporting to the final report.

## 2. Key Features

### 2.1 Integration with Burp Suite Tools

- **Context Menu Integration**: "Send to Evidence Locker" menu available in:
  - **Proxy History**: Capture request/response from intercepted traffic
  - **Repeater**: Save manual testing results from Repeater
  - **Scanner Results**: Capture findings from automated scanner
  - **Message Editor/Viewer**: Capture from request editor or response viewer
- **Automatic Snapshot**: Request and response are automatically captured along with:
  - Target URL
  - Automatic timestamp
  - Source tool (Proxy, Repeater, Scanner, etc.)

### 2.2 Findings Management

- **Evidence Locker Tab**: Dedicated tab at the top level of Burp Suite for quick access
- **Color-Coded Severity**: Findings are displayed with different colors based on severity:
  - 🔴 **Critical** (Red)
  - 🟠 **High** (Orange)
  - 🟡 **Medium** (Yellow)
  - 🔵 **Low** (Blue)
  - ⚪ **Info** (Gray)
- **Findings List**: Left panel displays all findings with title and color-coding
- **Detail View**: Right panel displays complete details of the selected finding
- **Create New Finding**: Button to create a new finding without request/response (for findings discovered outside Burp Suite)
- **Delete Finding**: Delete finding with confirmation to prevent accidental deletion

### 2.3 Finding Input Form

- **Finding Title** (Mandatory): Finding title that must be filled
- **Severity**: Dropdown with 5 levels (Critical, High, Medium, Low, Info)
- **Description/Notes**: Text area for detailed finding description with multi-line support
- **Impact**: Field to explain the impact of the vulnerability
- **Remediation**: Field for remediation recommendations
- **CVSS Score**: Optional field for CVSS score
- **Tags**: Comma-separated input for categorization (e.g., SQLi, XSS, Auth, IDOR, etc.)

### 2.4 Request/Response Viewer

- **Syntax-Highlighted Display**: Request and response displayed in easily readable monospace format
- **Side-by-Side View**: Request and response displayed side-by-side for easy comparison
- **Scrollable**: Support for long requests/responses
- **Read-Only Display**: Request/response displayed as reference (cannot be changed after capture)

### 2.5 Export Capabilities

The extension supports export to 5 different formats:

- **Markdown (.md)**: 
  - Format suitable for GitHub, GitLab, or Confluence
  - Syntax highlighting for code blocks
  - Easy-to-read structure and version control friendly
  
- **JSON (.json)**:
  - Structured format for automated processing
  - Suitable for integration with other tools or scripting
  - All metadata included in JSON format
  
- **HTML (.html)**:
  - Professional report with CSS styling
  - Color-coded findings based on severity
  - Ready to share or print
  - Responsive design
  
- **DOCX (.docx)**:
  - Microsoft Word format
  - Suitable for formal client reports
  - Can be further edited in Word
  
- **PDF (.pdf)**:
  - Final format ready to send to clients
  - Professional layout
  - Non-editable (for documentation security)

### 2.6 Automatic Metadata

Each finding automatically includes:
- **Unique ID**: Unique ID for each finding (format: EVID-timestamp-random)
- **Timestamp**: Time when the finding was added
- **Source Tool**: Burp Suite tool used (Proxy, Repeater, Scanner)
- **Request URL**: Target URL from the captured request

## 3. Usage Instructions

### 3.1 Installing the Extension

1. **Build Extension** (if not already built):
   ```bash
   ant build
   ```
   The JAR file will be available at `build/dist/EvidenceLocker.jar`

2. **Load Extension in Burp Suite**:
   - Open Burp Suite
   - Go to menu **Extender** → **Extensions**
   - Click the **Add** button
   - Select tab **Extension type: Java**
   - Click **Select file** and choose the `EvidenceLocker.jar` file
   - Click **Next** to load the extension
   - Ensure the status shows "Loaded successfully"

3. **Verify Installation**:
   - After the extension is loaded, a new **"Evidence Locker"** tab will appear at the top of Burp Suite
   - Click the tab to open the Evidence Locker interface

### 3.2 Adding Evidence from Burp Suite Tools

#### From Proxy History:

1. Open tab **Proxy** → **HTTP history**
2. Select the request/response you want to use as evidence
3. **Right-click** on the selected item
4. Choose **"Send to Evidence Locker"** from the context menu
5. The **Evidence Entry Details** dialog will appear
6. Fill in the finding information:
   - **Finding Title** (required): Enter the finding title, e.g., "SQL Injection in Login Form"
   - **Severity**: Select severity level from the dropdown
   - **Description/Notes**: Describe the details of the discovered vulnerability
   - **Impact**: Explain the impact if the vulnerability is exploited
   - **Remediation**: Provide remediation recommendations
   - **CVSS Score**: (Optional) Enter CVSS score if available
   - **Tags**: Enter tags for categorization, separated by commas (e.g., `SQLi, Authentication, Critical`)
7. Click **Save** to save the finding

#### From Repeater:

1. Open tab **Repeater**
2. Perform manual testing on the request
3. After obtaining a response that shows the vulnerability:
   - **Right-click** on the request editor or response viewer
   - Select **"Send to Evidence Locker"**
4. Fill in the form as described above
5. Click **Save**

#### From Scanner Results:

1. Open tab **Scanner** → **Scan results**
2. Select a finding from the scan results
3. **Right-click** on the finding
4. Select **"Send to Evidence Locker"**
5. The form will be automatically populated with some information from the scanner
6. Complete any missing information
7. Click **Save**

### 3.3 Creating a New Finding (Without Request/Response)

For findings discovered outside Burp Suite or to be added manually:

1. Open the **Evidence Locker** tab
2. Click the **"New Finding"** button in the bottom left panel
3. A new finding will appear in the list with status "Untitled Finding"
4. Click on the finding to open the detail panel
5. Fill in all fields in the right panel:
   - Finding Title
   - Severity
   - Description
   - Impact
   - Remediation
   - CVSS Score (optional)
   - Tags (optional)
6. All changes are saved automatically as you type

### 3.4 Editing an Existing Finding

1. Open the **Evidence Locker** tab
2. In the left panel, **click** on the finding you want to edit
3. The finding details will appear in the right panel
4. Edit the desired fields:
   - All fields can be edited directly
   - Changes are saved automatically as you type
5. To view request/response, scroll down in the detail panel

### 3.5 Deleting a Finding

1. Open the **Evidence Locker** tab
2. In the left panel, **select** the finding you want to delete
3. Click the **"Delete"** button in the bottom left panel
4. A confirmation dialog will appear
5. Click **Yes** to confirm deletion
6. The finding will be removed from the list

**Note**: Deletion cannot be undone. Make sure you have exported important findings before deleting.

### 3.6 Exporting Reports

The extension supports export to 5 formats. Here are the steps:

#### Export to Markdown:

1. Ensure you have at least one finding in Evidence Locker
2. At the bottom of the Evidence Locker tab, click the **"Export Markdown"** button
3. A **Save As** dialog will appear
4. Choose the save location and name the file (e.g., `pentest_report.md`)
5. Click **Save**
6. A success notification will appear if export is successful
7. The Markdown file is ready to use for GitHub, GitLab, or Confluence

#### Export to JSON:

1. Click the **"Export JSON"** button
2. Choose the location and file name (e.g., `findings.json`)
3. Click **Save**
4. The JSON file can be used for automated processing or integration with other tools

#### Export to HTML:

1. Click the **"Export HTML"** button
2. Choose the location and file name (e.g., `report.html`)
3. Click **Save**
4. Open the HTML file in a browser to view the formatted report

#### Export to DOCX:

1. Click the **"Export DOCX"** button
2. Choose the location and file name (e.g., `final_report.docx`)
3. Click **Save**
4. The file can be opened and further edited in Microsoft Word

#### Export to PDF:

1. Click the **"Export PDF"** button
2. Choose the location and file name (e.g., `client_report.pdf`)
3. Click **Save**
4. The PDF file is ready to send to the client

**Note**: 
- If there are no findings, the export button will display a warning
- All findings will be exported in a single file
- The export format will sort findings according to the order in the list

### 3.7 Usage Tips

1. **Organization with Tags**: Use consistent tags to facilitate filtering and categorization (e.g., `SQLi`, `XSS`, `Auth`, `IDOR`, `CSRF`)

2. **Appropriate Severity**: Ensure severity matches the actual impact:
   - **Critical**: Can cause complete system compromise
   - **High**: Can cause unauthorized access or data exposure
   - **Medium**: Can cause significant security issues
   - **Low**: Minor security issues
   - **Info**: Useful information to be aware of

3. **Complete Description**: 
   - Explain how the vulnerability was discovered
   - Include reproduction steps if possible
   - Add screenshots or references if needed

4. **Specific Remediation**: 
   - Provide actionable recommendations
   - Include code examples or configuration if relevant

5. **Regular Exports**: 
   - Export findings regularly during pentest for backup
   - Use JSON format for raw data backup
   - Use HTML/PDF format for client reports

6. **Review Before Export**: 
   - Ensure all findings are complete before final export
   - Double-check severity and descriptions for accuracy

## UI Layout

- **Left Panel**: List of all findings with severity color-coded
- **Right Panel**: Detail view with request/response in syntax-highlighted format
- **Bottom Panel**: Export buttons for various formats

## Build Instructions

### Prerequisites

1. Java JDK 8 or higher
2. Apache Ant
3. Burp Suite API (burp.jar) in the `lib/` folder

### How to Obtain burp.jar

1. Open Burp Suite
2. Go to **Extender -> APIs**
3. Click **"Save interface definitions"**
4. Copy the saved `burpsuite_api.jar` file to the `lib/` folder and rename it to `burp.jar`

### Build

```bash
ant build
```

The JAR file will be created at `build/dist/EvidenceLocker.jar`

### Install in Burp Suite

1. Open Burp Suite
2. Go to **Extender -> Extensions**
3. Click **Add**
4. Select tab **Extension type: Java**
5. Click **Select file** and choose `build/dist/EvidenceLocker.jar`
6. Click **Next** to load the extension

## Project Structure

```
Evidence Locker/
├── lib/
│   ├── burp.jar          # Burp Suite API (must be added manually)
│   └── README.txt
├── src/
│   └── burp/
│       ├── EvidenceLocker.java          # Main extension class
│       ├── EvidenceEntry.java           # Data model
│       ├── EvidenceLockerPanel.java     # Main UI panel
│       ├── EvidenceDetailPanel.java     # Detail view panel
│       ├── EvidenceEntryDialog.java     # Dialog for editing entry
│       ├── ReportExporter.java          # Export functionality
│       └── BurpExtender.java            # Helper class
├── build.xml                             # Ant build file
└── README.md                             # This documentation
```

## Notes

- This extension uses Java Swing for the UI
- For more complete DOCX and PDF export, it is recommended to add libraries such as Apache POI (for DOCX) and iText/Apache PDFBox (for PDF)
- Currently DOCX and PDF export use simple text format that can be converted manually or with external tools

## License

This extension is created for pentest and security documentation purposes.

