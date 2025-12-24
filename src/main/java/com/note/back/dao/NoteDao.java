package com.note.back.dao;

import com.note.back.pojo.Category;
import com.note.back.pojo.Note;
import com.note.back.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoteDao extends JpaRepository<Note,Integer> {
    List<Note> findAllByCategory(Category category);
    
    @Query("SELECT n FROM Note n WHERE n.author = ?1 AND (n.name LIKE %?2% OR n.abs LIKE %?2% OR n.contentMd LIKE %?2%)")
    Page<Note> searchNotesByKeyword(User user, String keyword, Pageable pageable);
    
    @Query("SELECT COUNT(n) FROM Note n WHERE n.author = ?1 AND (n.name LIKE %?2% OR n.abs LIKE %?2% OR n.contentMd LIKE %?2%)")
    long countSearchResultsByKeyword(User user, String keyword);
}
