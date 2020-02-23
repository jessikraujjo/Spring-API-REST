package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	/* TEMPO DE VALIDADE DO TOKEN 2 dias*/
	private static final long EXPIRATION_TIME = 172800000;
	
	/*uma senha unica p compor a auth*/
	private static final String SECRET = "*SenhaExtremamenteSecreta";
	
	/*Prefixo padrao de token*/
	private static final String TOKEN_PREFIX = "Bearer";
	
	private static final String HEADER_STRING = "Authorization";
	
	/*Gerando token de auth e adicionando o cabecalho e resposta http*/
	public void addAuthentication(HttpServletResponse response, String username) throws IOException{
		/*montagem do token*/
		String JWT = Jwts.builder()//chama o gerador de token
				.setSubject(username)/*add o user*/
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))/*tempo de expiracao*/
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();/*compactacao e algortmo de geracao*/
		
		/*junta o token com o prefixo*/
		String token = TOKEN_PREFIX + " " + JWT; /*Bearer 4f5bgf5b5g5hg5nhnw*/
		
		/*add o cabecalho http*/
		response.addHeader(HEADER_STRING, token); /* Authorization: Bearer 4f5bgf5b5g5hg5nhnw*/
		
		/*liberando resposta para porta difierente do projeto angula*/
		response.addHeader("Access-Control-Allow-Origin", "*");
		
		/*escreve token como resposta no corpo do http*/
		response.getWriter().write("{\"Authorization\":\""+token+"\"}");  
		
	}
	
	/*Rtorna o user validado com token ou caso n seja valido retorna null*/
	public Authentication getAuthentication(HttpServletRequest request) {
		
		/*pega o token enviado no cabecalho http*/
		String token = request.getHeader(HEADER_STRING);
		
		if(token != null) {
			/*faz a validacao do token do user na requisicao*/
			String user = Jwts.parser().setSigningKey(SECRET) /* vem isso -> Bearer 4f5bgf5b5g5hg5nhnw*/
					.parseClaimsJws(token.replace(TOKEN_PREFIX, ""))/* fica so a numeracao-> 4f5bgf5b5g5hg5nhnw*/
					.getBody().getSubject(); /*jessica araujo*/
			
			if(user != null) {
				/*se ele foi autorizado se tem authenticacao*/
				Usuario usuario = ApplicationContextLoad.getApplicationContext()
						.getBean(UsuarioRepository.class).findUserByLogin(user);
				
				if(usuario != null) {
					return new UsernamePasswordAuthenticationToken(
							usuario.getLogin(),
							usuario.getSenha(),
							usuario.getAuthorities());
				}
			}

		}
		return null; /*n autorizado*/
		
	}
}
