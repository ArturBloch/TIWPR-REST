package com.tiwpr.rest.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Audited
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idTransaction;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference(value="transaction-boardgame")
	@JoinColumn(name = "id_boardgame")
	BoardGame boardGame;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference(value="transaction-client")
	@JoinColumn(name = "id_client")
	Client client;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfTransaction;

	private String typeOfTransaction;

	private Integer amount;

	@Version
	private Integer version;

	public Transaction() {

	}

	public Integer getIdTransaction() {
		return idTransaction;
	}

	public void setIdTransaction(Integer idTransaction) {
		this.idTransaction = idTransaction;
	}

	public BoardGame getBoardGame() {
		return boardGame;
	}

	public void setBoardGame(BoardGame boardGame) {
		this.boardGame = boardGame;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public LocalDate getDateOfTransaction() {
		return dateOfTransaction;
	}

	public String getTypeOfTransaction() {
		return typeOfTransaction;
	}

	public void setTypeOfTransaction(String typeOfTransaction) {
		this.typeOfTransaction = typeOfTransaction;
	}

	public void setDateOfTransaction(LocalDate dateOfTransaction) {
		this.dateOfTransaction = dateOfTransaction;
	}

	public Integer getAmount() {
		if (amount == null)
			return 0;
		return amount;
	}

	@JsonIgnore
	public boolean isSellTransaction() {
		if (typeOfTransaction == null)
			return false;
		return typeOfTransaction.toLowerCase().equals("sell");
	}

	@JsonIgnore
	public boolean isLendTransaction() {
		if (typeOfTransaction == null)
			return false;
		return typeOfTransaction.toLowerCase().equals("lend");
	}

	void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
