package study_boot.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("anime")
public class Anime {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;           // 名称
    private String image;          // 封面图片URL
    private String bgImg;         // 背景图片URL
    private String description;    // 简介
    private String evaluation;    // 评价
    private String rating;        // 评分
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
