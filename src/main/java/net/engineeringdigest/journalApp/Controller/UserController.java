package net.engineeringdigest.journalApp.Controller;

import net.engineeringdigest.journalApp.Entity.User;
import net.engineeringdigest.journalApp.Repository.UserRepository;
import net.engineeringdigest.journalApp.Service.AuthUtilityService;
import net.engineeringdigest.journalApp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthUtilityService authUtilityService;

    @PutMapping
    public ResponseEntity<?> editById(@RequestBody User user) {
        String userName = authUtilityService.getUserNameFormAuthContext();
        User foundUser = userService.findUserByName(userName);
        if (foundUser != null) {
            try {
                foundUser.setUserName(user.getUserName());
                foundUser.setPassword(user.getPassword());
                userService.saveNewUser(foundUser);
                return new ResponseEntity<>(foundUser, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteByUsername() {
        String userName = authUtilityService.getUserNameFormAuthContext();
        userRepository.deleteByUserName(userName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
