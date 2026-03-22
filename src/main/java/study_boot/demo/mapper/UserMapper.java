package study_boot.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import study_boot.demo.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
