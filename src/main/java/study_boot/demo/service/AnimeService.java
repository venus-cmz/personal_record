package study_boot.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import study_boot.demo.entity.Anime;

public interface AnimeService extends IService<Anime> {
    IPage<Anime> getPage(int pageNum, int pageSize);
}
