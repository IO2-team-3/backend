package com.team3.central.services;

import com.team3.central.controllers.AuthenticationRequest;
import com.team3.central.controllers.AuthenticationResponse;
import com.team3.central.controllers.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  //private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) {
    var user = User.builder()
        .username(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .build();
    //repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder().token(jwtToken).build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    //var user = repository.findByEmail(request.getEmail()).orElseThrow(new Exception());
    //var jwtToken = jwtService.generateToken(user);
    //return AuthenticationResponse.builder().token(jwtToken).build();
    return null;
  }
}
