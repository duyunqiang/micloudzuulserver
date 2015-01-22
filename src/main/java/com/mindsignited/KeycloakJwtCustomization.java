package com.mindsignited;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by nicholaspadilla on 1/21/15.
 */
@RefreshScope
@Component
public class KeycloakJwtCustomization implements JwtAccessTokenConverterConfigurer, AccessTokenConverter {

    final String KEYCOAK_CLIENT_ID = "aud";

    @Value("${spring.oauth2.resource.id}")
    private String currentResourceId;

    private KeycloakUserAuthenticationConverter userTokenConverter = new KeycloakUserAuthenticationConverter();

    @PostConstruct
    public void init(){
        userTokenConverter.setCurrentResourceId(currentResourceId);
    }

    @Override
    public void configure(JwtAccessTokenConverter converter) {
        converter.setAccessTokenConverter(this);
    }

    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        Map<String, Object> response = new HashMap<String, Object>();
        OAuth2Request clientToken = authentication.getOAuth2Request();

        if (!authentication.isClientOnly()) {
            response.putAll(userTokenConverter.convertUserAuthentication(authentication.getUserAuthentication()));
        } else {
            if (clientToken.getAuthorities()!=null && !clientToken.getAuthorities().isEmpty()) {
                response.put(KeycloakUserAuthenticationConverter.AUTHORITIES, clientToken.getAuthorities());
            }
        }

        // we may want to specify dynamic scopes?
        if (token.getScope()!=null) {
            response.put(SCOPE, token.getScope());
        }
        if (token.getAdditionalInformation().containsKey(JTI)) {
            response.put(JTI, token.getAdditionalInformation().get(JTI));
        }

        if (token.getExpiration() != null) {
            response.put(EXP, token.getExpiration().getTime() / 1000);
        }

        response.putAll(token.getAdditionalInformation());

        response.put(CLIENT_ID, clientToken.getClientId());
        if (clientToken.getResourceIds() != null && !clientToken.getResourceIds().isEmpty()) {
            response.put(AUD, clientToken.getResourceIds());
        }
        return response;
    }

    public OAuth2AccessToken extractAccessToken(String value, Map<String, ?> map) {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(value);
        Map<String, Object> info = new HashMap<String, Object>(map);
        info.remove(EXP);
        info.remove(AUD);
        info.remove(CLIENT_ID);
        info.remove(SCOPE);
        if (map.containsKey(EXP)) {
            token.setExpiration(new Date((Long) map.get(EXP) * 1000L));
        }
        if (map.containsKey(JTI)) {
            info.put(JTI, map.get(JTI));
        }
        @SuppressWarnings("unchecked")
        Collection<String> scope = (Collection<String>) map.get(SCOPE);
        if (scope != null) {
            token.setScope(new HashSet<String>(scope));
        }
        token.setAdditionalInformation(info);
        return token;
    }

    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        Map<String, String> parameters = new HashMap<String, String>();
        Set<String> scope = new LinkedHashSet<String>(map.containsKey(SCOPE) ? (Collection<String>) map.get(SCOPE) : Collections.<String>emptySet());
        // the user gets populated with authorities only for this service - or none.
        Authentication user = userTokenConverter.extractAuthentication(map);
        String clientId = (String) map.get(KEYCOAK_CLIENT_ID);
        parameters.put(CLIENT_ID, clientId);
        Object access = map.get(KeycloakUserAuthenticationConverter.RESOURCE_ACCESS);
        Set<String> resourceIds = new LinkedHashSet<String>();
        if(access != null){
            if(access instanceof String) {
                resourceIds.add((String) access);
            }else if(access instanceof Collection) {
                resourceIds.addAll((Collection<String>) access);
            }else if(access instanceof Map){
                if(((Map)access).containsKey(currentResourceId)){
                    resourceIds.add(currentResourceId);
                }else{
                    // this needs to be here so that the internal checks can check for access - if the list is empty it doesn't check
                    resourceIds.add("dummy");
                }
            }
        }
        // FIXME: there seems to be an issue with the way this works.  If we have a list of resourceId's thats fine,
        // however each resource is a service and so the Principle might have different authorities for each resource.
        // so what i am going to try out here is to pull out the currentResourceId, service this authentication is
        // happening on, and only get the authorities for that one resource.

        // this portionn is for the client authorities - so leave as is.
        OAuth2Request request = new OAuth2Request(parameters, clientId, null, true, scope, resourceIds, null, null,
                null);
        return new OAuth2Authentication(request, user);
    }

}
