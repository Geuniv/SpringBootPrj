package kopo.poly.controller;

import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserInfoController {

    private final IUserInfoService userInfoService;

    /* *
     * 회원가입으로 이동
     * */
    @GetMapping(value = "/user/userRegForm")
    public String userRegForm() {
        log.info(this.getClass().getName() + ".user/userRegForm");
        return "/user/userRegForm" ;
    }

    /* *
     * 회원가입 로직 처리
     * */
    @PostMapping(value = "/user/insertUserInfo")
    public String insertUserInfo(HttpServletRequest request, ModelMap modelMap) throws Exception {

        log.info(this.getClass().getName() + ".insertUserInfo start !");
        int res;
        String msq = " "; // 회원가입 결과에 대한 메시지를 전달할 변수
        String url = " "; // 회원가입 결과에 대한 URL 을 전달할 변수

        UserInfoDTO pDTO = null;

        try {

            /*
            * ############################################
            *           웹 ( 회원정보 입력화면 ) 에서 받는 정보를 String 변수에 저장 시작 !
            *
            *       무조건 웹으로 받는 정보는 DTO에 저장하기 위해 임시로 String 변수에 저장함
            * ############################################
            * */
            String user_id = CmmUtil.nvl(request.getParameter("user_id"));
            String user_name = CmmUtil.nvl(request.getParameter("user_name"));
            String password = CmmUtil.nvl(request.getParameter("password"));
            String email = CmmUtil.nvl(request.getParameter("email"));
            String addr1 = CmmUtil.nvl(request.getParameter("addr1"));
            String addr2 = CmmUtil.nvl(request.getParameter("addr2"));

            /*
             * ############################################
             *  반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함
             *                                  반드시 작성 할 것
             * ############################################
             * */
            log.info("user_id  : " + user_id);
            log.info("user_name  : " + user_name);
            log.info("password  : " + password);
            log.info("email : " + email);
            log.info("addr1 : " + addr1);
            log.info("addr2  : " + addr2);

            /*
             * ############################################
             *           웹 ( 회원정보 입력화면 ) 에서 받는 정보를 DTO에 저장하기 시작 !
             *
             *       무조건 웹으로 받는 정보는 DTO에 저장해야 한다고 이해하길 권장
             * ############################################
             * */

            // 웹 ( 회원정보 입력화면 ) 에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO();

            pDTO.setUser_id(user_id);
            pDTO.setUser_name(user_name);

            // 비밀번호는 절대로 복호화되지 않도록 해서 알고리즘으로 암호화함
            pDTO.setPassword(EncryptUtil.encHashSHA256(password));

            // 민감 정보인 이메일은 AES128 - CBC로 암호화함
            pDTO.setEmail(EncryptUtil.encAES128CBC(email));
            pDTO.setAddr1(addr1);
            pDTO.setAddr2(addr2);

            /*
             * ############################################
             *           웹 ( 회원정보 입력화면 ) 에서 받는 정보를 DTO에 저장하기 끝 !
             *
             *       무조건 웹으로 받은 정보는 DTO에 저장해야 한다고 이해하길 권장
             * ############################################
             * */

            /*
            * 회원가입
            * */
            res = userInfoService.insertUserInfo(pDTO);

            log.info("회원가입 결과(res) : " + res);

            if (res == 1) {
                msq = "회원가입되었습니다.";

                // 추후 회원가입 입력화면에서 ajax를 활용해서 아이디 중복, 이메일 중복을 체크하길 바람
            } else if (res == 2) {
                msq = "이미 가입된 아이디입니다.";
            } else {
                msq = "오류로 인해 회원가입이 실패하였습니다.";
            }
        }  catch (Exception e) {
            // 저장이 실패되면 사용자에게 보여줄 메시지
            msq = "실패하였습니다. : " + e;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            modelMap.addAttribute("msq", msq);
            modelMap.addAttribute("url", url);

            log.info(this.getClass().getName() + ".insertUserInfo End !");
        }

        return "/redirect";
    }
}
