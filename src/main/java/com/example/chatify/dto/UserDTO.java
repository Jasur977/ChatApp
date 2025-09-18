package com.example.chatify.dto;

import com.example.chatify.model.User;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDTO {
    private Long id;
    private String username;
    private String displayName;
    private Set<FriendDTO> friends;

    // Inner class for friends
    public static class FriendDTO {
        private Long id;
        private String username;
        private String displayName;

        public FriendDTO(Long id, String username, String displayName) {
            this.id = id;
            this.username = username;
            this.displayName = displayName;
        }

        // Getters
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getDisplayName() { return displayName; }
    }

    // Convert User -> UserDTO
    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.displayName = user.getDisplayName();
        dto.friends = user.getFriends().stream()
                .map(f -> new FriendDTO(f.getId(), f.getUsername(), f.getDisplayName()))
                .collect(Collectors.toSet());
        return dto;
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public Set<FriendDTO> getFriends() { return friends; }
}
