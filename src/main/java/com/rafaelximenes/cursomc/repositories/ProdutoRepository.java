package com.rafaelximenes.cursomc.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.rafaelximenes.cursomc.domain.Categoria;
import com.rafaelximenes.cursomc.domain.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer>{

	//@Transactional
	//@Query("select distinct obj FROM Produto obj Inner Join obj.categorias cat where obj.nome like %:nome% and cat IN :categorias")
	//Page<Produto> search(@Param("nome") String nome,@Param("categorias") List<Categoria> listCat, Pageable pageRequest);
	
	@Transactional
	Page<Produto> findDistinctByNomeContainingAndCategoriasIn(String nome, List<Categoria> categorias, Pageable pageRequest);

}
