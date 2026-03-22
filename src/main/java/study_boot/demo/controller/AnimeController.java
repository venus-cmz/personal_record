package study_boot.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import study_boot.demo.entity.Anime;
import study_boot.demo.entity.Result;
import study_boot.demo.service.AnimeService;

@RestController
@RequestMapping("/api/anime")
@CrossOrigin(origins = "*")
public class AnimeController {

    @Autowired
    private AnimeService animeService;

    // 获取动漫列表（分页）
    @GetMapping("/list")
    public Result<IPage<Anime>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) {
        IPage<Anime> page = animeService.getPage(pageNum, pageSize);
        return Result.success(page);
    }

    // 获取动漫详情
    @GetMapping("/{id}")
    public Result<Anime> getById(@PathVariable Long id) {
        Anime anime = animeService.getById(id);
        if (anime != null) {
            return Result.success(anime);
        }
        return Result.error("动漫不存在");
    }

    // 创建动漫
    @PostMapping
    public Result<String> create(@RequestBody Anime anime) {
        // 检查名称是否已存在
        LambdaQueryWrapper<Anime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Anime::getName, anime.getName());
        Anime existAnime = animeService.getOne(wrapper);
        if (existAnime != null) {
            return Result.error("该名称已存在");
        }
        
        boolean success = animeService.save(anime);
        if (success) {
            return Result.success("创建成功");
        }
        return Result.error("创建失败");
    }

    // 更新动漫
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Anime anime) {
        // 检查名称是否与其他动漫重复（排除自己）
        LambdaQueryWrapper<Anime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Anime::getName, anime.getName()).ne(Anime::getId, id);
        Anime existAnime = animeService.getOne(wrapper);
        if (existAnime != null) {
            return Result.error("该名称已存在");
        }
        
        anime.setId(id);
        boolean success = animeService.updateById(anime);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    // 删除动漫
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = animeService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
