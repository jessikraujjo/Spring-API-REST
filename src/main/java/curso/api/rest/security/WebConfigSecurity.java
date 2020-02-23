package curso.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.AntPathMatcher;

import curso.api.rest.service.ImplementacaoUserDetailsService;

/*mapear url, enderecos, autoriza ou bloqueia acesso a url*/
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	// config as solicitacoes de acesso plo o http
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*ativando a proteçao contra user q n estao validados por token*/	
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		
		/* ativando restrincao a pag inicial do sistema: o index*/
		.disable().authorizeRequests().antMatchers("/").permitAll()
		.antMatchers("/index").permitAll()
		
		/*url de logout - Redireciona apos o user deslogar do siste*/
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
		
		/*mapeia url de logout e invalida o user*/
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		
		/*Filtra requisicoes de logins para autenticacao*/
		.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
				UsernamePasswordAuthenticationFilter.class)
		
		/*filtra demais requisicoes p verificar a presenca do Token jwt no Header Http*/
			.addFilterBefore(new JwtApiAutenticacaoFilter(),UsernamePasswordAuthenticationFilter.class);
	}
	
	@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			// Tservice ira consultar o user no banco
			auth.userDetailsService(implementacaoUserDetailsService)
			//padrao de codificacao de senha
			.passwordEncoder(new BCryptPasswordEncoder());
		
		}
}
