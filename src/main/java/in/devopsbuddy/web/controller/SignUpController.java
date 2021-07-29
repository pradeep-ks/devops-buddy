package in.devopsbuddy.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import in.devopsbuddy.enums.PlanEnum;
import in.devopsbuddy.enums.RoleEnum;
import in.devopsbuddy.persistence.domain.Plan;
import in.devopsbuddy.persistence.domain.Role;
import in.devopsbuddy.persistence.domain.User;
import in.devopsbuddy.persistence.domain.UserRole;
import in.devopsbuddy.util.UserUtil;
import in.devopsbuddy.web.domain.dto.ProAccount;
import in.devopsbuddy.web.service.PlanService;
import in.devopsbuddy.web.service.UserService;

@Controller
public class SignUpController {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SignUpController.class);

    public static final String URL_SIGNUP = "/signup";

    public static final String ATTRIBUTE_NAME_PAYLOAD = "payload";

    public static final String ATTRIBUTE_NAME_USERNAME_EXISTS = "usernameExists";

    public static final String ATTRIBUTE_NAME_EMAIL_EXISTS = "emailExists";

    public static final String ATTRIBUTE_NAME_SIGNED_UP = "signedUp";

    public static final String ATTRIBUTE_NAME_ERROR_MESSAGE = "message";

    public static final String VIEW_NAME_SIGNUP = "register/signup";

    @Autowired
    private UserService userService;

    @Autowired
    private PlanService planService;

    @RequestMapping(value = URL_SIGNUP, method = RequestMethod.GET)
    public String signup(@RequestParam("planId") int planId, ModelMap model) {
        if (planId != PlanEnum.BASIC.getId() && planId != PlanEnum.PRO.getId()) {
            LOGGER.error("Plan id {} not valid", planId);
            throw new IllegalAccessError("Invalid plan id");
        }
        model.addAttribute(ATTRIBUTE_NAME_PAYLOAD, new ProAccount());
        return VIEW_NAME_SIGNUP;
    }

    @RequestMapping(value = URL_SIGNUP, method = RequestMethod.POST)
    public String signup(@RequestParam(name = "planId", required = true) int planId,
            @RequestParam(name = "profileImage", required = false) MultipartFile profileImage,
            @Valid @ModelAttribute(ATTRIBUTE_NAME_PAYLOAD) ProAccount payload, ModelMap model) throws IOException {
        if (planId != PlanEnum.BASIC.getId() && planId != PlanEnum.PRO.getId()) {
            LOGGER.error("Plan id {} not valid", planId);
            throw new IllegalAccessError("Invalid plan id");
        }

        this.checkForDuplicates(payload, model);

        boolean duplicates = false;

        List<String> errors = new ArrayList<>();
        if (model.containsKey(ATTRIBUTE_NAME_USERNAME_EXISTS)) {
            LOGGER.warn("Username {} already exists!", payload.getUsername());
            model.addAttribute(ATTRIBUTE_NAME_SIGNED_UP, "false");
            errors.add("Username already exists!");
            duplicates = true;
        }

        if (model.containsKey(ATTRIBUTE_NAME_EMAIL_EXISTS)) {
            LOGGER.warn("Email {} already registered!", payload.getEmail());
            model.addAttribute(ATTRIBUTE_NAME_SIGNED_UP, "false");
            errors.add("Email already registered!");
            duplicates = true;
        }

        if (duplicates) {
            model.addAttribute(ATTRIBUTE_NAME_ERROR_MESSAGE, errors);
            return VIEW_NAME_SIGNUP;
        }

        User user = UserUtil.fromPayloadToDomainUser(payload);

        // Stores profile image to Amazon S3
        if (profileImage != null && !profileImage.isEmpty()) {
            String profileImageUrl = null;
            if (profileImageUrl != null) {
                user.setProfileImageUrl(profileImageUrl);
            } else {
                LOGGER.warn("There was a problem uploading profile image to aws");
            }
        }
        Plan selectedPlan = this.planService.findById(planId).orElse(null);
        if (selectedPlan == null) {
            model.addAttribute(ATTRIBUTE_NAME_SIGNED_UP, "false");
            model.addAttribute(ATTRIBUTE_NAME_ERROR_MESSAGE, "Plan id not found!");
            return VIEW_NAME_SIGNUP;
        }

        user.setPlan(selectedPlan);

        User registeredUser = null;
        Set<UserRole> userRoles = new HashSet<>();
        if (planId == PlanEnum.BASIC.getId()) {
            userRoles.add(new UserRole(user, new Role(RoleEnum.BASIC)));
            registeredUser = userService.save(user, PlanEnum.BASIC, userRoles);
        } else {
            userRoles.add(new UserRole(user, new Role(RoleEnum.PRO)));
            registeredUser = userService.save(user, PlanEnum.PRO, userRoles);
        }

        // auto-login registered user
        Authentication authentication = new UsernamePasswordAuthenticationToken(registeredUser, null,
                registeredUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        model.addAttribute(ATTRIBUTE_NAME_SIGNED_UP, "true");
        return VIEW_NAME_SIGNUP;
    }

    private void checkForDuplicates(@Valid ProAccount payload, ModelMap model) {
        if (this.userService.existsByUsername(payload.getUsername())) {
            model.addAttribute(ATTRIBUTE_NAME_USERNAME_EXISTS, true);
        }

        if (this.userService.existsByEmail(payload.getEmail())) {
            model.addAttribute(ATTRIBUTE_NAME_EMAIL_EXISTS, true);
        }
    }
}
