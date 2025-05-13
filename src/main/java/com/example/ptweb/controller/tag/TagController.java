package com.example.ptweb.controller.tag;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.ptweb.controller.dto.response.TagResponseDTO;
import com.example.ptweb.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tag")
@Slf4j
public class TagController {
    @Autowired
    private TagService tagService;
    @GetMapping("/list")
    @SaCheckLogin
    public List<TagResponseDTO> listCategory(){
        return tagService.getAllTags().stream().map(TagResponseDTO::new).toList();
    }
}
