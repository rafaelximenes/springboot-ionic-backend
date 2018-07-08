package com.rafaelximenes.cursomc.domain.enums;

public enum TipoCliente {

	PESSOAFISICA(1, "F"),
	PESSOAJURIDICA(2, "J");
	
	private Integer cod;
	private String descricao;
	
	private TipoCliente(Integer cod, String descricao) {
		this.cod = cod;
		this.descricao = descricao;
	}
	
	public Integer getCod() {
		return cod;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public static TipoCliente toEnum(Integer cod) {
		if (cod == null) {
			return null;
		}
		
		for (TipoCliente x : TipoCliente.values()) {
			if(x.getCod().equals(cod)) {
				return x;
			}
		}
		throw new IllegalArgumentException("Id inválido: " + cod);
	}
}
