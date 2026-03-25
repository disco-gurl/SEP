package PreRegisterFaculty;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class RegistrationUtility {
    private String filePath;

    public RegistrationUtility(String s) {
        this.filePath = filePath;
    }

    public Collection<FacultyMember> registerFaculty() {
        Collection<FacultyMember> listofMembers = new ArrayList<>();

        try (BufferedReader list = new BufferedReader(new FileReader(filePath))){ //saves the files into list
            String lines;
            while ((lines = list.readLine()) != null){ //reads each line of the file
                lines = lines.trim(); //remove spaces
                String [] information = lines.split(","); //split into 2 for the same and password
                if (information.length != 2) continue; //if it is more then stop

                String email = information[0].trim();
                String password = information[1].trim();

                FacultyMember f = new FacultyMember(email, password); //make new faculty member
                listofMembers.add(f);
            }
        }

        catch (IOException e) {
            System.out.print("Error"); //if it does not work then return error
        }

        return listofMembers;
    }
}
