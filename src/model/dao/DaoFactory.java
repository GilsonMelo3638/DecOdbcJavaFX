package model.dao;

import db.DB;
import model.dao.impl.AgendaDaoJDBC;

public class DaoFactory {

	public static AgendaDao createAgendaDao() {
		return new AgendaDaoJDBC(DB.getConnection());
	}
}
