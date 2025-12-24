package com.note.back.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.note.back.Response.Response;
import com.note.back.pojo.Category;
import com.note.back.pojo.Note;
import com.note.back.pojo.User;
import com.note.back.service.CategoryService;
import com.note.back.service.NoteService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class NoteController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    NoteService noteService;

    @CrossOrigin
    @GetMapping("/api/categories")
    @ResponseBody
    public Response getCategoryList(){        
        Subject subject = SecurityUtils.getSubject();        
        List<Category> categories = categoryService.getAllByUser((User)subject.getPrincipal());
        return new Response(200,"成功",categories);
    }

    @CrossOrigin
    @GetMapping("/api/category/delete/{id}")
    @ResponseBody
    public Response deleteCategory(@PathVariable("id") int id){
        categoryService.deleteById(id);
        return new Response(200,"成功",null);
    }

    @CrossOrigin
    @PostMapping("/api/category/add")
    @ResponseBody
    public Response addCategory(@RequestBody Category requestCategory){
        Subject subject = SecurityUtils.getSubject();
        Category category = new Category();
        category.setName(requestCategory.getName());
        category.setAuthor((User)subject.getPrincipal());
        categoryService.updateCategory(category);
        return new Response(200,"成功",null);
    }

    @CrossOrigin
    @PostMapping("/api/category/update")
    @ResponseBody
    public Response updateCategory(@RequestBody Category requestCategory){
        Category category = categoryService.getById(requestCategory.getId());
        category.setName(requestCategory.getName());
        categoryService.updateCategory(category);
        return new Response(200,"成功",null);
    }


    @CrossOrigin
    @GetMapping("/api/notes")
    @ResponseBody
    public Response getNoteList(){        
        List<Note> notes = noteService.getAll();
        return new Response(200,"成功",notes);
    }


    @CrossOrigin
    @GetMapping("/api/note/{id}")
    @ResponseBody
    public Response getNote(@PathVariable("id") int id){
        Note note = noteService.getById(id);
        return new Response(200,"成功",note);
    }


    @CrossOrigin
    @GetMapping("/api/note/delete/{id}")
    @ResponseBody
    public Response deleteNote(@PathVariable("id") int id){
        noteService.deleteById(id);
        return new Response(200,"成功",null);
    }

    @CrossOrigin
    @GetMapping("/api/categories/{id}/notes")
    @ResponseBody
    public Response getNotesByCategory(@PathVariable("id") int id){        
        List<Note> notes = noteService.getNotesByCategory(id);
        return new Response(200,"成功",notes);
    }
    
    @CrossOrigin
    @GetMapping("/api/notes/search")
    @ResponseBody
    public Response searchNotes(
            @RequestParam(value = "keyword", required = true) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdTime") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder,
            HttpServletRequest request) {
        
        try {
            // 获取当前登录用户
            Subject subject = SecurityUtils.getSubject();
            User currentUser = (User) subject.getPrincipal();
            
            // 调用搜索服务
            Map<String, Object> searchResult = noteService.searchNotesByKeyword(currentUser, keyword, page, size, sortBy, sortOrder);
            
            // 构建响应
            return new Response(200, "搜索成功", searchResult, request.getRequestURI());
        } catch (IllegalArgumentException e) {
            Response.ErrorInfo errorInfo = new Response.ErrorInfo("BAD_REQUEST", e.getMessage(), "VALIDATION_ERROR");
            return new Response(400, e.getMessage(), null, errorInfo, request.getRequestURI());
        } catch (Exception e) {
            e.printStackTrace();
            Response.ErrorInfo errorInfo = new Response.ErrorInfo("INTERNAL_SERVER_ERROR", e.getMessage(), "SYSTEM_ERROR");
            return new Response(500, "搜索失败，请稍后重试", null, errorInfo, request.getRequestURI());
        }
    }

    @CrossOrigin
    @PostMapping("/api/update/{type}/note/{id}")
    @ResponseBody
    public Response updateNoteInfo(@RequestBody Note requestNote, @PathVariable("id") int id,@PathVariable("type") String type){
        Note note = noteService.getById(id);
        if(type.equals("info")){
            note.setName(requestNote.getName());
            note.setAbs(requestNote.getAbs());
        }
        else if(type.equals("content")){
            note.setContentHtml(requestNote.getContentHtml());
            note.setContentMd(requestNote.getContentMd());
        }
        note.setLastModifiedTime(new Timestamp(System.currentTimeMillis()));
        noteService.updateNote(note);
        return new Response(200,"成功",null);
    }

    @CrossOrigin
    @PostMapping("/api/update/category/{id}/note/add")
    @ResponseBody
    public Response addNote(@RequestBody Note requestNote,@PathVariable("id") int id){
        Subject subject = SecurityUtils.getSubject();
        Note note = new Note();
        note.setName(requestNote.getName());
        note.setAbs(requestNote.getAbs());
        Category category = categoryService.getById(id);
        note.setCategory(category);
        note.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        note.setLastModifiedTime(new Timestamp(System.currentTimeMillis()));
        note.setAuthor((User)subject.getPrincipal());
        noteService.updateNote(note);
        return new Response(200,"成功",null);
    }


    final static String PIC_PATH = "static/pics/";

    @CrossOrigin
    @PostMapping("/api/pic")
    @ResponseBody
    public Response uploadPic(MultipartHttpServletRequest multiRequest, HttpServletRequest request){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String datePrefix = dateFormat.format(new Date());
        String savePath = "src/main/resources/"+PIC_PATH;

        File folder = new File(savePath+datePrefix);
        if(!folder.isDirectory()){
            folder.mkdirs();
        }
        String originalName = multiRequest.getFile("image").getOriginalFilename();
        String saveName = UUID.randomUUID().toString() + originalName.substring(originalName.lastIndexOf("."),originalName.length());
        String absolutePath = folder.getAbsolutePath();

        try{
            File fileToSave = new File(absolutePath + File.separator + saveName);
            multiRequest.getFile("image").transferTo(fileToSave);
            String returnPath = request.getScheme() + "://"
                    + request.getServerName()+":"+request.getServerPort()
                    +"/article/images/"
                    + datePrefix +"/"+ saveName;

            return new Response(200,"上传成功",returnPath);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Response(500,"上传失败",null);

    }




}
