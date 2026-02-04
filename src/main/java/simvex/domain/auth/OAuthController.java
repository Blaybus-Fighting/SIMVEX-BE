package simvex.domain.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthController {

    @GetMapping("/oauth/success")
    public String successAPI() {

        return "success";
    }

    @GetMapping("/oauth/fail")
    public String failAPI() {

        return "fail";
    }
}