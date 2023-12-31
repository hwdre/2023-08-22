package com.hsw.web.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionIdListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.hsw.web.Service.AdminService;
import com.hsw.web.util.Util;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private Util util;
	
	@GetMapping("/")
	public String adminIndex() {
		return "forward:/admin/admin"; //redirect와 비슷하지만 다릅니다.
		//forward는 url은 그대로 유지하고 안의 jsp만 바꿉니다.
		//한마디로 forward로 가면 url에 'http://localhost/admin/'이렇게 나오지만 
		//redirect는 'http://localhost/admin/admin'이렇게 나옵니다.
	}
	
	@GetMapping("/admin")
	public String adminIndex2() {
		return "admin/index";
	}
	
	@PostMapping("/login")
	public String adminLogin(@RequestParam Map<String, Object> map, HttpSession session) {
		//System.out.println(map);
		Map<String, Object> result = adminService.adminLogin(map);
		System.out.println(result);
		if(util.obj2Int(result.get("count")) == 1 && util.obj2Int(result.get("m_grade")) > 5) {
			System.out.println("코딩재밌다.");
			session.setAttribute("mid", map.get("id"));
			session.setAttribute("mname", result.get("m_name"));
			session.setAttribute("mgrade", result.get("m_grade"));
			//System.out.println(session.getAttribute("mid"));
			return "redirect:/admin/main";
		} else {
			return "redirect:/admin/admin?error=error";
		}
	}
	
	@GetMapping("/main")
	public String main() {
		return "admin/main"; //admin폴더 속에있는 main.jsp를 읽어서 화면에 보여줍니다.
	}
	
	@GetMapping("/notice")
	public String notice(Model model) {
		//1. 데이터베이스 불러오기
		//2. 데이터 불러오기
		//3. 데이터 jsp로 보내기
		List<Map<String, Object>> noticelist = adminService.noticelist();
		model.addAttribute("noticelist", noticelist);
		//System.out.println(noticelist);
		return "admin/notice";
	}
	
	@PostMapping("/noticeWrite")
	public String noticeWrite(@RequestParam("upfile") MultipartFile mf, @RequestParam Map<String, Object> map,
			HttpSession session) {
		//System.out.println(map); 
		
		//2023-08-22 요구사항확인
		//jsp에서 upfile로 올려서 RequestParam으로 잡아서 끝냅니다.
		if(mf.getSize() > 0) { //!mf.isEmpty()도 같은 의미입니다.
			//저장할 경로를 뽑겠습니다.
			HttpServletRequest rq0 = 
					((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
			String path = rq0.getServletContext().getRealPath("/upload");
			System.out.println("실제 경로: " + path);
			//mf의 정보 보기
			System.out.println(mf.getOriginalFilename());
			System.out.println(mf.getSize());
			System.out.println(mf.getContentType());
			//진짜 파일 업로드 하기 = 경로 + 저장할 파일 명칭
			//String타입의 경로를 file형태로 바꿔주겠습니다.
			//File filePath = new File(path);
			//중복이 발생할 수 있기 때문에 원래는 파일명 + 날짜 + 파일확장자를 이어붙여서 db에 집어넣습니다.
			//아니면 파일명, 날짜, ID, 파일확장자를 ,같은 걸로 구분해서 집어 넣습니다.
			//UUID + 파일명 + 확장자
			UUID uuid = UUID.randomUUID();
			
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmmss");
			String dateTime = sdf.format(date);
			LocalDateTime ldt = LocalDateTime.now();
			String format = ldt.format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"));
			
			String realFileName = dateTime +","+ uuid.toString() +","+ mf.getOriginalFilename();
//			String realFileName0 = format + uuid.toString() + mf.getOriginalFilename();
			
			File newFileName = new File(path, realFileName);
//			File newFileName = new File(path, realFileName0);
//			/든 \\든 상관없습니다. 다만 \는 반드시 \\로 써야합니다.
			//진짜 파일 업로드합니다.
			try {
				//mf.transferTo(newFileName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("저장했습니다.");
			//FileCopyUtils를 사용하기 위해서는 오리지널 파일을 byte[]로 만들어야 합니다.
			try {
				FileCopyUtils.copy(mf.getBytes(), newFileName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			map.put("upfile", mf.getOriginalFilename());
			map.put("realFile", realFileName);
		}
		map.put("mno", 3);
		adminService.noticeWrite(map);
		return "redirect:/admin/notice";
		/* a가 어제 1.png, b가 오늘 1.png를 올리면 서버에선 중복을 방지하기 위해서 다른 이름을 넣습니다.
		 * 예시) a의 1.png -> 1.png
		 *  	b의 1.png -> 1(1).png
		 *  	c의 1.png -> 1(2).png*/
	}	
	
	@GetMapping("/mail")
	public String mail() {
		return "admin/mail";
	}
}