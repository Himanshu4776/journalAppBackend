package net.engineeringdigest.journalApp.Controller;

import net.engineeringdigest.journalApp.Entity.JournalEntry;
import net.engineeringdigest.journalApp.Entity.User;
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

    @GetMapping("{userName}")
    public ResponseEntity<?> getAllJournalEntriesOfUser(@PathVariable String userName) {
        User user = userService.findUserByName(userName);
        List<JournalEntry> journals = user.getJournals();
        if(!journals.isEmpty()) {
            return new ResponseEntity<>(journals, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("{userName}")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry entry, @PathVariable String userName) {
        try {
            User user = userService.findUserByName(userName);
            if(user != null) {
                entry.setDate(LocalDateTime.now());
                journalEntryService.saveEntries(entry, userName);
                return new ResponseEntity<>(entry, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId) {
        Optional<JournalEntry> entry = journalEntryService.getEntryById(myId);
        if (entry.isPresent()) {
            return entry.map(journalEntry -> new ResponseEntity<>(journalEntry, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{userName}/{myId}")
    // ? in ResponseEntity is like a wildcard type value
    public ResponseEntity<?> deleteJournalById(@PathVariable ObjectId myId, @PathVariable String userName) {
        journalEntryService.deleteById(myId, userName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/id/{myId}")
    public ResponseEntity<?> editById(@PathVariable ObjectId myId, @RequestBody JournalEntry myentry, @PathVariable String userName) {
        User user = userService.findUserByName(userName);
        List<JournalEntry> collect = user.getJournals().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
        JournalEntry old = journalEntryService.getEntryById(myId).orElse(null);
        if(old != null) {
            old.setTitle(myentry.getTitle() != null && !myentry.getTitle().isEmpty() ? myentry.getTitle() : old.getTitle());
            old.setContent(myentry.getContent() != null && !myentry.getContent().isEmpty() ? myentry.getContent() : old.getContent());

            journalEntryService.saveEntries(old, userName);
            return new ResponseEntity<>(old, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
