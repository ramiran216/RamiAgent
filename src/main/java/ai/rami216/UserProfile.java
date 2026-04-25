package ai.rami216;

import java.time.LocalDateTime;

public class UserProfile {

    private final String conversationId;
    private final String summary;
    private final LocalDateTime updatedAt;

    public UserProfile(String conversationId, String summary, LocalDateTime updatedAt) {
        this.conversationId = conversationId;
        this.summary = summary;
        this.updatedAt = updatedAt;
    }

    public String conversationId() {
        return conversationId;
    }

    public String summary() {
        return summary;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return conversationId.equals(that.conversationId) && summary.equals(that.summary) && updatedAt.equals(that.updatedAt);
    }

    @Override
    public int hashCode() {
        int result = conversationId.hashCode();
        result = 31 * result + summary.hashCode();
        result = 31 * result + updatedAt.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "conversationId='" + conversationId + '\'' +
                ", summary='" + summary + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
