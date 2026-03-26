package User;

public class StudentPreferences {

    private Student student;
    private boolean preferMusicEvents;
    private boolean preferTheaterEvents;
    private boolean preferDanceEvents;
    private boolean preferMovieEvents;
    private boolean preferSportsEvents;

    public StudentPreferences(Student student) {

        this.student = student;
        this.preferMusicEvents = false;
        this.preferTheaterEvents = false;
        this.preferDanceEvents = false;
        this.preferMovieEvents = false;
        this.preferSportsEvents = false;

    }

    public boolean updatePreferences(String studentRawStringPreferences) {

        // reset everything first
        this.preferMusicEvents = false;
        this.preferTheaterEvents = false;
        this.preferDanceEvents = false;
        this.preferMovieEvents = false;
        this.preferSportsEvents = false;

        if (studentRawStringPreferences == null || studentRawStringPreferences.trim().isEmpty()) {
            return false;
        }

        boolean foundValid = false;
        String[] parts = studentRawStringPreferences.split(",");

        for (String part : parts) {
            String trimmed = part.trim().toLowerCase();
            switch (trimmed) {
                case "music":
                    this.preferMusicEvents = true;
                    foundValid = true;
                    break;
                case "theatre":
                    this.preferTheaterEvents = true;
                    foundValid = true;
                    break;
                case "dance":
                    this.preferDanceEvents = true;
                    foundValid = true;
                    break;
                case "movie":
                    this.preferMovieEvents = true;
                    foundValid = true;
                    break;
                case "sports":
                    this.preferSportsEvents = true;
                    foundValid = true;
                    break;
                default:
                    break; // just ignore anything we dont recognise
            }
        }

        return foundValid;
    }

    public Student getStudent() {
        return student;
    }

    public String toString() {

        String result = "Preferences: ";

        if (preferMusicEvents) result += "Music ";
        if (preferTheaterEvents) result += "Theatre ";
        if (preferDanceEvents) result += "Dance ";
        if (preferMovieEvents) result += "Movie ";
        if (preferSportsEvents) result += "Sports ";
        if (!preferMusicEvents && !preferTheaterEvents && !preferDanceEvents
                && !preferMovieEvents && !preferSportsEvents) {

            result += "None set";
        }

        return result;
    }

    public boolean getPreferMusicEvents() { return preferMusicEvents; }

    public boolean getPreferTheaterEvents() { return preferTheaterEvents; }

    public boolean getPreferDanceEvents() { return preferDanceEvents; }

    public boolean getPreferMovieEvents() { return preferMovieEvents; }

    public boolean getPreferSportsEvents() { return preferSportsEvents; }
}
