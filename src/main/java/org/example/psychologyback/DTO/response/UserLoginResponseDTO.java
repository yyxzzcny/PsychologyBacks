package org.example.psychologyback.DTO.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UserLoginResponseDTO {
    private String token;
    private String roleType;
    private UserDetail userInfo;
    @Builder
    @Data
    public static class UserDetail{
        private long id;
        private String username;
        private String email;
        private String nickname;
        private String avatar;
        private String phone;
        private Integer gender;
        private String genderDisplayName;
        private LocalDate birthday;
        private Integer userType;
        private String userTypeDisplayName;
        private Integer status;
        private String statusDisplayName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String displayName;
    }
}
