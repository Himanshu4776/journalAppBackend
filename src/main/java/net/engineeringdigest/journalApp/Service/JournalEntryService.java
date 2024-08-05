package net.engineeringdigest.journalApp.Service;

import net.engineeringdigest.journalApp.Entity.JournalEntry;
import net.engineeringdigest.journalApp.Entity.User;
import net.engineeringdigest.journalApp.Repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JournalEntryService {
    @Autowired
    JournalEntryRepository journalEntryRepository;

    @Autowired
    UserService userService;

    @Transactional
    public void saveEntries(JournalEntry journalEntry, String userName) {
        User user = userService.findUserByName(userName);
        journalEntry.setDate(LocalDateTime.now());
        JournalEntry saved = journalEntryRepository.save(journalEntry);
        user.getJournals().add(saved);
        userService.saveEntries(user);
    }
    
    public void saveEntries(JournalEntry journalEntry) {
        journalEntryRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> getEntryById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    public void deleteById(ObjectId id, String userName) {
        User user = userService.findUserByName(userName);
        user.getJournals().removeIf(x -> x.getId().equals(id));
        userService.saveEntries(user);
        journalEntryRepository.deleteById(id);
    }
}
