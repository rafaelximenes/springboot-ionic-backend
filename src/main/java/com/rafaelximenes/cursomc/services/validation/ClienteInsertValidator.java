package com.rafaelximenes.cursomc.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.rafaelximenes.cursomc.domain.Cliente;
import com.rafaelximenes.cursomc.domain.enums.TipoCliente;
import com.rafaelximenes.cursomc.dto.ClienteNewDTO;
import com.rafaelximenes.cursomc.repositories.ClienteRepository;
import com.rafaelximenes.cursomc.resources.exceptions.FieldMessage;
import com.rafaelximenes.cursomc.services.validation.utils.BR;

public class ClienteInsertValidator implements ConstraintValidator<ClienteInsert, ClienteNewDTO> {
	
	@Autowired
	private ClienteRepository repo;
	
	@Override
	public void initialize(ClienteInsert ann) {
	}

	@Override
	public boolean isValid(ClienteNewDTO objDto, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();
		
		if(objDto.getTipoCliente().equals(TipoCliente.PESSOAFISICA.getCod()) && !BR.isValidCpf(objDto.getCpfOuCnpj())) {
			list.add(new FieldMessage("CpfOuCnpj", "CPF inválido"));
		}
		if(objDto.getTipoCliente().equals(TipoCliente.PESSOAJURIDICA.getCod()) && !BR.isValidCpnj(objDto.getCpfOuCnpj())) {
			list.add(new FieldMessage("CpfOuCnpj", "CNPJ inválido"));
		}
		
		Cliente c = repo.findByEmail(objDto.getEmail());
		if(c!=null) {
			list.add(new FieldMessage("Email", "E-mail já cadastrado"));
		}

		// inclua os testes aqui, inserindo erros na lista

		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}