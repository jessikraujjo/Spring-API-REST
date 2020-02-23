package curso.api.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;

/*@CrossOrigin(origins = "http://localhost:8090/") permite acesso aos metodos ou endpoints, qq requisicao pra Api, passando a origin restringe, sem origem todos podem acessar*/

@RestController /*Arquitetura RESTful*/
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired /*se fosse CDI seria @inject*/
	private UsuarioRepository usuariorepository;
	
	/*servico Restfull*/
	@GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v1")
	public ResponseEntity <Usuario>initv1(@PathVariable(value = "id") Long id) {
		 
		Optional<Usuario> usuario = usuariorepository.findById(id);
		System.out.println("Executando versão 1");
		 /*retorno de relatorio*/
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);

	}
	/*servico Restfull*/
	@GetMapping(value = "v2/{id}", produces = "application/json")
	public ResponseEntity <Usuario>initv2(@PathVariable(value = "id") Long id) {
		 
		Optional<Usuario> usuario = usuariorepository.findById(id);
		 System.out.println("Executando versão 2");
		 /*retorno de relatorio*/
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);

	}
	
	/*supondo q osistema fique lento ao carregar*/
	@GetMapping(value = "/", produces = "application/json")
	@Cacheable("cacheusuarios")
	public ResponseEntity <List<Usuario>>usuario() throws InterruptedException {
		List<Usuario> list = (List<Usuario>) usuariorepository.findAll();
		Thread.sleep(6000);/*segura o codigo por 6seg*/
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);

	}
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario>cadastrar(@RequestBody Usuario usuario){
		
		for(int pos = 0 ; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhacriptografada);
		Usuario usuarioSalvo = usuariorepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario>atualizar(@RequestBody Usuario usuario){
		
		for(int pos = 0 ; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		Usuario userTemporario = usuariorepository.findUserByLogin(usuario.getLogin());
		//caso senha ja exista
		if(!userTemporario.getSenha().equals(usuario.getSenha())){
			String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhacriptografada);
		}
		Usuario usuarioSalvo = usuariorepository.save(usuario);
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<String> delete(@PathVariable("id") Long id) {
		usuariorepository.deleteById(id);
		return ResponseEntity.ok("ok");
	}
		
	
	/*passando parametros
	public ResponseEntity init(@RequestParam (value = "nome", required = true, defaultValue = "Nome não informado") String nome) {
		
		System.out.println("Parametro sendo recebido" + nome);
		
		return new ResponseEntity("Ola Usuario Rest Spring seu nome é: "+ nome, HttpStatus.OK);
	}
	
	public ResponseEntity<Usuario> init(){
		Usuario usuario = new Usuario();
		usuario.setId(50L);
		usuario.setLogin("jessikraujo");
		usuario.setNome("Jessica");
		usuario.setSenha("12345");
		//return ResponseEntity.ok(usuario); pra 1 usuario
		
		Usuario usuario2 = new Usuario();
		usuario2.setId(50L);
		usuario2.setLogin("jessikbrandao");
		usuario2.setNome("Jessica brandao");
		usuario2.setSenha("12345"); 
		
		List<Usuario> usuarios = new ArrayList<Usuario>();
		usuarios.add(usuario);
		usuarios.add(usuario2);
		return new ResponseEntity(usuarios, HttpStatus.OK);
	}*/
	
}
