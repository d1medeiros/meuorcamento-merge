package org.meuorcamento.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement(name="usuario")
public class Usuario {
	
	@Id 
	@SequenceGenerator(name="EMP_SEQ", allocationSize=25, initialValue=333333)
	@GeneratedValue(strategy=GenerationType.IDENTITY, generator="EMP_SEQ")
	private long id;
	private String nome;
	private String login;
	private String senha;
	private boolean estado;
	private LocalDate ultimoAcesso;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public boolean isEstado() {
		return estado;
	}
	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	public LocalDate getUltimoAcesso() {
		return ultimoAcesso;
	}
	public void setUltimoAcesso(LocalDate ultimoAcesso) {
		this.ultimoAcesso = ultimoAcesso;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
}
