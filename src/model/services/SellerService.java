package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {

	private SellerDao dao = DaoFactory.createSellerDao();

	// Método para recuperar uma lista de todos os departamentos.
	public List<Seller> findAll() {
		return dao.findAll();
	}

	// Método para salvar ou atualizar um departamento.
	public void saveOrUpdate(Seller obj) {
		if (obj.getId() == null) {
			dao.insert(obj);  // Se o ID do departamento for nulo, insere um novo departamento.
		} else {
			dao.update(obj);  // Caso contrário, atualiza um departamento existente.
		}
	}

	// Método para remover um departamento.
	public void remove(Seller obj) {
		dao.deleteById(obj.getId());  // Remove um departamento pelo ID.
	}
}
