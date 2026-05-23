package kr.go.kaptnet.user.mapper;

import kr.go.kaptnet.authority.AuthUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

	AuthUser findUserIdByUserId(String userid);
}
