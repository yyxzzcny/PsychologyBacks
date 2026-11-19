package org.example.psychologyback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.psychologyback.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
