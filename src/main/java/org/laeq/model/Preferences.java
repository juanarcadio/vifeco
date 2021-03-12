package org.laeq.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.*;
import java.util.stream.Collectors;

@JsonPropertyOrder({"locale"})
@JsonIgnoreProperties({"localAvailables"})
public class Preferences {
    public List<String> languages = new ArrayList<>();

    public Locale locale;

    public Preferences() {
        languages.add("English");
        languages.add("Français");
        languages.add("Español");
    }

    @JsonIgnore
    public void setLocale(String key){
        locale = Locale.ENGLISH;
    }

    @JsonIgnore
    public Locale getLocale(){
        if(locale == null){
            return getDefaultLocale();
        }else {
            return locale;
        }
    }

    @JsonIgnore
    public Locale getDefaultLocale(){
        return Locale.ENGLISH;
    }
}
