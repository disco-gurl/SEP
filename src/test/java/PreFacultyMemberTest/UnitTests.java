package PreFacultyMemberTest;

import PreRegisterFaculty.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTests {
    private FacultyMember facultyMember;

    @BeforeEach
    public void set() {
        facultyMember = new FacultyMember("tracey@ed.ac.uk", "group29");
    }

    @Test
    public void testCreate(){
        assertEquals("tracey@ed.ac.uk", facultyMember.getEmail());
        assertTrue(facultyMember.isFirstLogin());
        assertEquals(0, facultyMember.getLoginAttempt());
    }

    @Test
    public void testChecks() {
        assertTrue(facultyMember.check("tracey@ed.ac.uk", "group29"));
        assertFalse(facultyMember.check("tracey@ed.ac.uk", "wrong"));
    }

    @Test
    public void testIncreaseAttempts() {
        facultyMember.increaseLogins();
        facultyMember.increaseLogins();
        assertEquals(2, facultyMember.getLoginAttempt());
    }

    @Test
    public void testResetAttempts() {
        facultyMember.increaseLogins();
        facultyMember.increaseLogins();
        facultyMember.resetAttempts();
        assertEquals(0, facultyMember.getLoginAttempt());
    }

    @Test
    public void testFirstLogin() {
        assertTrue(facultyMember.isFirstLogin());
        facultyMember.setFirstLogin(false);
        assertFalse(facultyMember.isFirstLogin());
    }

    @Test
    public void testNewPassword() {
        facultyMember.setPassword("new");
        assertTrue(facultyMember.check("tracey@ed.ac.uk", "new"));
        assertFalse(facultyMember.check("tracey@ed.ac.uk", "group29"));
    }

    @Test
    public void testNumberofFaculty() {
        RegistrationUtility utility = new RegistrationUtility("src/test/java/PreFacultyMemberTest/FakeLogin.txt");
        Collection<FacultyMember> list = utility.registerFaculty();
        assertEquals(3, list.size());
    }

    @Test
    public void testRegisterFacultyMembersFirstLogin() {
        RegistrationUtility utility = new RegistrationUtility("src/test/java/PreFacultyMemberTest/FakeLogin.txt");
        Collection<FacultyMember> list = utility.registerFaculty();

        for (FacultyMember l : list) {
            assertTrue(l.isFirstLogin());
        }
    }

    @Test
    public void testInvalidFile() {
        RegistrationUtility utility = new RegistrationUtility("a_file.txt");
        Collection<FacultyMember> list = utility.registerFaculty();
        assertTrue(list.isEmpty());
    }

}
