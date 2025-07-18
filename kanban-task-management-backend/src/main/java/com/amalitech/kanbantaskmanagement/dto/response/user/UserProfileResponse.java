package com.amalitech.kanbantaskmanagement.dto.response.user;


import com.amalitech.kanbantaskmanagement.model.jpa.enums.UserTheme;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String username;
    private String avatar;
    private UserTheme theme;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

