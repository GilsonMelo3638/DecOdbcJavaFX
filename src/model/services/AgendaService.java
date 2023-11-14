package model.services;

import java.util.List;

import model.dao.AgendaDao;
import model.dao.DaoFactory;
import model.entities.Agenda;

public class AgendaService {

	private AgendaDao dao = DaoFactory.createAgendaDao();

	// Método para recuperar uma lista de todos os departamentos.
	public List<Agenda> findAll() {
		return dao.findAll();
	}

	// Método para salvar ou atualizar um departamento.
	public void saveOrUpdate(Agenda obj) {
		if (obj.getCod_agenda_extracao() == null) {
			dao.insert(obj);  // Se o ID do departamento for nulo, insere um novo departamento.
		} else {
			dao.updateAgenda(obj);  // Caso contrário, atualiza um departamento existente.
		}
	}

	// Método para remover um departamento.
	public void remove(Agenda obj) {
		dao.deleteByCodAgenda(obj.getCod_agenda_extracao());  // Remove um departamento pelo ID.
	}
}
