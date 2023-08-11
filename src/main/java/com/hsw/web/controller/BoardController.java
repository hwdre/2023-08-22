package com.hsw.web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hsw.web.dto.BoardDTO;
import com.hsw.web.Service.BoardService;

@Controller
public class BoardController {
	@Autowired
	private BoardService boardService;
	
	@GetMapping("/board")
	public String board(Model model) {
		List<BoardDTO> list = boardService.boardList();
		//System.out.println(list);
		model.addAttribute("list", list);		
		return "board";
	}
	
	@ResponseBody
	@PostMapping("/detail")
	public String detail(@RequestParam("bno") int bno ) {
		//System.out.println(bno);
		BoardDTO dto = boardService.detail(bno);
		
		JSONObject json = new JSONObject();
		JSONObject e = new JSONObject();
		
		e.put("content", dto.getBcontent());
		e.put("uuid", dto.getUuid());
		
		json.put("result", e);
		
		
		System.out.println(json.toString());
		
		return json.toString();
	}

	@GetMapping("/write")
	public String write() {
		return "write";
	}
	
	@PostMapping("/write")
	public String write(HttpServletRequest request) {
		System.out.println(request.getParameter("title"));
		System.out.println(request.getParameter("content"));
		BoardDTO dto = new BoardDTO();
		dto.setBtitle(request.getParameter("title"));
		dto.setBcontent(request.getParameter("content"));
		dto.setM_id("superman"); //임시로 멤버스에 있는 아이디를 넣었습니다.
		dto.setBip("0.0.0.0");
		int result = boardService.write(dto);
		System.out.println(result);
		
		return "redirect:/board";
	}
	
	@PostMapping("/delete")
	public String delete(BoardDTO dto) {
		boardService.delete(dto);
		return "redirect:/board";
	}
}