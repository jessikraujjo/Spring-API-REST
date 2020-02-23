package curso.api.rest.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import curso.api.rest.model.Usuario;

/*estabelece o gerenciador de token*/
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter{

	/*configurando o gerenciador de autenticacao*/
	protected JWTLoginFilter(String url, AuthenticationManager authenticationManager) {
		
		/*obriga a autenticar url*/
		super(new AntPathRequestMatcher(url));
		 
		/*genrenciador de autenticacao*/
		setAuthenticationManager(authenticationManager);
	}

	/*retorna o user ao processar autenticacao*/
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		
		// Pega o token para validar
		Usuario user = new ObjectMapper()
				.readValue(request.getInputStream(), Usuario.class);
		
		/*retornar o user login, senha e acesso*/
		return getAuthenticationManager()
				.authenticate(new UsernamePasswordAuthenticationToken(
						user.getLogin(), 
						user.getSenha()));
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		new JWTTokenAutenticacaoService().addAuthentication(response, authResult.getName());
	}

}
