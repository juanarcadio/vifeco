package org.laeq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.laeq.model.Preferences;
import org.laeq.settings.Settings;

import java.io.File;
import java.io.IOException;

public class PreferencesService {
    private Preferences preferences;
    private String fileName = "preferences.json";

    public PreferencesService() {
        File file = new File(getFileName());
        if(file.exists()){
            try {
                preferences = new ObjectMapper().readValue(file, Preferences.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            preferences = new Preferences();
            export(preferences);
        }
    }

    private String getFileName(){
        return String.format("%s%s%s", Settings.defaultPath, File.separator, fileName);
    }

    public void export(Preferences preferences){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(getFileName()), preferences);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Preferences getPreferences() {
        return preferences;
    }

}