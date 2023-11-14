package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.NfeDao;
import model.entities.Evento;
import model.entities.NFe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NfeDaoJDBC implements NfeDao {
	private Connection conn;
	
	public NfeDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public NFe findByChave(String id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT "
							+ "B.NSUDF,"
							+ " A.CHAVE,"
							+ " A.DHEMI,"
							+ " A.EMITENTE,"
							+ " A.UF_EMITENTE,"
							+ " A.DESTINATARIO,"
							+ " A.UF_DESTINATARIO, "
							+  "B.XML_DOCUMENTO,"
							+ " B.TP_EVENTO, "
							+ "B.DHEVENTO, "
							+ "B.SEQ_EVENTO "
							+ "FROM "
							+ "ADMDEC.DEC_DFE_NFE A "
							+ "INNER JOIN "
							+ "ADMDEC.DEC_DFE_NFE_CANCELAMENTO B ON A.CHAVE = B.CHAVE "
							+ "WHERE "
							+ "A.CHAVE =  ?");
			
			st.setString(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Evento dep = instantiateEvento(rs);
				NFe obj = instantiateNfe(rs, dep);
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
	private NFe instantiateNfe(ResultSet rs, Evento event) throws SQLException {
		NFe obj = new NFe();
		obj.setNsudf(rs.getLong("nsudf"));
		obj.setChave(rs.getString("chave"));
		obj.setEmitente(rs.getString("emitente"));
		obj.setUf_emitente(rs.getString("uf_emitente"));
		obj.setDestinatario(rs.getString("destinatario"));
		obj.setUf_destinatario(rs.getString("uf_destinatario"));
		obj.setDhemi(rs.getDate("dhemi"));
		obj.setXmlDocumento(rs.getString("xml_Documento"));
		obj.setEvento(event);
		return obj;
	}

	private Evento instantiateEvento(ResultSet rs) throws SQLException {
		Evento event = new Evento();
		event.setChave(rs.getString("chave"));
		event.setTpevento(rs.getInt("tp_evento"));
		event.setDhevento(rs.getDate("dhevento"));
		event.setSeqevento(rs.getInt("seq_evento"));
		return event;
	}

	@Override
	public List<NFe> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT "
							+ "B.NSUDF, "
							+ "A.CHAVE, "
							+ "A.DHEMI, "
							+ "A.EMITENTE, "
							+ "A.UF_EMITENTE, "
							+ "A.DESTINATARIO, "
							+ "A.UF_DESTINATARIO, "
							+ "xmlserialize(document A.XML_DOCUMENTO.extract('//nfeProc/NFe/infNFe/emit',"
							+ "'xmlns=\"http://www.portalfiscal.inf.br/nfe\"') AS CLOB) XML_DOCUMENTO, "
							+ "B.TP_EVENTO, "
							+ "B.DHEVENTO, "
							+ "B.SEQ_EVENTO "
							+ "FROM "
							+ "ADMDEC.DEC_DFE_NFE A "
							+ "INNER JOIN "
							+ "ADMDEC.DEC_DFE_NFE_CANCELAMENTO B ON A.CHAVE = B.CHAVE "
							+ "WHERE "
							+ "A.DESTINATARIO = '25860054149' "
							+ "ORDER BY "
							+ "DHEVENTO");

			rs = st.executeQuery();
			
			List<NFe> list = new ArrayList<>();
			Map<Integer, Evento> map = new HashMap<>();
			
			while (rs.next()) {
				
				Evento event = map.get(rs.getInt("seq_evento"));
				
				if (event == null) {
					event = instantiateEvento(rs);
					map.put(rs.getInt("seq_evento"), event);
				}
				
				NFe obj = instantiateNfe(rs, event);
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
	public List<NFe> findByEvento(Evento evento) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT "
							+ "B.NSUDF, "
							+ "A.CHAVE, "
							+ "A.DHEMI, "
							+ "A.EMITENTE, "
							+ "A.UF_EMITENTE, "
							+ "A.DESTINATARIO, "
							+ "A.UF_DESTINATARIO, "
							+"B.XML_DOCUMENTO, "
							+ "B.TP_EVENTO, "
							+ "B.DHEVENTO, "
							+ "B.SEQ_EVENTO "
							+ "FROM "
							+ "ADMDEC.DEC_DFE_NFE A "
							+ "INNER JOIN "
							+ "ADMDEC.DEC_DFE_NFE_EVENTO B ON A.CHAVE = B.CHAVE "
							+ "WHERE "
							+ "A.CHAVE =  ? "
							+ "ORDER BY DHEVENTO");

			st.setString(1, evento.getChave());
			
			rs = st.executeQuery();
			
			List<NFe> list = new ArrayList<>();
			Map<String, Evento> map = new HashMap<>();
			
			while (rs.next()) {
				
				Evento event = map.get(rs.getString("seq_evento"));
				
				if (event == null) {
					event = instantiateEvento(rs);
					map.put(rs.getString("seq_evento"), event);
				}
				
				NFe obj = instantiateNfe(rs, event);
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

}
