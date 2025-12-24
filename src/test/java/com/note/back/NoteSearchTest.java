package com.note.back;

import com.note.back.pojo.Note;
import com.note.back.pojo.User;
import com.note.back.service.NoteService;
import com.note.back.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NoteSearchTest {

    @Autowired
    private NoteService noteService;
    
    @Autowired
    private UserService userService;
    
    private User testUser;
    private Note testNote1;
    private Note testNote2;
    
    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setEmail("test@example.com");
        testUser.setRegisterTime(new Timestamp(System.currentTimeMillis()));
        userService.addUser(testUser);
        
        // 创建测试笔记1
        testNote1 = new Note();
        testNote1.setName("测试笔记1");
        testNote1.setAbs("这是测试笔记1的摘要");
        testNote1.setContentMd("# 测试笔记1\n这是测试笔记1的内容");
        testNote1.setContentHtml("<h1>测试笔记1</h1><p>这是测试笔记1的内容</p>");
        testNote1.setAuthor(testUser);
        testNote1.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        testNote1.setLastModifiedTime(new Timestamp(System.currentTimeMillis()));
        noteService.updateNote(testNote1);
        
        // 创建测试笔记2
        testNote2 = new Note();
        testNote2.setName("测试笔记2");
        testNote2.setAbs("这是测试笔记2的摘要");
        testNote2.setContentMd("# 测试笔记2\n这是测试笔记2的内容");
        testNote2.setContentHtml("<h1>测试笔记2</h1><p>这是测试笔记2的内容</p>");
        testNote2.setAuthor(testUser);
        testNote2.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        testNote2.setLastModifiedTime(new Timestamp(System.currentTimeMillis()));
        noteService.updateNote(testNote2);
    }
    
    @Test
    void testSearchNotesByKeyword() {
        // 测试按标题搜索
        Map<String, Object> result1 = noteService.searchNotesByKeyword(testUser, "测试笔记1", 1, 12, "createdTime", "desc");
        assertNotNull(result1);
        assertEquals(1, result1.get("total"));
        
        // 测试按摘要搜索
        Map<String, Object> result2 = noteService.searchNotesByKeyword(testUser, "摘要", 1, 12, "createdTime", "desc");
        assertNotNull(result2);
        assertEquals(2, result2.get("total"));
        
        // 测试按内容搜索
        Map<String, Object> result3 = noteService.searchNotesByKeyword(testUser, "内容", 1, 12, "createdTime", "desc");
        assertNotNull(result3);
        assertEquals(2, result3.get("total"));
        
        // 测试不存在的关键词
        Map<String, Object> result4 = noteService.searchNotesByKeyword(testUser, "不存在的关键词", 1, 12, "createdTime", "desc");
        assertNotNull(result4);
        assertEquals(0, result4.get("total"));
        
        // 测试分页功能
        Map<String, Object> result5 = noteService.searchNotesByKeyword(testUser, "测试", 1, 1, "createdTime", "desc");
        assertNotNull(result5);
        assertEquals(2, result5.get("total"));
        assertEquals(1, result5.get("size"));
        assertEquals(1, result5.get("page"));
        assertEquals(2, result5.get("totalPages"));
    }
    
    @Test
    void testSearchNotesByKeywordWithInvalidParams() {
        // 测试空关键词
        assertThrows(IllegalArgumentException.class, () -> {
            noteService.searchNotesByKeyword(testUser, "", 1, 12, "createdTime", "desc");
        });
        
        // 测试空用户
        assertThrows(IllegalArgumentException.class, () -> {
            noteService.searchNotesByKeyword(null, "测试", 1, 12, "createdTime", "desc");
        });
        
        // 测试无效页码
        assertThrows(IllegalArgumentException.class, () -> {
            noteService.searchNotesByKeyword(testUser, "测试", 0, 12, "createdTime", "desc");
        });
        
        // 测试无效每页数量
        assertThrows(IllegalArgumentException.class, () -> {
            noteService.searchNotesByKeyword(testUser, "测试", 1, 0, "createdTime", "desc");
        });
    }
}