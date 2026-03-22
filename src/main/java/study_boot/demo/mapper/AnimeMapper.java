package study_boot.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import study_boot.demo.entity.Anime;

@Mapper
public interface AnimeMapper extends BaseMapper<Anime> {
}
