package com.mindsignited;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nicholaspadilla on 1/21/15.
 */
public class KeycloakUserAuthenticationConverter implements UserAuthenticationConverter {

    public static final String RESOURCE_ACCESS = "resource_access";

    // LOOK: we can now use email if we really wanted to, just has to be unique
    public static final String CUSTOM_USERNAME = "preferred_username";

    private Collection<? extends GrantedAuthority> defaultAuthorities;

    private String currentResourceId;

    /**
     * Default value for authorities if an Authentication is being created and the input has no data for authorities.
     * Note that unless this property is set, the default Authentication created by {@link #extractAuthentication(java.util.Map)}
     * will be unauthenticated.
     *
     * @param defaultAuthorities the defaultAuthorities to set. Default null.
     */
    public void setDefaultAuthorities(String[] defaultAuthorities) {
        this.defaultAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
                .arrayToCommaDelimitedString(defaultAuthorities));
    }

    // LOOK: might have to convert this to what spring expects, like USERNAME and AUD, maybe.
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put(CUSTOM_USERNAME, authentication.getName());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(RESOURCE_ACCESS, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        return response;
    }

    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(CUSTOM_USERNAME)) {
            return new UsernamePasswordAuthenticationToken(map.get(CUSTOM_USERNAME), "N/A", getAuthorities(map));
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        if (!map.containsKey(RESOURCE_ACCESS)) {
            return defaultAuthorities;
        }
        Object authorities = map.get(RESOURCE_ACCESS);
        if (authorities instanceof String) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
        }
        if (authorities instanceof Collection) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
                    .collectionToCommaDelimitedString((Collection<?>) authorities));
        }
        if (authorities instanceof Map) {
            Map<String, Map<String, List<String>>> grants = (Map<String, Map<String, List<String>>>) authorities;
            if(grants.containsKey(currentResourceId)){
                Map<String, List<String>> rolesMap = grants.get(currentResourceId);
                return AuthorityUtils.createAuthorityList(StringUtils.
                        collectionToCommaDelimitedString((List<String>)rolesMap.get("roles")));
            }else{
                return defaultAuthorities;
            }
        }
        throw new IllegalArgumentException("Authorities must be either a String or a Collection");
    }

    public void setCurrentResourceId(String currentResourceId) {
        this.currentResourceId = currentResourceId;
    }
}
