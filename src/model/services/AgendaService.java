package model.services;

import java.util.List;

import Validacoes.TipoDoc;
import model.dao.AgendaDao;
import model.dao.DaoFactory;
import model.entities.Agenda;

public class AgendaService {

    private AgendaDao dao = DaoFactory.createAgendaDao();

    // Método para recuperar uma lista de todos os departamentos.
    public List<Agenda> findAll() {
        return dao.findAll();
    }

    // Método para recuperar uma agenda por código.
    public Agenda findByCodAgenda(Long codAgenda) {
        return dao.findByCodAgenda(codAgenda);
    }

    // Método para salvar ou atualizar uma agenda.
    public void saveOrUpdate(Agenda obj) {
        if (obj.getCod_agenda_extracao() == null) {
            dao.insert(obj);  // Se o ID da agenda for nulo, insere uma nova agenda.
        } else {
            dao.updateAgenda(obj);  // Caso contrário, atualiza uma agenda existente.
        }
    }

    // Método para remover uma agenda.
    public void remove(Agenda obj) {
        dao.deleteByCodAgenda(obj.getCod_agenda_extracao());  // Remove uma agenda pelo ID.
    }
    
    public List<Agenda> findAllByTipoDoc(TipoDoc tipoDoc, int dias) {
        return dao.findAllByTipoDoc(tipoDoc, dias);
    }
    
}