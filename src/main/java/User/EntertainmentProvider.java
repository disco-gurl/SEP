package User;

import java.util.ArrayList;
import java.util.Collection;
import Event.Event;

/**
 * An EP is an organisation that creates and manages events and the even performances.
 */
public class EntertainmentProvider extends User {
    private String orgName;
    private String businessNumber;
    private String name;
    private String description;
    private Collection<Event> events;

    /**
     * The constructor for the EPs. Creates the object based on the given information about it when registering.
     *
     * @param email
     * @param password
     * @param orgName
     * @param businessNumber
     * @param name
     * @param description
     */
    public EntertainmentProvider(String email, String password, String orgName, String businessNumber, String name, String description) {
        super(email, password);
        this.orgName = orgName;
        this.businessNumber = businessNumber;
        this.name = name;
        this.description = description;
        this.events = new ArrayList<>();
    }

    /**
     * The getters and setters needed in the method.
     * @return
     */

    public String getOrgName() {
        return orgName;
    }

    public String getBusinessNumber() {
        return businessNumber;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Adds an event to this EP object.
     *
     * @param event the added event
     */
    public void addEvent(Event event) {
        events.add(event);
    }

    /**
     * gets the events of this EP.
     *
     * @return the collection of events.
     */
    public Collection<Event> getEvents() {
        return events;
    }
}
