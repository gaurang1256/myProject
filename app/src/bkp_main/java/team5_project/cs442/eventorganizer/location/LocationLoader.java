package team5_project.cs442.eventorganizer.location;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sangwon on 10/26/15.
 */
public class LocationLoader implements GetLocations {

    private List<Location> locations;

    private String locationFileName;

    public LocationLoader() {
        locationFileName = "res/raw/app_iit_locations.txt";
        locations = new ArrayList<Location>();
    }

    public LocationLoader(String fileName) {
        locations = new ArrayList<Location>();
    }

    private void getLocationFromFile() {
        try {

            InputStream inputStream = LocationLoader.class.getClassLoader().getResourceAsStream(locationFileName);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = r.readLine()) != null) {
                String[] var = line.split(",");
                Location location = new Location(var[0], Double.valueOf(var[1]), Double.valueOf(var[2]));
                locations.add(location);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Location> getLocation() {
        getLocationFromFile();
        return (locations.isEmpty()) ? null : locations;
    }
}
