package com.tiwpr.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "boardGames")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Audited
public class BoardGame {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_boardgame")
	private Integer idBoardGame;

	private String boardgameName;

	private Double communityRating;

	private String author;

	private String category;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfPremiere;

	private double price;

	private Integer copiesToSell;

	private Integer copiesToLend;

	@Version
	private Integer version;

	@OneToMany(mappedBy = "boardGame")
	@JsonManagedReference(value="transaction-boardgame")
	private List<Transaction> transactionList = new ArrayList<>();

	public BoardGame() {
	}

	public Integer getIdBoardGame() {
		return idBoardGame;
	}

	public void setIdBoardGame(Integer idBoardGame) {
		this.idBoardGame = idBoardGame;
	}

	public String getBoardgameName() {
		return boardgameName;
	}

	public void setBoardgameName(String boardgameName) {
		this.boardgameName = boardgameName;
	}

	public Double getCommunityRating() {
		return communityRating;
	}

	public void setCommunityRating(Double communityRating) {
		this.communityRating = communityRating;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public LocalDate getDateOfPremiere() {
		return dateOfPremiere;
	}

	public void setDateOfPremiere(LocalDate dateOfPremiere) {
		this.dateOfPremiere = dateOfPremiere;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Integer getCopiesToSell() {
		return copiesToSell;
	}

	public void setCopiesToSell(Integer copiesToSell) {
		this.copiesToSell = copiesToSell;
	}

	public Integer getCopiesToLend() {
		return copiesToLend;
	}

	public void setCopiesToLend(Integer copiesToLend) {
		this.copiesToLend = copiesToLend;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public List<Transaction> getTransactionList() {
		return transactionList;
	}

	public void setTransactionList(List<Transaction> transactionList) {
		this.transactionList = transactionList;
	}

	public boolean lend(int amount){
		if(copiesToLend == null || copiesToLend < amount) return false;
		copiesToLend = copiesToLend - amount;
		return true;
	}

	public boolean sell(int amount){
		if(copiesToSell == null || copiesToSell < amount) return false;
		copiesToSell = copiesToSell - amount;
		return true;
	}

	@Override
	public String toString() {
		return "BoardGame{" +
			"idBoardGame=" + idBoardGame +
			", boardgameName='" + boardgameName + '\'' +
			", communityRating=" + communityRating +
			", author='" + author + '\'' +
			", category='" + category + '\'' +
			", dateOfPremiere=" + dateOfPremiere +
			", price=" + price +
			", copiesToSell=" + copiesToSell +
			", copiesToLend=" + copiesToLend +
			", version=" + version +
			", transactionList=" + transactionList +
			'}';
	}
}
