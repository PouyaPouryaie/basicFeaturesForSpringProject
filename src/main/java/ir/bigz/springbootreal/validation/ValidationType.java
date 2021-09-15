package ir.bigz.springbootreal.validation;

public enum ValidationType {

    EMAIL ("email pattern is not correct", new String[]{}),
    MOBILE ("mobile number is not correct", new String[]{}),
    GENDER ("gender type is not correct", new String[]{"male", "female"}),
    NATIONAL_CODE ("national code is not correct", new String[]{});

    private final String failedMessage;
    private final String[] defaultValue;

    ValidationType(String failedMessage, String[] defaultValue) {
        this.failedMessage = failedMessage;
        this.defaultValue = defaultValue;
    }

    public String getFailedMessage() {
        return failedMessage;
    }

    public String[] getDefaultValue() {
        return defaultValue;
    }
}
