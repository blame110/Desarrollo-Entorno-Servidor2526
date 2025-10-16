package com.calabozo.mapa.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.calabozo.mapa.model.AuthProvider;
import com.calabozo.mapa.model.User;
import com.calabozo.mapa.repository.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        return processOAuth2User(userRequest, oauth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();

        String googleId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        Optional<User> userOptional = userRepository.findByGoogleId(googleId);
        User user;

        if (userOptional.isPresent()) {
            // Usuario existente - actualizar información
            user = userOptional.get();
            user.setName(name);
            user.setPicture(picture);
        } else {
            // Verificar si el email ya existe
            Optional<User> existingUserByEmail = userRepository.findByEmail(email);

            if (existingUserByEmail.isPresent()) {
                // Usuario existe con ese email pero diferente proveedor
                user = existingUserByEmail.get();
                user.setGoogleId(googleId);
                user.setProvider(AuthProvider.GOOGLE);
                user.setPicture(picture);
            } else {
                // Nuevo usuario
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setGoogleId(googleId);
                user.setPicture(picture);
                user.setProvider(AuthProvider.GOOGLE);
            }
        }

        userRepository.save(user);

        return new DefaultOAuth2User(
                oauth2User.getAuthorities(),
                attributes,
                "sub");
    }

}
