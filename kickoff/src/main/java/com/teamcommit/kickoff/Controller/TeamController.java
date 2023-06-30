package com.teamcommit.kickoff.Controller;

import com.teamcommit.kickoff.Do.TeamApplyDO;
import com.teamcommit.kickoff.Do.TeamDO;
import com.teamcommit.kickoff.Do.TeamInfoDO;
import com.teamcommit.kickoff.Do.UserDO;
import com.teamcommit.kickoff.Service.team.TeamService;
import com.teamcommit.kickoff.Service.login.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

@Controller
public class TeamController {

    @Autowired
    @Qualifier("TeamService")
    private TeamService teamService;
    
    @Autowired
    private LoginService loginService;


    // 팀 목록 & 게시글 목록
    @RequestMapping(value = "/team")
    public String TeamList(@ModelAttribute("teamInfoDO") TeamInfoDO teamInfoDO, @ModelAttribute("teamDO") TeamDO teamDO, Model model) throws Exception {
        String view = "/team/team";
        
        List<TeamInfoDO> teamList = teamService.teamInfoList(teamInfoDO);
        model.addAttribute("teamList", teamList);
        
        List<TeamDO> teamBoard = teamService.teamBoardList(teamDO);
        model.addAttribute("teamBoard", teamBoard);
        
        List<TeamDO> teamRecruit = teamService.teamRecruitList(teamDO);
        model.addAttribute("teamRecruit", teamRecruit);

        return view;
    }

    // 팀 게시글 상세보기
    @RequestMapping( "/teamDetail")
    public String teamDetail(@ModelAttribute("teamDO") TeamDO teamDO, @RequestParam("teamSeqNo") int teamSeqNo, HttpServletRequest request, Model model) throws Exception {
        String view = "/team/teamDetail";

        TeamDO teamContents = teamService.getTeamContents(teamSeqNo);
        model.addAttribute("teamContents", teamContents);

        return view;
    }

    // 팀 모집 글 등록 페이지 이동
    @GetMapping("/teamInsert")
    public String teamInsert(HttpSession session, Model model, HttpServletRequest request) {
        String view = "";

        if(session.getAttribute("userId") == null) {
            model.addAttribute("script", "alert('로그인 후 이용하실 수 있습니다.');");
            view = "login/loginAll";
        }
        else if (session.getAttribute("userId") != null) {
            view = "team/teamInsert";
        }
        return view;
    }

    // 팀 등록 요청
    @RequestMapping("/teamInsertAction")
    public ModelAndView teamInsertAction(@ModelAttribute("teamDO") TeamDO teamDO, Model model, HttpSession session) throws Exception {
        ModelAndView mv = new ModelAndView();

        try {
            teamService.insertTeam(teamDO);
            mv = new ModelAndView("redirect:/team");

            model.addAttribute("script", "alert('팀 등록을 완료하였습니다.');");

        } catch (Exception e) {
            model.addAttribute("script", "alert('잘못된 요청입니다. 다시 시도해 주세요.');");
        }

        return mv;
    }

    // 팀 랭킹
    @RequestMapping(value = "/teamRank", method = RequestMethod.GET)
    public String TeamRank(@ModelAttribute("teamDO") TeamDO teamDO, HttpServletRequest request, Model model) throws Exception {

        String view = "/team/teamRank";

        List<TeamDO> teamRankList = teamService.rankList(teamDO);
        model.addAttribute("teamRankList", teamRankList);

        return view;
    }

    // 팀 지원 페이지 이동
    @GetMapping("/teamApply")
    public String teamInsert(HttpSession session, Model model) {
        String view = "";

        if(session.getAttribute("userId") == null) {
            model.addAttribute("script", "alert('로그인 후 이용하실 수 있습니다.');");
            view = "login/loginAll";
        }
        else if (session.getAttribute("userId") != null) {
            view = "team/teamApply";
        }
        return view;
    }
    
    //팀 지원 DB 저장
    @RequestMapping("/apply_action")
    public ModelAndView apply_action(@ModelAttribute("teamApplyDO") TeamApplyDO teamApplyDO, ModelMap model, HttpServletRequest request, RedirectAttributes redirect) throws Exception {
    	
    	ModelAndView mv = new ModelAndView();
    	
    	try {
    		
    		//로그인한 이용자 ID로 로그인 정보 가져오기
            String userId = (String) request.getSession().getAttribute("userId");
            UserDO userDO =new UserDO();
            userDO.setUserId(userId);
            userDO = loginService.procSetUserInfo(userDO);
            
            String userName = (String) request.getSession().getAttribute("userName");
            String userGender = (String) request.getSession().getAttribute("userGender");
            String userPhoneNumber = (String) request.getSession().getAttribute("userPhoneNumber");
    		
			teamService.insertTeamApply(teamApplyDO);
			
			mv = new ModelAndView("redirect:/team");
			redirect.addFlashAttribute("msg", "팀 지원 완료되었습니다.");
			
		} catch (Exception e) {
			redirect.addFlashAttribute("msg", "오류가 발생되었습니다. 다시 시도해주세요.");
		}
    	
    	return mv;
    }
    
    // 팀 생성 페이지 이동
    @RequestMapping(value = "/teamCreateForm")
    public String teamCreateForm(HttpSession session, Model model, HttpServletRequest request) {
        String view = "";

        if(session.getAttribute("userId") == null) {
            model.addAttribute("script", "alert('로그인 후 이용하실 수 있습니다.');");
            view = "login/loginAll";
        }
        else if (session.getAttribute("userId") != null) {
            view = "team/teamCreate";
        }
        return view;
    }
    
    // 팀 생성
    @RequestMapping(value = "/teamCreate")
    public String teamCreate() throws Exception {
        String view = "";

        return view;
    }
    
    
    // 팀 상세정보 & 팀원 리스트
    @RequestMapping( "/teamManage")
    public String teamMembersList(HttpServletRequest request, Model model, HttpSession session) throws Exception {
    	String view = "";
    	//로그인한 이용자 ID로 로그인 정보 가져오기
    	String userId = (String)session.getAttribute("userId");
    	 
		if (userId == null) {
			model.addAttribute("script", "alert('로그인 후 이용하실 수 있습니다.');");
			view = "login/loginAll";
			
			return view;
		} else if (userId != null) {
			view = "team/teamManage";
		}
		
		List<TeamInfoDO> teamDetail = teamService.teamInfoDetail(userId);
		model.addAttribute("teamDetail", teamDetail);
    	
        TeamInfoDO teamInfoDO = teamService.teamInfo(userId);
        
        int teamId = teamInfoDO.getTeamId();
        
    	List<Map<String, String>> memberList = teamService.teamMemberList(teamId);
        model.addAttribute("memberList", memberList);
        
        return view;
    }
    
    // 팀원 방출
//    @RequestMapping("/memberDelete")
//    public String memberDelete() throws Exception {
//    	String view = "redirect:/teamManage";
//    	
//    	if()
//    }
}
