package com.project.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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


import com.project.dto.FileDto;
import com.project.dto.ImageDto;
import com.project.dto.MemberDto;
import com.project.dto.PubBook_BoardRequestDto;
import com.project.entity.Image;
import com.project.service.FileService;
import com.project.service.ImageService;
import com.project.service.MemberService;
import com.project.service.PubBook_BoardService;
import com.project.util.MD5Generator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PubBook_BoardController {
	
	
	private final Logger LOGGER = LoggerFactory.getLogger(MainController.class.getName());
	
	private final PubBook_BoardService boardService;
	private final FileService fileService;
	private final ImageService imageService;
	private final MemberService memberService;
	

	
	@GetMapping("/board/publishment/pubbook_List")
	public String getBoardListPage(Model model,@RequestParam(value="searchKeyword", required = false) String searchKeyword
			, @RequestParam(required = false, defaultValue = "0") Integer page
			, @RequestParam(required = false, defaultValue = "5") Integer size) throws Exception {
		
		System.out.println(searchKeyword);
		
		if(searchKeyword == null){
	
		try {
			model.addAttribute("resultMap", boardService.findAll(page, size));
		
		} catch (Exception e) {
		throw new Exception(e.getMessage()); 
		}

		}else {
			model.addAttribute("resultMap", boardService.findByTitleContaining(page, size, searchKeyword ));
		
		}
		System.out.println(boardService.findAll(page, size));
		return "/board/publishment/pubbook_List";
	}


	
	@GetMapping("/board/publishment/pubbook_Write")
	public String getBoardWritePage(Model model, PubBook_BoardRequestDto boardRequestDto) {
		return "/board/publishment/pubbook_Write";
	}
	


	@GetMapping("/board/publishment/pubbook_View")
	public String getBoardViewPage(Model model, PubBook_BoardRequestDto boardRequestDto) throws Exception {
		
		try {
			if(boardRequestDto.getId() != null) {
				model.addAttribute("info", boardService.findById(boardRequestDto.getId()));
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage()); 
		}
		System.out.println(model);
		
		return "/board/publishment/pubbook_View";
	}
	
//	@GetMapping("/board/view/{id}")
//	public String getBoardViewPage(@PathVariable("id") Long id, Model model, BoardRequestDto boardRequestDto) throws Exception {
//		
//	    BoardRequestDto boardrequestDto = boardService.getPost(id);
//        model.addAttribute("post", boardrequestDto);
//		
//		return "board/view";
//	}
	
	@PostMapping("/board/publishment/pubbook_Write/action")
	public String boardWriteAction(@RequestParam("file") MultipartFile files, @RequestParam("pubbookimage") MultipartFile images, Model model, 
	PubBook_BoardRequestDto boardRequestDto)
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
	            
	            
	        } catch(Exception e) {
	            e.printStackTrace();
	        }
		  
		  try {
	            String origImageName = images.getOriginalFilename();
	            int dot = origImageName.indexOf(".");
	            String ext = origImageName.substring(dot);
	            String imageName = new MD5Generator(origImageName).toString() + ext;
	            
	            /* 실행되는 위치의 'images' 폴더에 파일이 저장됩니다. */
	            String savePath2 = System.getProperty("user.dir") +	 "\\src\\main\\resources\\static\\images";
	            /* 파일이 저장되는 폴더가 없으면 폴더를 생성합니다. */
	            if (!new File(savePath2).exists()) {
	                try{
	                    new File(savePath2).mkdir();
	                }
	                catch(Exception e){
	                    e.getStackTrace();
	                }
	            }
	            String imagePath = savePath2 + "\\" + imageName;
	            images.transferTo(new File(imagePath));

	            ImageDto imageDto = new ImageDto();
	            imageDto.setOrigImageName(origImageName);
	            imageDto.setImageName(imageName);
	            imageDto.setImagePath(imagePath);
	            Image image = imageDto.toEntity();
	            Long id = imageService.saveFile(image);
	            LOGGER.info("이미지 저장 완료");
	            
	            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    		String username = ((UserDetails) principal).getUsername();
	    		MemberDto memberDto = memberService.MemberRecord(username);
	    		String name = memberDto.getName();
	    		boardRequestDto.setRegisterId(name);
	    		
	            boardService.save(boardRequestDto, id);
	            LOGGER.info("게시글 저장 완료");
	            
	            
      } catch(Exception e){
      	e.printStackTrace();
      }
		  
		  
		  
		return "redirect:/board/publishment/pubbook_List";
	}
	
	@PostMapping("/board/publishment/pubbook_View/action")
	public String boardViewAction(Model model, PubBook_BoardRequestDto boardRequestDto) throws Exception {
		
		try {
			int result = boardService.updateBoard(boardRequestDto);
			
			if (result < 1) {
				throw new Exception("#Exception boardViewAction!");
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage()); 
		}
		
		return "redirect:/board/publishment/pubbook_List";
	}
	
	@PostMapping("/board/publishment/pubbook_View/delete")
	public String boardViewDeleteAction(Model model, @RequestParam() Long id) throws Exception {
		
		try {
			boardService.deleteById(id);
		} catch (Exception e) {
			throw new Exception(e.getMessage()); 
		}
		
		return "redirect:/board/publishment/pubbook_List";
	}
	
	@PostMapping("/board/publishment/pubbook/delete")
	public String boardDeleteAction(Model model, @RequestParam() Long[] deleteId) throws Exception {
		
		try {
			boardService.deleteAll(deleteId);
		} catch (Exception e) {
			throw new Exception(e.getMessage()); 
		}
		
		return "redirect:/board/publishment/pubbook_List";
	}
	
	@GetMapping("/pubbook_download/{fileId}")
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
