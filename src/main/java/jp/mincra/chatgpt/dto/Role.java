package jp.mincra.chatgpt.dto;

public enum Role {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");

    private final String string;

    Role(final String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    public static Role fromString(String str) {
        return switch (str) {
            case "system" -> Role.SYSTEM;
            case "user" -> Role.USER;
            case "assistant" -> Role.ASSISTANT;
            default -> USER;
        };
    }
}
