package com.project.controller;



import org.hibernate.Criteria;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.dto.Policy_BoardRequestDto;

import com.project.dto.FileDto;
import com.project.dto.MemberDto;
import com.project.entity.Policy_Board;

import com.project.service.FileService;
import com.project.service.MemberService;
import com.project.service.Policy_BoardService;
import com.project.util.MD5Generator;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DsvpController {
	
	private final Policy_BoardService boardService;

	
	@GetMapping("/DSVP/*")
	public String getFreeBoardListPage(Model model, @RequestParam(required = false, defaultValue = "0")
										Integer page, @RequestParam(required = false, defaultValue = "5")
										Integer size, Pageable pageable, @RequestParam(required = false, value="check1")
	String check1, @RequestParam(required = false, value="check2")
	String check2 ) throws Exception{
		
		pageable = PageRequest.of(page, size, Sort.by("id").descending());
		String check = check1+check2;
		
		
		
		try {
			if("DP".equals(check)) {
				model.addAttribute("resultMap", boardService.findByContentContaining(pageable, check));
			}else if("DS".equals(check)) {
				model.addAttribute("resultMap", boardService.findByContentContaining(pageable, check));
			}else if("DV".equals(check)) {
				model.addAttribute("resultMap", boardService.findByContentContaining(pageable, check));
			}
			
		} catch(Exception e) {
			throw new Exception(e.getMessage());
		}
		
		return "DSVP/DP";
	}



	
	
	
}
