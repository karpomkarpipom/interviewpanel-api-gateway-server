package com.gateway.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gateway.bean.AuthResponse;
import com.gateway.bean.LoginRequest;
import com.gateway.bean.Role;
import com.gateway.bean.User;
import com.gateway.exception.CustomException;
import com.gateway.service.ILoginService;



@Controller
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private ILoginService iLoginService;
    
    @CrossOrigin("*")
    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<Map<String, String>> signup(@RequestBody User user) {
    	Map<String, String> responseSuccessMap=new HashMap<String,String>();
    	//revisit this code feature------
    	Set<Role> roleSet=new HashSet<Role>();
    	roleSet.add(new Role(1,"USER"));
    	user.setRole(roleSet);
    	//------------
    	
    	User dbuserObj=iLoginService.saveUser(user);
    	if(dbuserObj!=null) {
    		responseSuccessMap.put("status","User "+dbuserObj.getFirstName() +" signedup Successfully!" );
    	}else {
    	      throw new CustomException("Signup unsuccessful!",HttpStatus.INTERNAL_SERVER_ERROR);
    	}
        return new ResponseEntity<Map<String, String>>(responseSuccessMap, HttpStatus.CREATED);
    }
    
    @CrossOrigin("*")
    @PostMapping("/authenticate")
    @ResponseBody
    public ResponseEntity<AuthResponse> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
        String token = iLoginService.login(loginRequest.getEmail(),loginRequest.getPassword());
        HttpHeaders headers = new HttpHeaders();
        List<String> headerlist = new ArrayList<>();
        List<String> exposeList = new ArrayList<>();
        headerlist.add("Content-Type");
        headerlist.add(" Accept");
        headerlist.add("X-Requested-With");
        headerlist.add("Authorization");
        headers.setAccessControlAllowHeaders(headerlist);
        exposeList.add("Authorization");
        headers.setAccessControlExposeHeaders(exposeList);
        headers.set("Authorization", token);
        return new ResponseEntity<AuthResponse>(new AuthResponse(token), headers, HttpStatus.CREATED);
    }
    @CrossOrigin("*")
    @PostMapping("/signout")
    @ResponseBody
    public ResponseEntity<AuthResponse> logout (@RequestHeader(value="Authorization") String token) {
        HttpHeaders headers = new HttpHeaders();
      if (iLoginService.logout(token)) {
          headers.remove("Authorization");
          return new ResponseEntity<AuthResponse>(new AuthResponse("logged out"), headers, HttpStatus.CREATED);
      }
        return new ResponseEntity<AuthResponse>(new AuthResponse("Logout Failed"), headers, HttpStatus.NOT_MODIFIED);
    }

    /**
     *
     * @param token
     * @return boolean.
     * if request reach here it means it is a valid token.
     */
    @PostMapping("/valid/token")
    @ResponseBody
    public Boolean isValidToken (@RequestHeader(value="Authorization") String token) {
        return true;
    }


    @PostMapping("/signin/token")
    @CrossOrigin("*")
    @ResponseBody
    public ResponseEntity<AuthResponse> createNewToken (@RequestHeader(value="Authorization") String token) {
        String newToken = iLoginService.createNewToken(token);
        HttpHeaders headers = new HttpHeaders();
        List<String> headerList = new ArrayList<>();
        List<String> exposeList = new ArrayList<>();
        headerList.add("Content-Type");
        headerList.add(" Accept");
        headerList.add("X-Requested-With");
        headerList.add("Authorization");
        headers.setAccessControlAllowHeaders(headerList);
        exposeList.add("Authorization");
        headers.setAccessControlExposeHeaders(exposeList);
        headers.set("Authorization", newToken);
        return new ResponseEntity<AuthResponse>(new AuthResponse(newToken), headers, HttpStatus.CREATED);
    }
}
