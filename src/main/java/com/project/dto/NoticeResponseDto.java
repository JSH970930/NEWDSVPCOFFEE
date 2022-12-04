package com.project.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.entity.Notice;

import lombok.Getter;

@Getter

public class NoticeResponseDto {
	
	private Long id;
	private String title;
	private String content;
	private int readCnt;
	
	private String registerId;
	private LocalDate registerTime;
	
	public NoticeResponseDto(Notice entity) {
		this.id = entity.getId();
		this.title = entity.getTitle();
		this.content = entity.getContent();
		this.readCnt = entity.getReadCnt();
		
		this.registerId = entity.getRegisterId();
		this.registerTime = entity.getRegisterTime();
		
	}
	
	@Override
	public String toString() {
		return "NoticeListDto [id=" + id + ", title=" + title + ", content=" +
				content + ", readCnt=" + readCnt + ", registerId =" + registerId +
				", registerTime=" + registerTime + "]";
	}

}
