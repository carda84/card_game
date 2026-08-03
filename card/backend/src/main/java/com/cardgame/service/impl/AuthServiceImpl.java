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
import com.cardgame.model.entity.PlayerCard;
import com.cardgame.model.entity.User;
import com.cardgame.service.AuthService;
import com.cardgame.service.EmailService;
import com.cardgame.util.IdGenerator;
import com.cardgame.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 认证服务实现
 *
 * 验证码存储：使用内存 ConcurrentHashMap（开发阶段）
 * 生产环境建议替换为 Redis + TTL
 *
 * 注册流程：
 *   1. sendCode() 发送验证码到邮箱
 *   2. register() 校验验证码 → 创建用户 → 生成唯一标识 → 返回结果
 *
 * 登录流程：
 *   1. login() 校验邮箱密码 → 签发 JWT → 返回用户信息
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    /** 验证码存储：email -> CodeEntry(code, expireTime, createdAt) */
    private final Map<String, CodeEntry> codeStore = new ConcurrentHashMap<>();

    private final UserDao userDao;
    private final EmailService emailService;
    private final IdGenerator idGenerator;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CardDao cardDao;
    private final PlayerCardDao playerCardDao;

    /** 用户注册时默认获得的 18 张卡牌 */
    private static final List<String> DEFAULT_CARD_NAMES = List.of(
            "白鼬", "牛蛙", "狼", "狼崽", "麋鹿", "小麋鹿",
            "麻雀", "渡鸦", "负鼠", "蝰蛇", "臭鼬", "石龙子",
            "蜂巢", "蟑螂", "响尾蛇", "螳螂", "鼹鼠", "环形虫"
    );

    @Value("${app.verification.code-expiry-minutes:10}")
    private int codeExpiryMinutes;

    @Value("${app.verification.resend-cooldown-seconds:60}")
    private int resendCooldownSeconds;

    public AuthServiceImpl(UserDao userDao,
                           EmailService emailService,
                           IdGenerator idGenerator,
                           JwtUtil jwtUtil,
                           PasswordEncoder passwordEncoder,
                           CardDao cardDao,
                           PlayerCardDao playerCardDao) {
        this.userDao = userDao;
        this.emailService = emailService;
        this.idGenerator = idGenerator;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.cardDao = cardDao;
        this.playerCardDao = playerCardDao;
    }

    // ==================== 发送验证码 ====================

    @Override
    public void sendCode(String email) {
        // 防止频繁发送：若上一次发送距今不到 cooldown 秒，拒绝
        CodeEntry existing = codeStore.get(email);
        if (existing != null && existing.createdAt.plusSeconds(resendCooldownSeconds).isAfter(LocalDateTime.now())) {
            throw new BusinessException("验证码已发送，请稍后再试", HttpStatus.TOO_MANY_REQUESTS);
        }

        String code = generateCode();
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(codeExpiryMinutes);
        codeStore.put(email, new CodeEntry(code, expireTime, LocalDateTime.now()));

        emailService.sendVerificationCode(email, code);
        log.info("验证码已生成并发送，email={}, code={}", email, code);
    }

    // ==================== 注册 ====================

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String email = request.getEmail();

        // 1. 校验验证码
        verifyCode(email, request.getVerificationCode());

        // 2. 校验邮箱未被注册
        if (userDao.existsByEmail(email)) {
            throw new BusinessException("该邮箱已被注册", HttpStatus.CONFLICT);
        }

        // 3. 生成唯一标识（6位数字）
        String uniqueTag = idGenerator.generateUniqueTag();

        // 4. 创建用户
        User user = User.builder()
                .nickname(request.getNickname())
                .uniqueTag(uniqueTag)
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        userDao.save(user);

        // 5. 发放默认卡牌
        grantDefaultCards(user.getId());

        // 6. 清除验证码
        codeStore.remove(email);

        log.info("用户注册成功：{}", user.getFullId());
        return RegisterResponse.ok(user.getFullId());
    }

    // ==================== 登录 ====================

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. 查找用户
        User user = userDao.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("邮箱或密码错误", HttpStatus.UNAUTHORIZED));

        // 2. 校验密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("邮箱或密码错误", HttpStatus.UNAUTHORIZED);
        }

        // 3. 签发 JWT
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        log.info("用户登录成功：{}", user.getFullId());
        return LoginResponse.builder()
                .token(token)
                .nickname(user.getNickname())
                .uniqueTag(user.getUniqueTag())
                .gold(user.getGold())
                .points(user.getPoints())
                .build();
    }

    // ==================== 内部方法 ====================

    /** 校验验证码是否正确且未过期 */
    private void verifyCode(String email, String code) {
        CodeEntry entry = codeStore.get(email);
        if (entry == null) {
            throw new BusinessException("请先获取验证码");
        }
        if (entry.expireTime.isBefore(LocalDateTime.now())) {
            codeStore.remove(email);
            throw new BusinessException("验证码已过期，请重新获取");
        }
        if (!entry.code.equals(code)) {
            throw new BusinessException("验证码错误");
        }
    }

    /** 生成 6 位数字验证码 */
    private String generateCode() {
        int code = ThreadLocalRandom.current().nextInt(1_000_000);
        return String.format("%06d", code);
    }

    /** 验证码存储条目 */
    private record CodeEntry(String code, LocalDateTime expireTime, LocalDateTime createdAt) {}

    /**
     * 为新注册用户发放 18 张默认卡牌
     */
    private void grantDefaultCards(Long userId) {
        for (String cardName : DEFAULT_CARD_NAMES) {
            cardDao.findByName(cardName).ifPresent(card -> {
                PlayerCard pc = PlayerCard.builder()
                        .userId(userId)
                        .cardId(card.getId())
                        .quantity(1)
                        .build();
                playerCardDao.save(pc);
            });
        }
        log.info("用户 {} 已获得 {} 张默认卡牌", userId, DEFAULT_CARD_NAMES.size());
    }
}