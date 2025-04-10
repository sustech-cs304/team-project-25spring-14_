package com.example.album.controller;

import com.example.album.entity.Result;
import com.example.album.entity.User;
import com.example.album.service.UserService;
import com.example.album.utils.JwtUtil;
import com.example.album.utils.Md5Util;
import com.example.album.utils.ThreadLocalUtil;
import com.example.album.vo.UserVO;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password) {
        // 查询用户
        User user = userService.findByUsername(username);
        if (user == null) {
            userService.register(username, password);
            return Result.success();
        } else {
            return Result.error("用户名已存在");
        }
    }

    @PostMapping("/login")
    public Result login(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password) {
        // 根据用户名查询用户
        User loginUser = userService.findByUsername(username);
        // 判断该用户是否存在
        if (loginUser == null) {
            return Result.error("用户名错误");
        }

        // 判断密码是否正确 loginUser中的password是密文
        if (Md5Util.getMD5String(password).equals(loginUser.getPassword())) {
            // 登录成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", loginUser.getUserId());
            claims.put("username", loginUser.getUsername());
            claims.put("role", loginUser.getRolename());  // 将角色信息放入 claims
            String token = JwtUtil.genToken(claims);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("role", loginUser.getRolename());  // 让前端可以获取角色信息
            data.put("status", loginUser.getStatus());  // 让前端可以获取用户状态信息

            return Result.success(data);
        }

        return Result.error("密码错误");
    }

    @GetMapping("/userInfo")
    public Result<User> userInfo() {
        // 解析token
        Map<String, Object> claims = ThreadLocalUtil.get();
        // 根据用户名查询用户
        User user = userService.findByUsername((String) claims.get("username"));
        return Result.success(user);
    }

    @GetMapping("/all")
    public Result getAllUsers() {
        // 1. 解析 JWT Token
        Map<String, Object> claims = ThreadLocalUtil.get();

        // 2. 检查用户是否是管理员
        String role = (String) claims.get("role");
        if (!"admin".equals(role)) {
            return Result.error("权限不足，只有管理员可以查看用户列表");
        }

        // 3. 查询所有用户
        List<UserVO> users = userService.getAllUsers();

        return Result.success(users);
    }

    @GetMapping("/{userId}")
    public Result getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        return Result.success(user);
    }


    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user) {
        userService.update(user);
        return Result.success();
    }

    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl) {
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }

    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String, String> params) {
        // 1.校验参数
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");

        if (!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)) {
            return Result.error("缺少必要的参数");
        }

        // 原密码是否正确
        Map<String, Object> claims = ThreadLocalUtil.get();
        User loginUser = userService.findByUsername((String) claims.get("username"));
        if (!Md5Util.getMD5String(oldPwd).equals(loginUser.getPassword())) {
            return Result.error("原密码错误");
        }

        // newPwd和rePwd是否一致
        if (!newPwd.equals(rePwd)) {
            return Result.error("两次密码不一致");
        }

        // 2.调用service完成密码更新
        userService.updatePwd(newPwd);
        return Result.success();
    }

    /**
     * 根据用户id查询用户信息
     *
     * @param userId 用户id
     */
    @GetMapping("/getUser/{userId}")
    public Result<User> getUser(@PathVariable Integer userId) {
        // 1. 调用service查询用户
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 2. 返回结果
        return Result.success(user);
    }
}
