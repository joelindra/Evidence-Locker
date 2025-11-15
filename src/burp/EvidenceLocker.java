package burp;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Main extension class for Evidence Locker
 */
public class EvidenceLocker implements IBurpExtender, IContextMenuFactory, ITab {
    
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private PrintWriter stdout;
    private PrintWriter stderr;
    
    private EvidenceLockerPanel mainPanel;
    private List<EvidenceEntry> evidenceEntries;
    
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        this.helpers = callbacks.getHelpers();
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.stderr = new PrintWriter(callbacks.getStderr(), true);
        this.evidenceEntries = new ArrayList<>();
        
        // Set callbacks in helper class
        BurpExtender.setCallbacks(callbacks);
        
        callbacks.setExtensionName("Evidence Locker");
        
        // Create main panel
        mainPanel = new EvidenceLockerPanel(callbacks, helpers, evidenceEntries);
        
        // Register context menu factory
        callbacks.registerContextMenuFactory(this);
        
        // Register tab
        callbacks.addSuiteTab(this);
        
        stdout.println("Evidence Locker extension loaded successfully!");
    }
    
    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        List<JMenuItem> menuItems = new ArrayList<>();
        
        // Check if we're in a context where we can capture request/response
        if (invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_REQUEST ||
            invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_RESPONSE ||
            invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_VIEWER_REQUEST ||
            invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_VIEWER_RESPONSE ||
            invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_PROXY_HISTORY ||
            invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_TARGET_SITE_MAP_TABLE ||
            invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_SCANNER_RESULTS) {
            
            IHttpRequestResponse[] messages = invocation.getSelectedMessages();
            if (messages != null && messages.length > 0) {
                JMenuItem sendToLocker = new JMenuItem("Send to Evidence Locker");
                sendToLocker.addActionListener(e -> {
                    for (IHttpRequestResponse message : messages) {
                        String source = getSourceFromContext(invocation);
                        mainPanel.addEvidenceEntry(message, source);
                    }
                });
                menuItems.add(sendToLocker);
            }
        }
        
        return menuItems;
    }
    
    private String getSourceFromContext(IContextMenuInvocation invocation) {
        int context = invocation.getInvocationContext();
        switch (context) {
            case IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_REQUEST:
            case IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_RESPONSE:
                return "Repeater";
            case IContextMenuInvocation.CONTEXT_PROXY_HISTORY:
                return "Proxy";
            case IContextMenuInvocation.CONTEXT_SCANNER_RESULTS:
                return "Scanner";
            default:
                return "Unknown";
        }
    }
    
    @Override
    public String getTabCaption() {
        return "Evidence Locker";
    }
    
    @Override
    public Component getUiComponent() {
        return mainPanel;
    }
}

