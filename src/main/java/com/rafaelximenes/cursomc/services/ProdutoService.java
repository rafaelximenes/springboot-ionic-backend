package com.rafaelximenes.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.rafaelximenes.cursomc.domain.Categoria;
import com.rafaelximenes.cursomc.domain.Produto;
import com.rafaelximenes.cursomc.repositories.CategoriaRepository;
import com.rafaelximenes.cursomc.repositories.ProdutoRepository;
import com.rafaelximenes.cursomc.services.exception.ObjectNotFoundException;

@Service
public class ProdutoService {

	@Autowired
	private ProdutoRepository repo;
	
	@Autowired
	private CategoriaRepository categoriaRepository;

	public Produto find(Integer id) {
		Optional<Produto> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
		"Objeto não encontrado! Id: " + id + ", Tipo: " + Produto.class.getName()));
	}
	
	public Page<Produto> search(String nome, List<Integer> ids,Integer page, Integer linesPerPage, String orderBy, String direction) {
		@SuppressWarnings("deprecation")
		PageRequest pageRequest = new PageRequest(page, linesPerPage, Direction.valueOf(direction), orderBy);
		List<Categoria> listCat = categoriaRepository.findAllById(ids);
		return repo.findDistinctByNomeContainingAndCategoriasIn(nome,listCat,pageRequest);
		
	}
}
