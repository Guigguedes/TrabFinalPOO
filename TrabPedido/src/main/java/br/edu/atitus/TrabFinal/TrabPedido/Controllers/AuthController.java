package br.edu.atitus.TrabFinal.TrabPedido.Controllers;

import java.security.SecureRandom;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.util.RequestPayload;

import br.edu.atitus.TrabFinal.TrabPedido.Components.JwtUtils;
import br.edu.atitus.TrabFinal.TrabPedido.Controllers.payloads.LoginPayload;
import br.edu.atitus.TrabFinal.TrabPedido.Controllers.payloads.SignupPayload;
import br.edu.atitus.TrabFinal.TrabPedido.Entities.Usuario;
import br.edu.atitus.TrabFinal.TrabPedido.Services.UsuarioService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
	private final UsuarioService usuarioService;
	public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UsuarioService usuarioService) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
		this.usuarioService = usuarioService;
	}
	
	@PostMapping("/signup")
	public ResponseEntity<Object> signup(@RequestBody SignupPayload signup) {
		Usuario usuarioNovo = new Usuario();
		usuarioNovo.setNome(signup.getNome());
		usuarioNovo.setEmail(signup.getEmail());
		String password = gerarSenhaAleatoria(10);
		usuarioNovo.setSenha(new BCryptPasswordEncoder().encode(password));
		
		try {
			usuarioService.save(usuarioNovo);
			return ResponseEntity.status(HttpStatus.OK).body(password);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		
		
	}
	
	private String gerarSenhaAleatoria(int tamanho) {
		String CARACTERES_PERMITIDOS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        SecureRandom random = new SecureRandom();
        StringBuilder senha = new StringBuilder(tamanho);

        for (int i = 0; i < tamanho; i++) {
            int indice = random.nextInt(CARACTERES_PERMITIDOS.length());
            senha.append(CARACTERES_PERMITIDOS.charAt(indice));
        }
        return senha.toString();
    }


	@PostMapping("/signin")
	public ResponseEntity<Object> signin(@RequestBody LoginPayload login) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getSenha()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String tokenJwt = jwtUtils.generateTokenFromEmail(login.getEmail());
		return ResponseEntity.ok().body(tokenJwt);
		
	}

}