package com.example.ptweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 根据 username 查询
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    // 根据 email 查询
    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    // 根据 passkey（忽略大小写）查询
    @Select("SELECT * FROM user WHERE LOWER(passkey) = LOWER(#{passkey})")
    User findByPasskeyIgnoreCase(@Param("passkey") String passkey);

    // 根据 personalAccessToken（忽略大小写）查询
    @Select("SELECT * FROM user WHERE LOWER(personal_access_token) = LOWER(#{personalAccessToken})")
    User findByPersonalAccessTokenIgnoreCase(@Param("personalAccessToken") String personalAccessToken);

    // 根据 email 模糊查询
    List<User> findByEmailContains(@Param("emailPart") String emailPart);

    // 根据 username 模糊查询
    List<User> findByUsernameContains(@Param("usernamePart") String usernamePart);
}
