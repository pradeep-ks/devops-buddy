package in.devopsbuddy.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import in.devopsbuddy.enums.PlanEnum;
import in.devopsbuddy.web.domain.dto.ProAccount;

@Controller
public class SignUpController {
    
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SignUpController.class);

    public static final String URL_SIGNUP = "/signup";

    public static final String ATTRIBUTE_NAME_PAYLOAD = "payload";

    public static final String VIEW_NAME_SIGNUP = "register/signup";

    @RequestMapping(value = URL_SIGNUP, method = RequestMethod.GET)
    public String signup(@RequestParam("planId") int planId, ModelMap model) {
        if (planId != PlanEnum.BASIC.getId() && planId != PlanEnum.PRO.getId()) {
            LOGGER.error("Plan id {} not valid", planId);
            throw new IllegalAccessError("Invalid plan id");
        }
        model.addAttribute(ATTRIBUTE_NAME_PAYLOAD, new ProAccount());
        return VIEW_NAME_SIGNUP;
    }
}
