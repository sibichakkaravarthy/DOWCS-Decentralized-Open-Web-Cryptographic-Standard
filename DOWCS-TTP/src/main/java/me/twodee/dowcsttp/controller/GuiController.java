package me.twodee.dowcsttp.controller;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.twodee.dowcsttp.Helper;
import me.twodee.dowcsttp.ResultObject;
import me.twodee.dowcsttp.model.dto.Pws;
import me.twodee.dowcsttp.model.dto.User;
import me.twodee.dowcsttp.service.Accounts;
import me.twodee.dowcsttp.service.Authorization;
import me.twodee.dowcsttp.service.Transactions;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class GuiController {

    private final Accounts accounts;

    private final Authorization authorization;

    public GuiController(Accounts accounts, Authorization authorization) {
        this.accounts = accounts;
        this.authorization = authorization;
    }

    @GetMapping("/register")
    public String register(Model formModel) {
        formModel.addAttribute("title", "Create a new account");
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid User.RegistrationData data, BindingResult result, Model formModel) {

        if (result.hasErrors()) {
            formModel.addAttribute("error", result.getFieldErrors().get(0).getDefaultMessage());
        } else {
            ResultObject registrationResult = accounts.register(data);
            if (registrationResult.isSuccessful) {
                formModel.addAttribute("complete", true);
            } else {
                formModel.addAttribute("error", registrationResult.error);
            }
        }
        Map<String, String> values = new HashMap<>();
        Helper.addNotNull(values, "name", data.name);
        Helper.addNotNull(values, "email", data.email);

        formModel.addAttribute("values", values);
        return register(formModel);
    }

    @GetMapping("/")
    public String loginView(Model model, HttpSession session) {
        model.addAttribute("title", "Login - MockTTP");
        model.addAttribute("action", "/");
        session.setAttribute("csrf_token", UUID.randomUUID().toString());
        model.addAttribute("csrf_token", session.getAttribute("csrf_token"));
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {

        if (session.getAttribute("loggedIn") != null) {
            model.addAttribute("title", "Dashboard - MockTTP");
            return "dashboard";
        }
        return "redirect:/";
    }

    @PostMapping("/")
    public String login(@Valid User.LoginData data, BindingResult result, HttpSession session, Model model) {

        Map<String, String> values = new HashMap<>();
        Helper.addNotNull(values, "identifier", data.identifier);
        model.addAttribute("values", values);
        if (isLoginSuccessful(data, result, session, model)) {
            return "redirect:/dashboard";
        }
        return loginView(model, session);

    }

    private boolean isLoginSuccessful(@Valid User.LoginData data, BindingResult result, HttpSession session, Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("error", result.getFieldErrors().get(0).getDefaultMessage());
                return false;
            }
            if (!session.getAttribute("csrf_token").equals(data.csrf)) {
                model.addAttribute("error", "Invalid login request");
                return false;
            }
            if (!accounts.hasCorrectCredentials(data)) {
                model.addAttribute("error", "The credentials you supplied are invalid");
                return false;
            }
            accounts.login(data.identifier);
            return true;

        } catch (Throwable e) {
            model.addAttribute("error", "Something went wrong");
        }
        return false;
    }

    @GetMapping("/register/pws")
    public String registerPwsView(Model model) {
        model.addAttribute("title", "Register PWS - MockTTP");

        return "pws_registration";
    }

    @PostMapping("/register/pws")
    public String submitPwsRegistration(@Valid Pws.Registration data, BindingResult result, HttpSession session, Model model) {
        try {

            if (!accounts.isLoggedIn()) {
                return loginView(model, session);
            }

            Map<String, String> values = new HashMap<>();
            Helper.addNotNull(values, "name", data.name);
            Helper.addNotNull(values, "description", data.description);
            Helper.addNotNull(values, "callback", data.callback);
            Helper.addNotNull(values, "baseUrl", data.baseUrl);
            Helper.addNotNull(values, "pubkey", data.pubkey);

            model.addAttribute("values", values);

            if (result.hasErrors()) {
                model.addAttribute("error", result.getFieldErrors().get(0).getDefaultMessage());
                return registerPwsView(model);
            }

            var authResult = authorization.createNewPws(data, accounts.getCurrentUser());
            if (authResult.isSuccessful) {
                Pws.Challenge challenge = (Pws.Challenge) authResult.obj;
                model.addAttribute("title", "PWS Registration Challenge - MockTTP");
                model.addAttribute("challenge", challenge.value);
                model.addAttribute("pwsId", challenge.id);
                model.addAttribute("url", challenge.url);

                return "complete_pws_registration";
            } else {
                model.addAttribute("error", authResult.error);
                return registerPwsView(model);
            }

        } catch (Throwable e) {
            model.addAttribute("error", "Something went wrong");
            e.printStackTrace();
            return registerPwsView(model);

        }
    }

    @GetMapping("/verify_pws/{pwsId}")
    public String verifyPws(Model model, @PathVariable String pwsId) {
        model.addAttribute("title", "Verification - MockTTP");
        var pwsOptional = authorization.getPws(pwsId);
        if (pwsOptional.isPresent()) {
            var pws = pwsOptional.get();
            RestTemplate restTemplate = new RestTemplate();
            try {
                var result = restTemplate.getForEntity(pws.getChallengeUrl(), Pws.ChallengeResult.class);

                if (result.hasBody() && result.getBody() != null && BCrypt.checkpw(result.getBody().challenge, pws.getChallengeHash())) {
                    model.addAttribute("message", "Successfully added " + pws.getName() + " to MockTTP");
                    authorization.verifyPws(pws.getId());
                } else {
                    model.addAttribute("message", "MockTTP couldn't find the right challenge result in the url. Check and try again");
                }
            } catch (HttpClientErrorException e) {
                if (!e.getStatusCode().is2xxSuccessful()) {
                    model.addAttribute("message", "MockTTP couldn't find the right challenge result in the url. Check and try again");
                } else {
                    model.addAttribute("message", "Something went wrong");
                    e.printStackTrace();
                }
                return "pws_challenge_result";
            }
        } else {
            model.addAttribute("message", "Invalid PWS");
        }
        return "pws_challenge_result";
    }

    @GetMapping("/connect")
    public String connectPwsAccount(@RequestParam("pws_identifier") String pwsId, HttpSession session, Model model) {

        var pws = authorization.getPws(pwsId);

        if (pws.isPresent()) {
            model.addAttribute("title", "Authorize " + pws.get().getName() + " to use your MockTTP account");
            model.addAttribute("action", "/connect");
            session.setAttribute("csrf_token", UUID.randomUUID().toString());
            model.addAttribute("special", pwsId);
            model.addAttribute("csrf_token", session.getAttribute("csrf_token"));
        } else {
            model.addAttribute("title", "Invalid PWS connection request");
            model.addAttribute("complete", true);
        }
        return "login";
    }

    @PostMapping("/connect")
    public String connectPwsAccount(@Valid User.PwsConnectAuthData data, BindingResult result, HttpSession session, Model model) {
        Map<String, String> values = new HashMap<>();
        Helper.addNotNull(values, "identifier", data.identifier);
        model.addAttribute("values", values);

        if (isLoginSuccessful(data, result, session, model)) {
            var pws = authorization.getPws(data.special);
            if (pws.isEmpty()) {
                model.addAttribute("error", "Invalid PWS");
                return connectPwsAccount(data.special, session, model);
            }
            var pwsVal = pws.get();
            return "redirect:" + pwsVal.getBaseUrl() + pwsVal.getCallback();
        }
        return connectPwsAccount(data.special, session, model);
    }
}
