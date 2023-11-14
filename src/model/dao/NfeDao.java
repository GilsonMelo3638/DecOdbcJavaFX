package model.dao;

import java.util.List;

import model.entities.Evento;
import model.entities.NFe;

public interface NfeDao {

	NFe findByChave(String id);
	List<NFe> findAll();
	List<NFe> findByEvento(Evento evento);
}