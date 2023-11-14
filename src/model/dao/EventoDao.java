package model.dao;

import model.entities.Evento;

import java.util.List;

public interface EventoDao {

    Evento findById(String id);

    List<Evento> findAll();
}