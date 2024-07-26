package org.main.hackerthon.api.service;

import lombok.RequiredArgsConstructor;
import org.main.hackerthon.api.domain.User;
import org.main.hackerthon.api.dto.LoginRequest;
import org.main.hackerthon.api.dto.OAuth2Attribute;
import org.main.hackerthon.api.oauth2.CustomOAuth2User;
import org.main.hackerthon.api.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService{

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); //google
        OAuth2Attribute attribute = OAuth2Attribute.of(registrationId, oAuth2User.getAttributes());

        String uniqueId = registrationId+" "+attribute.getProviderId();

        User user = saveOrUpdate(attribute, uniqueId);
        LoginRequest loginRequest = new LoginRequest(user);

        return new CustomOAuth2User(loginRequest);
    }

    private User saveOrUpdate(OAuth2Attribute attribute, String uniqueId){
        User user = userRepository.findByUniqueId(uniqueId)
                .map(entity -> entity.update(attribute.getEmail(), attribute.getName()))
                .orElse(attribute.toEntity(uniqueId));
        return userRepository.save(user);
    }
}
