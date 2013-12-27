package com.btxtech.game.jsre.client.common.info;

import com.google.gwt.user.client.ui.SuggestOracle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 09.06.13
 * Time: 17:30
 */
public class Suggestion implements SuggestOracle.Suggestion, Serializable {
    private String string;

    /**
     * Used by GWT
     */
    Suggestion() {
    }

    public Suggestion(String string) {
        this.string = string;
    }

    @Override
    public String getDisplayString() {
        return string;
    }

    @Override
    public String getReplacementString() {
        return string;
    }

    public static Collection<? extends SuggestOracle.Suggestion> createSuggestionCollection(List<String> usersNames) {
        Collection<Suggestion> suggestions = new ArrayList<Suggestion>();
        for (String usersName : usersNames) {
            suggestions.add(new Suggestion(usersName));
        }
        return suggestions;
    }

    public static List<String> createStringList(SuggestOracle.Response response) {
        List<String> suggestions = new ArrayList<String>();
        if(response.getSuggestions() != null) {
            for (SuggestOracle.Suggestion suggestion : response.getSuggestions()) {
                suggestions.add(suggestion.getDisplayString());
            }
        }
        return suggestions;
    }
}
