package study_boot.demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import study_boot.demo.entity.Anime;
import study_boot.demo.mapper.AnimeMapper;
import study_boot.demo.service.AnimeService;

@Service
public class AnimeServiceImpl extends ServiceImpl<AnimeMapper, Anime> implements AnimeService {

    @Override
    public IPage<Anime> getPage(int pageNum, int pageSize) {
        Page<Anime> page = new Page<>(pageNum, pageSize);
        return this.page(page);
    }
}
