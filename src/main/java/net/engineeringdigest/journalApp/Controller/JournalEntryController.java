package net.engineeringdigest.journalApp.Controller;

import net.engineeringdigest.journalApp.Entity.JournalEntry;
import net.engineeringdigest.journalApp.Entity.User;
import net.engineeringdigest.journalApp.Service.AuthUtilityService;
import net.engineeringdigest.journalApp.Service.JournalEntryService;
import net.engineeringdigest.journalApp.Service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {
    @Autowired
    JournalEntryService journalEntryService;

    @Autowired
    UserService userService;

    @Autowired
    AuthUtilityService authUtilityService;

    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser() {
        String userName = authUtilityService.getUserNameFormAuthContext();
        User user = userService.findUserByName(userName);
        List<JournalEntry> journals = user.getJournals();
        if(!journals.isEmpty()) {
            return new ResponseEntity<>(journals, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry entry) {
        try {
            String userName = authUtilityService.getUserNameFormAuthContext();
            User user = userService.findUserByName(userName);
            if(user != null) {
                entry.setDate(LocalDateTime.now());
                journalEntryService.saveEntries(entry, userName);
                return new ResponseEntity<>(entry, HttpStatus.CREATED);
            } else {
                System.out.println("user not found: " + userName);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId) {
        String userName = authUtilityService.getUserNameFormAuthContext();
        User user = userService.findUserByName(userName);
        List<JournalEntry> matchedEntry = user.getJournals()
                .stream().filter((x) -> x.getId().equals(myId))
                .collect(Collectors.toList());
        if(!matchedEntry.isEmpty()) {
            // means we have a matching entry with myId.
            Optional<JournalEntry> entry = journalEntryService.getEntryById(myId);
            if (entry.isPresent()) {
                return new ResponseEntity<>(entry.get(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{myId}")
    // ? in ResponseEntity is like a wildcard type value
    public ResponseEntity<?> deleteJournalById(@PathVariable ObjectId myId) {
        String userName = authUtilityService.getUserNameFormAuthContext();
        boolean deleted = journalEntryService.deleteById(myId, userName);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/id/{myId}")
    public ResponseEntity<?> editById(@PathVariable ObjectId myId, @RequestBody JournalEntry myentry) {
        String userName = authUtilityService.getUserNameFormAuthContext();
        User user = userService.findUserByName(userName);
        List<JournalEntry> collect = user.getJournals().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            JournalEntry old = journalEntryService.getEntryById(myId).orElse(null);
            if(old != null) {
                old.setTitle(myentry.getTitle() != null && !myentry.getTitle().isEmpty() ? myentry.getTitle() : old.getTitle());
                old.setContent(myentry.getContent() != null && !myentry.getContent().isEmpty() ? myentry.getContent() : old.getContent());

                journalEntryService.saveEntries(old, userName);
                return new ResponseEntity<>(old, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
