package com.rafaelximenes.cursomc.services;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.rafaelximenes.cursomc.domain.Cidade;
import com.rafaelximenes.cursomc.domain.Cliente;
import com.rafaelximenes.cursomc.domain.Endereco;
import com.rafaelximenes.cursomc.domain.enums.Perfil;
import com.rafaelximenes.cursomc.domain.enums.TipoCliente;
import com.rafaelximenes.cursomc.dto.ClienteDTO;
import com.rafaelximenes.cursomc.dto.ClienteNewDTO;
import com.rafaelximenes.cursomc.repositories.ClienteRepository;
import com.rafaelximenes.cursomc.repositories.EnderecoRepository;
import com.rafaelximenes.cursomc.security.UserSS;
import com.rafaelximenes.cursomc.services.exception.AuthorizationException;
import com.rafaelximenes.cursomc.services.exception.DataIntegrityException;
import com.rafaelximenes.cursomc.services.exception.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private BCryptPasswordEncoder bp;

	@Autowired
	private ClienteRepository repo;

	@Autowired
	private EnderecoRepository enderecoRepository;

	@Autowired
	private S3Service s3Service;

	@Autowired
	private ImageService imageService;

	@Value("${img.prefix.client.profile}")
	private String prefixoImagem;

	@Value("${img.profile.size}")
	private Integer sizeImagem;

	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repo.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}

	public Cliente find(Integer id) {

		UserSS user = UserService.authenticated();
		if (user == null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
			throw new AuthorizationException("Acesso negado");
		}

		Optional<Cliente> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}

	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		obj = updateData(newObj, obj);
		return repo.save(obj);
	}

	private Cliente updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
		return newObj;
	}

	public void delete(Integer id) {
		find(id);
		try {
			repo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir um cliente com pedidos vinculados.");
		}
	}

	public List<Cliente> findAll() {
		return repo.findAll();
	}

	public Cliente findByEmail(String email) {

		UserSS user = UserService.authenticated();
		if (user == null || !user.hasRole(Perfil.ADMIN) && !email.equals(user.getUsername())) {
			throw new AuthorizationException("Acesso negado");
		}

		Cliente obj = repo.findByEmail(email);
		if (obj == null) {
			throw new ObjectNotFoundException(
					"Objeto não encontrado! Id: " + user.getId() + ", Tipo: " + Cliente.class.getName());
		}
		return obj;
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		@SuppressWarnings("deprecation")
		PageRequest pageRequest = new PageRequest(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}

	public Cliente fromDTO(ClienteDTO obj) {
		return new Cliente(obj.getId(), obj.getNome(), obj.getEmail(), null, null, null);
	}

	public Cliente fromDTO(ClienteNewDTO obj) {
		Cidade cidade = new Cidade(1, null, null);
		Cliente cli = new Cliente(null, obj.getNome(), obj.getEmail(), obj.getCpfOuCnpj(),
				TipoCliente.toEnum(obj.getTipoCliente()), bp.encode(obj.getSenha()));
		Endereco endereco = new Endereco(null, obj.getLogradouro(), obj.getNumero(), obj.getComplemento(),
				obj.getBairro(), obj.getCep(), cli, cidade);
		cli.getEnderecos().add(endereco);
		cli.getTelefones().add(obj.getTelefone1());
		if (obj.getTelefone2() != null)
			cli.getTelefones().add(obj.getTelefone2());
		if (obj.getTelefone3() != null)
			cli.getTelefones().add(obj.getTelefone3());
		return cli;

	}

	public URI uploadProfilePicture(MultipartFile multipartFile) {
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}

		Optional<Cliente> obj = repo.findById(user.getId());
		if (obj.isPresent()) {
			Cliente cliente = obj.get();
			BufferedImage img = imageService.getJpgImageFromFile(multipartFile);
			img = imageService.cropSquare(img);
			img = imageService.resize(img, sizeImagem);
			String fileName = prefixoImagem + cliente.getId() + ".jpg";
			return s3Service.uploadFile(imageService.getInputStream(img, "jpg"), fileName, "image");
		}
		throw new AuthorizationException("Erro ao converter imagem");
	}

}
