package model.dao;

import java.util.List;

import Validacoes.TipoDoc;
import model.entities.Agenda;
import model.entities.Evento;

public interface AgendaDao {

    void insert(Agenda obj);

    void updateAgenda(Agenda obj);

    void deleteByCodAgenda(Long id);

    Agenda findByCodAgenda(Long codAgenda);

    List<Agenda> findAll();

    List<Agenda> findByEvento(Evento evento);
    
    List<Agenda> findAllByTipoDoc(TipoDoc tipoDoc);

    void commit(); // Adicione o método commit
}