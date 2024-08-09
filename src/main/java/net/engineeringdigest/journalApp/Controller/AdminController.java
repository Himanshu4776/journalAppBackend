package net.engineeringdigest.journalApp.Controller;

import net.engineeringdigest.journalApp.Entity.User;
import net.engineeringdigest.journalApp.Service.AuthUtilityService;
import net.engineeringdigest.journalApp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    UserService userService;

    @Autowired
    AuthUtilityService authUtilityService;

    @PostMapping("create-user")
    public ResponseEntity<User> createEntry(@RequestBody User user) {
        try {
            userService.saveNewAdminUser(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("all-users")
    public ResponseEntity<List<User>> getAllUsers() {
        String userName = authUtilityService.getUserNameFormAuthContext();
        User adminUser = userService.findUserByName(userName);
        if (adminUser != null && adminUser.getRoles().contains("ADMIN")) {
            return new ResponseEntity<>(userService.getAll(), HttpStatus.FOUND);
        } else {
            return new ResponseEntity<>(Arrays.asList(),HttpStatus.NOT_FOUND);
        }
    }
}
