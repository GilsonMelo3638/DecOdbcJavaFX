package model.dao;

import model.entities.Evento;
import model.entities.Agenda;

import java.util.List;

public interface AgendaDao {

	void insert(Agenda obj);
	void updateAgenda(Agenda obj);
	void deleteByCodAgenda(Long id);
	Agenda findByCodAgenda(Long codAgenda);
	List<Agenda> findAll();
	List<Agenda> findByEvento(Evento evento);
}