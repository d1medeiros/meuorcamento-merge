package org.meuorcamento.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "tipoConta")
@XmlEnum
public enum TipoConta {
	
	GASTOS, 
	GANHO,
	DEFAULT;

	
	public static TipoConta getTipoConta(String tipo) {
		if(tipo.toUpperCase() == TipoConta.GASTOS.name().toUpperCase()) {
			return TipoConta.GASTOS;
		}else if(tipo.toUpperCase() == TipoConta.GANHO.name().toUpperCase()) {
			return TipoConta.GANHO;
		}else {
			return TipoConta.DEFAULT;
		}
		
	}
}
