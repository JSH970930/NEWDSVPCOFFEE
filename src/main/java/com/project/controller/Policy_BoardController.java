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
import org.springframework.web.bind.annotation.RequestParam;
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
public class Policy_BoardController {
	
	private final Policy_BoardService boardService;
	private final FileService fileService;
	private final MemberService memberService;
	

	

	
	   @GetMapping("/board/policy/list")
	   public String getBoardListPage(Model model,@RequestParam(value="searchKeyword", required = false) String searchKeyword
			 , Pageable pageable  
	         , @RequestParam(value="type", required=false) String type
	         , @RequestParam(required = false, defaultValue = "0") Integer page
	         , @RequestParam(required = false, defaultValue = "10") Integer size) throws Exception {
	      
	      
	     pageable = PageRequest.of(page, size, Sort.by("id").descending());
	      
	      if(searchKeyword == null){
	   
	      try {
	         model.addAttribute("resultMap", boardService.findAll(pageable));
	      
	      } catch (Exception e) {
	      throw new Exception(e.getMessage()); 
	      }

	      }else if(type == "title") {
	         model.addAttribute("resultMap", boardService.findByTitleContaining(pageable, searchKeyword ));
	      
	      }else {
	         model.addAttribute("resultMap" , boardService.findByContentContaining(pageable, searchKeyword));
	      } 
	      
	      
	      System.out.println(boardService.findAll(pageable));
	      

	      return "/board/policy/list";
	   }


	
	@GetMapping("/board/policy/write")
	public String getBoardWritePage(Model model, Policy_BoardRequestDto boardRequestDto) {
		return "/board/policy/write";
	}
	


	@GetMapping("/board/policy/view")
	public String getBoardViewPage(Model model, Policy_BoardRequestDto boardRequestDto) throws Exception {
		
		try {
			if(boardRequestDto.getId() != null) {
				model.addAttribute("info", boardService.findById(boardRequestDto.getId()));
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage()); 
		}
		System.out.println(model);
		
		return "board/policy/view";
	}
	
//	@GetMapping("/board/view/{id}")
//	public String getBoardViewPage(@PathVariable("id") Long id, Model model, BoardRequestDto boardRequestDto) throws Exception {
//		
//	    BoardRequestDto boardrequestDto = boardService.getPost(id);
//        model.addAttribute("post", boardrequestDto);
//		
//		return "board/view";
//	}
	
	@PostMapping("/board/policy/write/action")
	public String boardWriteAction(@RequestParam("file") MultipartFile files,Model model, Policy_BoardRequestDto boardRequestDto) 
	{
		
		  try {
	            String origFilename = files.getOriginalFilename();
	            String filename = new MD5Generator(origFilename).toString();
	            /* 실행되는 위치의 'files' 폴더에 파일이 저장됩니다. */
	            String savePath = System.getProperty("user.dir") + "\\files";
	            /* 파일이 저장되는 폴더가 없으면 폴더를 생성합니다. */
	            if (!new File(savePath).exists()) {
	                try{
	                    new File(savePath).mkdir();
	                }
	                catch(Exception e){
	                    e.getStackTrace();
	                }
	            }
	            String filePath = savePath + "\\" + filename;
	            files.transferTo(new File(filePath));

	            FileDto fileDto = new FileDto();
	            fileDto.setOrigFilename(origFilename);
	            fileDto.setFilename(filename);
	            fileDto.setFilePath(filePath);

	            Long fileId = fileService.saveFile(fileDto);
	            boardRequestDto.setFileId(fileId);
	            
	            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    		String username = ((UserDetails) principal).getUsername();
	    		MemberDto memberDto = memberService.MemberRecord(username);
	    		String name = memberDto.getName();
	    		boardRequestDto.setRegisterId(name);
	    		
	            boardService.save(boardRequestDto);
	        } catch(Exception e) {
	            e.printStackTrace();
	        }
		return "redirect:/board/policy/list";
	}
	
	@PostMapping("/board/policy/view/action")
	public String boardViewAction(Model model, Policy_BoardRequestDto boardRequestDto) throws Exception {
		
		try {
			int result = boardService.updateBoard(boardRequestDto);
			
			if (result < 1) {
				throw new Exception("#Exception boardViewAction!");
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage()); 
		}
		
		return "redirect:/board/policy/list";
	}
	
	@PostMapping("/board/policy/view/delete")
	public String boardViewDeleteAction(Model model, @RequestParam() Long id) throws Exception {
		
		try {
			boardService.deleteById(id);
		} catch (Exception e) {
			throw new Exception(e.getMessage()); 
		}
		
		return "redirect:/board/policy/list";
	}
	
	@PostMapping("/board/policy/delete")
	public String boardDeleteAction(Model model, @RequestParam() Long[] deleteId) throws Exception {
		
		try {
			boardService.deleteAll(deleteId);
		} catch (Exception e) {
			throw new Exception(e.getMessage()); 
		}
		
		return "redirect:/board/policy/list";
	}
	
	@GetMapping("/download/{fileId}")
	public ResponseEntity<Resource> fileDownload(@PathVariable("fileId") Long fileId) throws IOException {
	    FileDto fileDto = fileService.getFile(fileId);
	    Path path = Paths.get(fileDto.getFilePath());
	    Resource resource = new InputStreamResource(Files.newInputStream(path));
	    return ResponseEntity.ok()
	            .contentType(MediaType.parseMediaType("application/octet-stream"))
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDto.getOrigFilename() + "\"")
	            .body(resource);
	}
	
	
	
	
}
