package study_boot.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import study_boot.demo.entity.Result;
import study_boot.demo.entity.User;
import study_boot.demo.mapper.UserMapper;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody User loginUser) {
        String username = loginUser.getUsername();
        String password = loginUser.getPassword();

        // 从数据库查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);

        // 验证用户存在且密码匹配
        if (user != null && user.getPassword().equals(password)) {
            Map<String, String> data = new HashMap<>();
            data.put("token", "mock-token-" + System.currentTimeMillis());
            data.put("username", user.getUsername());
            data.put("nickname", user.getNickname());
            return Result.success(data);
        }

        return Result.error("用户名或密码错误");
    }
}
