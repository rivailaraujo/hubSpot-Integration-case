package com.example.hubspotintegration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Contact {
    private String vid;
    private Properties properties;

    @JsonProperty("identity-profiles")
    private List<IdentityProfile> identityProfiles = new ArrayList<>();


    public String getEmail() {
        for (IdentityProfile profile : identityProfiles) {
            if (profile.getIdentities() != null) {
                for (Identity identity : profile.getIdentities()) {
                    if ("EMAIL".equals(identity.getType())) {
                        return identity.getValue();
                    }
                }
            }
        }
        return null;
    }


    @Getter
    @Setter
    public static class Properties {
        private PropertyValue firstname;
        private PropertyValue lastname;

        @Getter
        @Setter
        public static class PropertyValue {
            private String value;
        }
    }

    @Getter
    @Setter
    public static class Identity {

        private String type;
        private String value;

    }

    @Getter
    @Setter
    public static class IdentityProfile {

        private Long vid;
        private List<Identity> identities;
    }

}

