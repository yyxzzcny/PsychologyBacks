package org.example.psychologyback.service.convert;

import org.example.psychologyback.DTO.comand.UserRegisterCommandDTO;
import org.example.psychologyback.DTO.response.UserLoginResponseDTO;
import org.example.psychologyback.entity.User;
import org.example.psychologyback.enumClass.UserStatus;

import java.time.LocalDateTime;

public class UserConvert {
    /**
     * User实体转换为详情响应DTO
     *
     * @param user User实体
     * @return 用户详情响应DTO
     */
    public static UserLoginResponseDTO.UserDetail entityToDetailResponse(User user) {
        return UserLoginResponseDTO.UserDetail.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .gender(user.getGender())
                .genderDisplayName(getGenderDisplayName(user.getGender()))
                .birthday(user.getBirthday())
                .userType(user.getUserType())
                .userTypeDisplayName(user.getUserTypeDisplayName())
                .status(user.getStatus())
                .statusDisplayName(user.getStatusDisplayName())
                .displayName(user.getDisplayName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    public static UserLoginResponseDTO buildLoginResponse(String token, UserLoginResponseDTO.UserDetail userInfo) {
        return UserLoginResponseDTO.builder()
                .userInfo(userInfo)
                .token(token)
                .roleType(userInfo.getUserType().toString())
                .build();
    }
    /**
     * 获取性别显示名称
     * @param gender 性别代码
     * @return 性别显示名称
     */
    private static String getGenderDisplayName(Integer gender) {
        if (gender == null) {
            return "未知";
        }
        switch (gender) {
            case 1:
                return "男";
            case 2:
                return "女";
            default:
                return "未知";
        }
    }
    //DTO转化成注册信息的实体类
    public static User RegisterCommandtoEntity(UserRegisterCommandDTO userRegisterCommandDTO,String encodedPassword) {
        return User.builder()
                .username(userRegisterCommandDTO.getUsername())
                .email(userRegisterCommandDTO.getEmail())
                .password(encodedPassword)
                .nickname(userRegisterCommandDTO.getNickname())
                .phone(userRegisterCommandDTO.getPhone())
                .gender(userRegisterCommandDTO.getGender())
                .birthday(userRegisterCommandDTO.getBirthday())
                .userType(userRegisterCommandDTO.getUserType())
                .status(UserStatus.NORMAL.getCode())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
