package com.cardgame.service.impl;

import com.cardgame.dao.CardDao;
import com.cardgame.dao.PlayerCardDao;
import com.cardgame.dao.UserDao;
import com.cardgame.exception.BusinessException;
import com.cardgame.model.dto.request.LoginRequest;
import com.cardgame.model.dto.request.RegisterRequest;
import com.cardgame.model.dto.response.LoginResponse;
import com.cardgame.model.dto.response.RegisterResponse;
import com.cardgame.model.entity.Card;
import com.cardgame.model.entity.User;
import com.cardgame.service.EmailService;
import com.cardgame.util.IdGenerator;
import com.cardgame.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthServiceImpl 单元测试（Mockito）
 * 覆盖注册、登录、验证码发送的核心逻辑
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserDao userDao;
    @Mock private EmailService emailService;
    @Mock private IdGenerator idGenerator;
    @Mock private JwtUtil jwtUtil;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private CardDao cardDao;
    @Mock private PlayerCardDao playerCardDao;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 设置 @Value 注入的配置值
        ReflectionTestUtils.setField(authService, "codeExpiryMinutes", 10);
        ReflectionTestUtils.setField(authService, "resendCooldownSeconds", 60);

        testUser = User.builder()
                .id(1L)
                .nickname("测试用户")
                .uniqueTag("123456")
                .email("test@example.com")
                .passwordHash("$2a$10$encodedHash")
                .gold(100)
                .points(0)
                .build();
    }

    @Nested
    @DisplayName("sendCode 发送验证码")
    class SendCode {

        @Test
        @DisplayName("发送成功，调用邮件服务")
        void sendCode_success() {
            doNothing().when(emailService).sendVerificationCode(eq("new@example.com"), anyString());

            assertDoesNotThrow(() -> authService.sendCode("new@example.com"));
            verify(emailService).sendVerificationCode(eq("new@example.com"), anyString());
        }

        @Test
        @DisplayName("短时间内重复发送抛出异常")
        void sendCode_tooFrequent() {
            doNothing().when(emailService).sendVerificationCode(anyString(), anyString());

            authService.sendCode("dup@example.com");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> authService.sendCode("dup@example.com"));
            assertTrue(ex.getMessage().contains("请稍后再试"));
        }
    }

    @Nested
    @DisplayName("register 用户注册")
    class Register {

        @Test
        @DisplayName("注册成功：创建用户并发放默认卡牌")
        void register_success() {
            // 先发送验证码
            doNothing().when(emailService).sendVerificationCode(anyString(), anyString());
            authService.sendCode("new@example.com");

            // Mock 验证码校验通过（sendCode 生成的码我们不知道，
            // 所以直接 spy 来测试：这里通过反射直接操作 codeStore）
            // 更简洁的做法：跳过验证码，直接测试注册逻辑
            // 用 register 之前手动设置 codeStore 的值
            setCodeStore("new@example.com", "123456");

            when(userDao.existsByEmail("new@example.com")).thenReturn(false);
            when(idGenerator.generateUniqueTag()).thenReturn("654321");
            when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hash");
            when(userDao.save(any(User.class))).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(2L);
                return u;
            });
            // 默认卡牌不存在（不影响注册成功）
            when(cardDao.findByName(anyString())).thenReturn(Optional.empty());

            RegisterRequest request = new RegisterRequest();
            request.setEmail("new@example.com");
            request.setVerificationCode("123456");
            request.setPassword("password123");
            request.setNickname("新用户");

            RegisterResponse response = authService.register(request);

            assertTrue(response.isSuccess());
            assertEquals("新用户#654321", response.getFullId());
            verify(userDao).save(any(User.class));
        }

        @Test
        @DisplayName("邮箱已注册时抛出异常")
        void register_emailExists() {
            setCodeStore("test@example.com", "123456");

            when(userDao.existsByEmail("test@example.com")).thenReturn(true);

            RegisterRequest request = new RegisterRequest();
            request.setEmail("test@example.com");
            request.setVerificationCode("123456");
            request.setPassword("password123");
            request.setNickname("重复用户");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> authService.register(request));
            assertTrue(ex.getMessage().contains("已被注册"));
        }

        @Test
        @DisplayName("验证码错误时抛出异常")
        void register_wrongCode() {
            setCodeStore("test@example.com", "654321");

            RegisterRequest request = new RegisterRequest();
            request.setEmail("test@example.com");
            request.setVerificationCode("000000");
            request.setPassword("password123");
            request.setNickname("用户");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> authService.register(request));
            assertTrue(ex.getMessage().contains("验证码错误"));
        }

        @Test
        @DisplayName("未发送验证码就注册时抛出异常")
        void register_noCodeSent() {
            RegisterRequest request = new RegisterRequest();
            request.setEmail("nosend@example.com");
            request.setVerificationCode("123456");
            request.setPassword("password123");
            request.setNickname("用户");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> authService.register(request));
            assertTrue(ex.getMessage().contains("请先获取验证码"));
        }
    }

    @Nested
    @DisplayName("login 用户登录")
    class Login {

        @Test
        @DisplayName("登录成功：返回 JWT Token 和用户信息")
        void login_success() {
            when(userDao.findByEmail("test@example.com"))
                    .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("password123", "$2a$10$encodedHash"))
                    .thenReturn(true);
            when(jwtUtil.generateToken(1L, "test@example.com"))
                    .thenReturn("mock-jwt-token");

            LoginRequest request = new LoginRequest();
            request.setEmail("test@example.com");
            request.setPassword("password123");

            LoginResponse response = authService.login(request);

            assertEquals("mock-jwt-token", response.getToken());
            assertEquals("测试用户", response.getNickname());
            assertEquals("123456", response.getUniqueTag());
            assertEquals(100, response.getGold());
        }

        @Test
        @DisplayName("邮箱不存在时抛出异常")
        void login_emailNotFound() {
            when(userDao.findByEmail("unknown@example.com"))
                    .thenReturn(Optional.empty());

            LoginRequest request = new LoginRequest();
            request.setEmail("unknown@example.com");
            request.setPassword("password123");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> authService.login(request));
            assertTrue(ex.getMessage().contains("邮箱或密码错误"));
        }

        @Test
        @DisplayName("密码错误时抛出异常")
        void login_wrongPassword() {
            when(userDao.findByEmail("test@example.com"))
                    .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrongpwd", "$2a$10$encodedHash"))
                    .thenReturn(false);

            LoginRequest request = new LoginRequest();
            request.setEmail("test@example.com");
            request.setPassword("wrongpwd");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> authService.login(request));
            assertTrue(ex.getMessage().contains("邮箱或密码错误"));
        }
    }

    /**
     * 通过反射直接设置 codeStore 中的验证码条目
     * 避免依赖 sendCode 的随机验证码
     */
    @SuppressWarnings("unchecked")
    private void setCodeStore(String email, String code) {
        try {
            var field = AuthServiceImpl.class.getDeclaredField("codeStore");
            field.setAccessible(true);
            var store = (java.util.Map<String, Object>) field.get(authService);

            // 构造 CodeEntry record（code, expireTime, createdAt）
            var codeEntryClass = Class.forName("com.cardgame.service.impl.AuthServiceImpl$CodeEntry");
            var constructor = codeEntryClass.getDeclaredConstructor(
                    String.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class);
            constructor.setAccessible(true);

            Object entry = constructor.newInstance(
                    code,
                    java.time.LocalDateTime.now().plusMinutes(10),
                    java.time.LocalDateTime.now().minusSeconds(120) // 2分钟前发送，避开冷却
            );
            store.put(email, entry);
        } catch (Exception e) {
            throw new RuntimeException("设置 codeStore 失败", e);
        }
    }
}
