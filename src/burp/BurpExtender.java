package burp;

/**
 * Helper class to access Burp API from other classes
 */
public class BurpExtender {
    private static IBurpExtenderCallbacks callbacks;
    private static IExtensionHelpers helpers;
    
    public static void setCallbacks(IBurpExtenderCallbacks callbacks) {
        BurpExtender.callbacks = callbacks;
        BurpExtender.helpers = callbacks.getHelpers();
    }
    
    public static IBurpExtenderCallbacks getCallbacks() {
        return callbacks;
    }
    
    public static IExtensionHelpers getHelpers() {
        return helpers;
    }
}

