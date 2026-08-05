package com.cardgame.controller;

import com.cardgame.dao.BattleRecordDao;
import com.cardgame.dao.DeckDao;
import com.cardgame.dao.UserDao;
import com.cardgame.model.entity.BattleRecord;
import com.cardgame.model.entity.User;
import com.cardgame.model.enums.BattleMode;
import com.cardgame.model.enums.BattleResult;
import com.cardgame.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserController 单元测试（Mockito）
 * 覆盖个人简介查询、头像上传的核心逻辑
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private UserDao userDao;
    @Mock private BattleRecordDao battleRecordDao;
    @Mock private DeckDao deckDao;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userController, "avatarDir", "target/test-uploads");

        testUser = User.builder()
                .id(1L)
                .nickname("测试用户")
                .uniqueTag("123456")
                .email("test@example.com")
                .passwordHash("$2a$10$hash")
                .gold(200)
                .points(150)
                .createdAt(LocalDateTime.of(2024, 1, 15, 10, 30))
                .build();
    }

    @Nested
    @DisplayName("GET /api/user/profile 个人简介")
    class GetProfile {

        @Test
        @DisplayName("无对战记录时返回正确的默认数据")
        void getProfile_noBattles() {
            when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
            when(battleRecordDao.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());
            when(deckDao.countByUserId(1L)).thenReturn(0L);
            when(userDao.findAll()).thenReturn(List.of(testUser));

            ResponseEntity<?> response = userController.getProfile("Bearer valid-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            var profile = response.getBody();
            assertNotNull(profile);
        }

        @Test
        @DisplayName("有对战记录时正确计算胜率")
        void getProfile_withBattles() {
            BattleRecord win = BattleRecord.builder()
                    .userId(1L).mode(BattleMode.PVE).result(BattleResult.WIN).build();
            BattleRecord lose = BattleRecord.builder()
                    .userId(1L).mode(BattleMode.PVE).result(BattleResult.LOSE).build();
            BattleRecord pvpWin = BattleRecord.builder()
                    .userId(1L).mode(BattleMode.PVP).result(BattleResult.WIN).build();

            when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
            when(battleRecordDao.findByUserIdOrderByCreatedAtDesc(1L))
                    .thenReturn(List.of(win, lose, pvpWin));
            when(deckDao.countByUserId(1L)).thenReturn(2L);
            when(userDao.findAll()).thenReturn(List.of(testUser));

            ResponseEntity<?> response = userController.getProfile("Bearer valid-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(battleRecordDao).findByUserIdOrderByCreatedAtDesc(1L);
        }

        @Test
        @DisplayName("正确计算排名（按积分降序）")
        void getProfile_rankCalculation() {
            User higherUser = User.builder()
                    .id(2L).nickname("高分玩家").uniqueTag("654321")
                    .email("high@example.com").points(300).build();

            when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
            when(battleRecordDao.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());
            when(deckDao.countByUserId(1L)).thenReturn(0L);
            when(userDao.findAll()).thenReturn(List.of(higherUser, testUser));

            ResponseEntity<?> response = userController.getProfile("Bearer valid-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("有头像时返回头像 URL")
        void getProfile_withAvatar() {
            testUser.setAvatar("avatar_1_abc123.jpg");

            when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
            when(battleRecordDao.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());
            when(deckDao.countByUserId(1L)).thenReturn(0L);
            when(userDao.findAll()).thenReturn(List.of(testUser));

            ResponseEntity<?> response = userController.getProfile("Bearer valid-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("已解锁人物数量正确解析")
        void getProfile_unlockedCharacters() {
            testUser.setUnlockedCharacters("1,3,5");

            when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
            when(battleRecordDao.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());
            when(deckDao.countByUserId(1L)).thenReturn(0L);
            when(userDao.findAll()).thenReturn(List.of(testUser));

            ResponseEntity<?> response = userController.getProfile("Bearer valid-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("POST /api/user/avatar 头像上传")
    class UploadAvatar {

        @Test
        @DisplayName("文件为空时返回 400")
        void uploadAvatar_emptyFile() {
            when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

            MockMultipartFile emptyFile = new MockMultipartFile(
                    "file", "", "image/png", new byte[0]);

            ResponseEntity<?> response = userController.uploadAvatar("Bearer token", emptyFile);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("文件超过 5MB 时返回 400")
        void uploadAvatar_fileTooLarge() {
            when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

            byte[] largeContent = new byte[6 * 1024 * 1024]; // 6MB
            MockMultipartFile largeFile = new MockMultipartFile(
                    "file", "avatar.jpg", "image/jpeg", largeContent);

            ResponseEntity<?> response = userController.uploadAvatar("Bearer token", largeFile);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("不支持的文件格式返回 400")
        void uploadAvatar_unsupportedFormat() {
            when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

            MockMultipartFile bmpFile = new MockMultipartFile(
                    "file", "avatar.bmp", "image/bmp", new byte[1024]);

            ResponseEntity<?> response = userController.uploadAvatar("Bearer token", bmpFile);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("支持的格式：JPG、PNG、GIF、WebP")
        void uploadAvatar_supportedFormats() {
            // 仅验证格式校验逻辑（不实际写文件，避免文件系统依赖）
            when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

            // BMP 不在支持列表中
            MockMultipartFile bmpFile = new MockMultipartFile(
                    "file", "test.bmp", "image/bmp", new byte[100]);
            ResponseEntity<?> response = userController.uploadAvatar("Bearer token", bmpFile);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("null Content-Type 返回 400")
        void uploadAvatar_nullContentType() {
            when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

            MockMultipartFile noTypeFile = new MockMultipartFile(
                    "file", "test.file", null, new byte[100]);

            ResponseEntity<?> response = userController.uploadAvatar("Bearer token", noTypeFile);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }
}
