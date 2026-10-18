package models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import utils.AppContext;


public class DailyStreakTracker implements Serializable {
    private static final long serialVersionUID = 1L;

    private int dailyStreak;
 
    private LocalDate lastActiveDate;

    private LocalDate lastCheckedDate;

    public DailyStreakTracker() {
        this.dailyStreak = 0;
        this.lastActiveDate = null;
        this.lastCheckedDate = null;
    }

    public void syncToDate(User user, LocalDate today) {
        if (user == null || today == null) return;

        // If we have no streak yet, just align checked date.
        if (dailyStreak <= 0 || lastActiveDate == null) {
            if (lastCheckedDate == null || today.isAfter(lastCheckedDate)) {
                lastCheckedDate = today;
            }
            return;
        }

        
        if (lastCheckedDate == null) { // If lastCheckedDate is missing (old save files), start syncing from the last known chain date.
            lastCheckedDate = lastActiveDate;
        }

        if (!today.isAfter(lastCheckedDate)) {
            return; // already synced for this date
        }

        LocalDate cursor = lastCheckedDate;

        while (cursor.isBefore(today)) {
            LocalDate nextDay = cursor.plusDays(1);
            LocalDate dayToValidate = nextDay.minusDays(1); // "yesterday" relative to nextDay

            
            if (lastActiveDate == null || !lastActiveDate.equals(dayToValidate)) { /*  If the chain was not maintained through 'dayToValidate',
             then a missed day occurred.*/
                if (user.getStreakSaves() > 0) {
                    user.consumeStreakSaves(1); // Consume exactly one save per missed day and extend the chain.
                    lastActiveDate = dayToValidate;
                } else {
                    dailyStreak = 0; // No saves -> streak breaks immediately.
                    lastActiveDate = null;
                    break;
                }
            }

            cursor = nextDay;
        }

        lastCheckedDate = today;
    }

    /**
     * Register that the user performed at least one task on this date.
     * Increments the streak once per day.
     */
    public void registerActivity(LocalDate date) {
        if (date == null) return;

        // If this is the first ever activity, start the streak.
        if (lastActiveDate == null || dailyStreak <= 0) {
            dailyStreak = 1;
            lastActiveDate = date;
            lastCheckedDate = date;
            return;
        }

        // If already counted for this day, do nothing.
        if (lastActiveDate.equals(date)) {
            return;
        }

        // If consecutive day, increment; else start new streak from 1.
        if (lastActiveDate.plusDays(1).equals(date)) {
            dailyStreak++;
        } else {
            dailyStreak = 1;
        }

        lastActiveDate = date;
        lastCheckedDate = date;
    }

    /**
     * Returns the number of missed full days between lastActiveDate and the given date.
     * Example: lastActive=Monday, date=Wednesday -> missedDays=1 (Tuesday)
     */
    public int getMissedDays(LocalDate date) {
        if (date == null || lastActiveDate == null) return 0;
        long diff = ChronoUnit.DAYS.between(lastActiveDate, date);
        if (diff <= 1) return 0;
        return (int) (diff - 1);
    }

    /**
     * Deprecated in the new design:
     * Previously used to increment streak after consuming streak saves lazily at completion time.
     * Now streak saves are consumed during syncToDate(...) and do NOT increment the streak.
     */
    public void registerActivityPreserved(LocalDate date) {
        // Preserve chain date without incrementing streak.
        if (date == null) return;
        if (lastActiveDate == null) {
            // If there's no streak yet, this should not create one.
            lastCheckedDate = date;
            return;
        }
        lastActiveDate = date;
        lastCheckedDate = date;
    }

    /** Convenience overload for current mode usage. */
    public void registerActivity() {
        registerActivity(AppContext.today());
    }

    public int getDailyStreak() {
        return dailyStreak;
    }

    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }
}
