package com.tiwpr.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.envers.Audited;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "klienci")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Audited
public class Client {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_client")
	private Integer idClient;

	private String name;

	private String surname;

	private String favouriteGameType;

	@OneToMany(mappedBy = "client")
	@JsonManagedReference(value="transaction-client")
	private List<Transaction> transactionList = new ArrayList<>();

	@Version
	private Integer version;

	public Client() {
	}

	public Integer getIdClient() {
		return idClient;
	}

	public void setIdClient(Integer idClient) {
		this.idClient = idClient;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getFavouriteGameType() {
		return favouriteGameType;
	}

	public void setFavouriteGameType(String favouriteGameType) {
		this.favouriteGameType = favouriteGameType;
	}

	public List<Transaction> getTransactionList() {
		return transactionList;
	}

	public void setTransactionList(List<Transaction> transactionList) {
		this.transactionList = transactionList;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
