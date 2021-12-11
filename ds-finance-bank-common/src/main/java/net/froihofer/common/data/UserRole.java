package net.froihofer.common.data;

public enum UserRole {
    EMPLOYEE("employee"),
    CUSTOMER("customer");

    private final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
