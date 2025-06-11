package br.dev.guereguere.entity.enums;

public enum SecurityUserCredentialEnum {

    CLIENT(2, "ROLE_CLIENTE"),
    ADMIN(1, "ROLE_ADMIN");

    private int code;
    private String description;

    SecurityUserCredentialEnum(int code, String description) {
        this.code = code;
        this.description = description;

    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static SecurityUserCredentialEnum toEnum(Integer code) {

        if (code == null) {
            return null;
        }

        for (SecurityUserCredentialEnum x : SecurityUserCredentialEnum.values()) {
            if (code.equals(x.getCode())) {
                return x;
            }
        }

        throw new IllegalArgumentException("Invalid ID: " + code);
    }

}
