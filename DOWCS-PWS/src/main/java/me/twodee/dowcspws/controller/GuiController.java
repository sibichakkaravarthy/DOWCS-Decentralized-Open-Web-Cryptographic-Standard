package me.twodee.dowcspws.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.twodee.dowcspws.Helper;
import me.twodee.dowcspws.ResultObject;
import me.twodee.dowcspws.model.dto.Ttp;
import me.twodee.dowcspws.model.dto.User;
import me.twodee.dowcspws.service.Accounts;
import me.twodee.dowcspws.service.Transactions;
import me.twodee.dowcspws.service.TtpManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class GuiController {

    private final Accounts accounts;
    private final Transactions transactions;
    private final TtpManager manager;

    @Autowired
    public GuiController(Accounts accounts, TtpManager manager, Transactions transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
        this.manager = manager;
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
        model.addAttribute("title", "Login - DemoPWS");
        session.setAttribute("csrf_token", UUID.randomUUID().toString());
        model.addAttribute("csrf_token", session.getAttribute("csrf_token"));
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {

        if (session.getAttribute("loggedIn") != null) {
            model.addAttribute("title", "Dashboard - DemoPWS");
            return "dashboard";
        }
        return "redirect:/";
    }

    @GetMapping("/connect/ttp")
    public String ttpList(Model model, HttpSession session) {
        if (session.getAttribute("loggedIn") != null) {

            var ttps = manager.getAvailablePwsList();
            model.addAttribute("title", "Connect a TTP to your account");
            model.addAttribute("ttps", ttps);
            return "ttp_list";
        }
        return "redirect:/";
    }

    @PostMapping("/")
    public String login(@Valid User.LoginData data, BindingResult result, HttpSession session, Model model) {
        try {
            Map<String, String> values = new HashMap<>();
            Helper.addNotNull(values, "name", data.identifier);
            model.addAttribute("values", values);

            if (result.hasErrors()) {
                model.addAttribute("error", result.getFieldErrors().get(0).getDefaultMessage());
                return loginView(model, session);
            }

            if (!session.getAttribute("csrf_token").equals(data.csrf)) {
                model.addAttribute("error", "Invalid login request");
                return loginView(model, session);
            }

            if (!accounts.hasCorrectCredentials(data)) {
                model.addAttribute("error", "The credentials you supplied are invalid");
                return loginView(model, session);
            }

            accounts.login(data.identifier);
            return "redirect:/dashboard";

        } catch (Throwable e) {
            model.addAttribute("error", "Something went wrong");
        }
        return loginView(model, session);
    }


    @GetMapping("/details")
    public String registerPwsView(Model model) throws IOException, URISyntaxException {
        var file = (getClass().getClassLoader().getResourceAsStream("pws_ec.pub"));
        assert file != null;
        String data = new String(file.readAllBytes());
        model.addAttribute("title", "Public Key - PWS");
        model.addAttribute("pubkey", data);
        return "pws_details";
    }

    @GetMapping("/register/ttp")
    public String registerTtp(Model model) {
        model.addAttribute("title", "Register a TTP");
        return "ttp_registration";
    }

    @PostMapping("/register/ttp")
    public String registerTtp(@Valid Ttp.Registration ttp, BindingResult result, Model model) {
        model.addAttribute("title", "Register a TTP");
        Map<String, String> values = new HashMap<>();
        Helper.addNotNull(values, "name", ttp.getName());
        Helper.addNotNull(values, "pwsId", ttp.getPwsId());
        Helper.addNotNull(values, "baseUrl", ttp.getBaseUrl());
        Helper.addNotNull(values, "challenge", ttp.getChallenge());

        if (result.hasErrors()) {
            model.addAttribute("error", result.getFieldErrors().get(0).getDefaultMessage());
        } else {
            var registerResult = manager.registerTtp(ttp);
            if (registerResult.isSuccessful) {
                return "redirect:/register/ttp/success";
            }
            model.addAttribute("error", registerResult.error);
            return registerTtp(model);
        }
        return "ttp_registration";
    }

    @GetMapping("/register/ttp/success")
    public String successfulTtpRegistration(Model model) {
        model.addAttribute("title", "Success! - PWS");
        return "ttp_registration_success";
    }

    @Data
    public static class LoginIdentity {
        public String identifier;
        public String password;
    }

    public static class AuthorizationDto {
        public LoginIdentity loginIdentity;
        public String initiationToken;
    }

    @Getter
    @Setter
    public static class LoginLoginIdentityWithMFA extends LoginIdentity {
        public String mfaData;
    }


//
//    @PostMapping("/login")
//    public <T> ResponseEntity<T> login(LoginIdentity loginIdentity) {
//
//        if (accounts.hasCorrectCredentials(loginIdentity)) {
//            accounts.login(loginIdentity);
//            // handle success
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//    }
//
//    @PostMapping("/authorize")
//    public Object login(AuthorizationDto authorizationDto, HttpSession session) {
//
//        if (accounts.hasCorrectCredentials(authorizationDto.loginIdentity) && session.getAttribute("initiationToken").equals(authorizationDto.initiationToken)) {
//            accounts.login(authorizationDto.loginIdentity);
//            // get the transaction identity related to the user identity and pws identity
//            String transactionIdentity = transactionManager.getTransactionIdentity(session, authorizationDto.loginIdentity.identifier);
//            String authToken = transactionManager.getAuthToken(authorizationDto.loginIdentity.identifier);
//            accounts.getPws(transactionIdentity).getRedirectUrl();
//
//        }
//        return new ResponseEntity<String>("https://facebook.com", HttpStatus.MOVED_PERMANENTLY);
//
//    }
//
//    @GetMapping("/authorize")
//    public ResponseEntity showAuthorizationPage(@RequestParam("pws_identifier") String pwsIdentifier, HttpSession session) {
//        String initiationToken = transactionManager.generateInitiationToken(session, pwsIdentifier);
//        // pass the token to moustache
//    }
}
