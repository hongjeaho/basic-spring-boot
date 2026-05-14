package kr.go.kaptnet.user.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

	int findUserIdByUserSeqNo(String userid);
}
