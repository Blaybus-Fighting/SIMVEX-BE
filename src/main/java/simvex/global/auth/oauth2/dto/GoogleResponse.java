package simvex.global.auth.oauth2.dto;

import java.util.Map;

public class GoogleResponse implements OAuth2Response{

    private final Map<String, Object> attribute;

    public GoogleResponse(Map<String, Object> attribute) {

        this.attribute = attribute;
    }

    @Override
    public String getProvider() {

        return "google";
    }

    @Override
    public String getProviderId() {

        return String.valueOf(attribute.get("sub"));
    }

    @Override
    public String getEmail() {

        return String.valueOf(attribute.get("email"));
    }

    @Override
    public String getName() {

        return String.valueOf(attribute.get("name"));
    }
}
