package program.api.socialapi.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import program.api.socialapi.dto.UserDTO;
import program.api.socialapi.entities.User;
import program.api.socialapi.security.jwt.JwtTokenProvider;
import program.api.socialapi.service.UserService;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Api(tags = "Authentication management")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    private final Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @ApiOperation(value = "Login user in system by giving him a token", consumes = "JSON")
    @PostMapping("/login")
    public ResponseEntity<HashMap<Object, Object>> login(
            @ApiParam(value = "user's login and password") @RequestBody UserDTO requestDTO
    ){
        try {
            String username = requestDTO.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDTO.getPassword()));
            HashMap<Object, Object> response = new HashMap<>();
            HashMap<Object, Object> responseContent = new HashMap<>();

            String token = jwtTokenProvider.createToken(username);

            response.put("result", "ok");
            responseContent.put("username", username);
            responseContent.put("token", token);
            response.put("response", responseContent);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            HashMap<Object, Object> response = new HashMap<>();
            response.put("result", "error");
            response.put("response", "Invalid username or password");

            return ResponseEntity.badRequest().body(response);
        }
    }

    @ApiOperation(value = "Creating a new user by saving him to the database and then logging him in by giving him a token", consumes = "JSON")
    @PostMapping("/register")
    public ResponseEntity<HashMap<Object, Object>> register(
            @ApiParam(value = "users's login, password and email")@RequestBody UserDTO requestDTO
    ){
        User newUser = new User();
        HashMap<Object, Object> response = new HashMap<>();
        HashMap<Object, Object> responseContent = new HashMap<>();

        if(userService.findByUsername(requestDTO.getUsername()) == null){
            newUser.setUsername(requestDTO.getUsername());
            newUser.setPassword(requestDTO.getPassword());
            Matcher matcher = emailPattern.matcher(requestDTO.getEmail());
            if(matcher.matches())
                newUser.setEmail(requestDTO.getEmail());
            else {
                response.put("result", "error");
                response.put("response", "Enter correct email!");

                return ResponseEntity.badRequest().body(response);
            }

            try {
                userService.register(newUser);
            }
            catch (DataIntegrityViolationException e){
                response.put("result", "error");
                response.put("response", "Fill in all fields!");

                return ResponseEntity.badRequest().body(response);
            }

            String token = jwtTokenProvider.createToken(newUser.getUsername());

            response.put("result", "ok");
            responseContent.put("username", newUser.getUsername());
            responseContent.put("token", token);
            response.put("response", responseContent);

            return ResponseEntity.ok(response);
        }
        else {
            response.put("result", "error");
            response.put("response", "This username already registered");
        }
        return ResponseEntity.badRequest().body(response);
    }
}
