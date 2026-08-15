package com.payme.gateway.config;

import com.payme.gateway.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/*
 since jwt filter is at gateway now , I want to make sure the req comes from the valid src [jwt + user_email here]
 so that when the /users/me is hit , we know who was it instead of having another JwtAuthFilter in users

 solution is to pass a header -> with email of the user [as email + pass auth]
* */

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        String token = authHeader.substring(7);

        if(jwtService.isTokenValid(token)){
            String username = jwtService.extractUsername(token);
            String role = jwtService.extractRole(token);

            if(SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = User
                        .withUsername(username)
                        .password("")
                        .authorities(role)
                        .build();

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            // attaching header to the existing req
            HttpServletRequest wrappedReq = new HttpServletRequestWrapper(request){
                @Override
                public String getHeader(String name){
                    if("X-User-Email".equalsIgnoreCase(name)){
                        return username;
                    }
                    if("X-User-Role".equalsIgnoreCase(name)){
                        return role;
                    }
                    return super.getHeader(name);
                }
            };
            filterChain.doFilter(wrappedReq,response);
            return;
        }

        filterChain.doFilter(request,response);
    }
}
