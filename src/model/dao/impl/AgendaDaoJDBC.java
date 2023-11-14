package model.dao.impl;

import Validacoes.SituacaoProcessamento;
import Validacoes.TipoDoc;
import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.AgendaDao;
import model.entities.Agenda;
import model.entities.Evento;
import model.entities.NFe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AgendaDaoJDBC implements AgendaDao {

	private Connection conn;

	public AgendaDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Agenda obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO ADMDEC.DEC_AGENDA_EXTRACAO (TIPO_DOC, IND_SITUACAO, PAR_INICIO, PAR_FIM )  "
							+ "VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS'), TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS'))");
			st.setString(1, String.valueOf(obj.getTipo_doc()));
			st.setString(2, String.valueOf(obj.getInd_situacao()));
			st.setString(3, obj.getPar_inicio());
			st.setString(4, obj.getPar_fim());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					Long cod_agenda = rs.getLong(1);
					obj.setCod_agenda_extracao(cod_agenda);
				}
			}
			else {
				throw new DbException("Unexpected error! No rows affected!");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void updateAgenda(Agenda obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE ADMDEC.DEC_AGENDA_EXTRACAO " +
							"SET IND_SITUACAO = ? " +
							"WHERE COD_AGENDA_EXTRACAO = ?");

			st.setString(1, String.valueOf(obj.getInd_situacao()));
			st.setLong(2, obj.getCod_agenda_extracao());

			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}
	@Override
	public void deleteByCodAgenda(Long cod_agenda) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"DELETE FROM ADMDEC.DEC_AGENDA_EXTRACAO WHERE COD_AGENDA_EXTRACAO  =  ?");
			st.setLong(1, cod_agenda);

			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}
				@Override
				public Agenda findByCodAgenda(Long codAgenda) {
					PreparedStatement st = null;
					ResultSet rs = null;
					try {
						st = conn.prepareStatement(
								"SELECT * FROM ADMDEC.DEC_AGENDA_EXTRACAO WHERE COD_AGENDA_EXTRACAO  =  ?");
						st.setLong(1, codAgenda);
						rs = st.executeQuery();
						if (rs.next()) {
							Agenda obj = new Agenda();
							obj.setCod_agenda_extracao(rs.getLong("cod_agenda_extracao"));
							obj.setPar_inicio(rs.getString("par_inicio"));
							obj.setPar_fim(rs.getString("par_Fim"));
							obj.setNome_arquivo(rs.getString("nome_arquivo"));
							obj.setTipo_doc(TipoDoc.valueOf(rs.getString("tipo_doc")));
							obj.setInd_situacao(SituacaoProcessamento.valueOf(rs.getString("ind_situacao")));
							return obj;
		}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Agenda> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT * FROM DEC_AGENDA_EXTRACAO WHERE NOME_ARQUIVO = 'MDFeEvento20230908.xml' ORDER BY COD_AGENDA_EXTRACAO");
			rs = st.executeQuery();

			List<Agenda> list = new ArrayList<>();

			while (rs.next()) {
				Agenda obj = new Agenda();
				obj.setCod_agenda_extracao(rs.getLong("cod_agenda_extracao"));
				obj.setNome_arquivo(rs.getString("nome_arquivo"));
				obj.setQuantidade(rs.getBigDecimal("quantidade"));
				obj.setTipo_doc(TipoDoc.valueOf(rs.getString("tipo_doc")));
				obj.setPar_inicio(rs.getString("par_inicio"));
				obj.setPar_fim(rs.getString("par_fim"));
				obj.setInd_situacao(SituacaoProcessamento.valueOf(rs.getString("ind_situacao")));
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Agenda> findByEvento(Evento evento) {
		return null;
	}
}