package br.edu.atitus.TrabFinal.TrabPedido.Components;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.edu.atitus.TrabFinal.TrabPedido.Entities.Usuario;
import br.edu.atitus.TrabFinal.TrabPedido.Services.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

	private final JwtUtils jwtUtils;
	private final UsuarioService usuarioService;

	public AuthTokenFilter(JwtUtils jwtUtils, UsuarioService usuarioService) {
		super();
		this.jwtUtils = jwtUtils;
		this.usuarioService = usuarioService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = jwtUtils.getJwtFromRequest(request);
			if (jwt != null && jwtUtils.validaJwtToken(jwt)) {
				String emailUsuario = jwtUtils.getEmailFromJwtToken(jwt);
				Usuario usuario = (Usuario) usuarioService.loadUserByUsername(emailUsuario);
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							usuario, null, usuario.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {

		}

		filterChain.doFilter(request, response);
	}

}