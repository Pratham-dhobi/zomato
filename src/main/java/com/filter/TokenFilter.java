package com.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.service.TokenBlacklistService;
import com.util.JwtUtility;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenFilter implements Filter {

	@Autowired
	private JwtUtility jwtUtility;
	
	@Autowired
	private TokenBlacklistService tokenBlacklistService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
        String url = req.getRequestURL().toString();

        if (url.contains("/public/")) {
            chain.doFilter(request, response);
        }else {
	        String token = req.getHeader("Authorization");
	        if (token != null && token.startsWith("Bearer ")) {
	        	token = token.split(" ")[1]; 	// Remove 'Bearer ' prefix
	
	        	if(!tokenBlacklistService.isTokenBlacklisted(token)) {
	        		if (jwtUtility.validateToken(token)) {
		                String email = jwtUtility.validateTokeAndGetEmail(token);
		                String role = jwtUtility.validateTokenAndGetRole(token);
		
		                // Create authentication object
		                UsernamePasswordAuthenticationToken authentication = 
		                    new UsernamePasswordAuthenticationToken(email, null, 
		                    List.of(new SimpleGrantedAuthority(role)));
		                
		                // Set the authentication in the context
		                SecurityContextHolder.getContext().setAuthentication(authentication);
		                
		                request.setAttribute("email", email);
		                request.setAttribute("role", role);
		                
		                chain.doFilter(request, response);
		            } else {
		                HttpServletResponse res = (HttpServletResponse) response;
		                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		                res.getWriter().write("Invalid or expired token");
		            }
	        	}else {
	        		HttpServletResponse res = (HttpServletResponse) response;
	                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	                res.getWriter().write("Blacklisted Token.");
	        	}
	        } else {
	            HttpServletResponse res = (HttpServletResponse) response;
	            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            res.getWriter().write("Missing or invalid Authorization header");
	        }
        }
    }
}
