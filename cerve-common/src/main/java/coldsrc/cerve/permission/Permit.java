package coldsrc.cerve.permission;

public enum Permit {

    /**
     * Explicitly grants the permission.
     */
    ALLOW(true),

    /**
     * The permission is not set.
     */
    UNSET(null),

    /**
     * Explicitly denies the permission.
     */
    DENY(false);

    private final Boolean boolValue;

    Permit(Boolean boolValue) {
        this.boolValue = boolValue;
    }

    /**
     * Get the boolean value associated with this setting.
     *
     * @return The boolean value or null if unset.
     */
    public Boolean asBoolean() {
        return boolValue;
    }

    /**
     * Get the boolean value associated with this setting
     * or the given default if unset.
     *
     * @param def The default value if unset.
     * @return The boolean value or the given default if unset.
     */
    public boolean asBoolean(boolean def) {
        return boolValue == null ? def : boolValue;
    }

}
