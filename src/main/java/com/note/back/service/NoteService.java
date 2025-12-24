package com.note.back.service;

import com.note.back.dao.CategoryDao;
import com.note.back.dao.NoteDao;
import com.note.back.pojo.Category;
import com.note.back.pojo.Note;
import com.note.back.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NoteService {
    @Autowired
    NoteDao noteDao;
    @Autowired
    CategoryDao categoryDao;

    public List<Note> getAll(){
        return noteDao.findAll(Sort.by(Sort.Direction.DESC,"id"));
    }

    public List<Note> getNotesByCategory(int id){
        Category category = categoryDao.getOne(id);
        return noteDao.findAllByCategory(category);
    }

    public Note getById(int id){
        return noteDao.findById(id).get();
    }

    public void updateNote(Note note){
        noteDao.save(note);
    }

    public void deleteById(int id) {
        noteDao.deleteById(id);
    }
    
    public Map<String, Object> searchNotesByKeyword(User user, String keyword, int page, int size, String sortBy, String sortOrder) {
        // 验证参数
        if (user == null) {
            throw new IllegalArgumentException("用户不能为空");
        }
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        
        // 设置默认值
        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 12;
        if (sortBy == null || sortBy.isEmpty()) sortBy = "createdTime";
        if (sortOrder == null || sortOrder.isEmpty()) sortOrder = "desc";
        
        // 转换排序方向
        Sort.Direction direction = Sort.Direction.DESC;
        if ("asc".equalsIgnoreCase(sortOrder)) {
            direction = Sort.Direction.ASC;
        }
        
        // 创建分页请求
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
        
        // 执行搜索
        Page<Note> notePage = noteDao.searchNotesByKeyword(user, keyword, pageable);
        
        // 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("notes", notePage.getContent());
        result.put("total", notePage.getTotalElements());
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", notePage.getTotalPages());
        
        return result;
    }
    
    public long countSearchResultsByKeyword(User user, String keyword) {
        // 验证参数
        if (user == null) {
            throw new IllegalArgumentException("用户不能为空");
        }
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        
        return noteDao.countSearchResultsByKeyword(user, keyword);
    }
}
