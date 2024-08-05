package net.engineeringdigest.journalApp.Controller;

import net.engineeringdigest.journalApp.Entity.User;
import net.engineeringdigest.journalApp.Service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<User> createEntry(@RequestBody User user) {
        try {
            userService.saveEntries(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return new ResponseEntity<>(userService.getAll(), HttpStatus.FOUND);
    }

    @PutMapping("/{userName}")
    public ResponseEntity<?> editById(@PathVariable User user, String userName) {
        User foundUser = userService.findUserByName(userName);
        if(foundUser != null) {
            try {
                foundUser.setUserName(user.getUserName());
                foundUser.setPassword(user.getPassword());
                userService.saveEntries(foundUser);
                return new ResponseEntity<>(foundUser, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
