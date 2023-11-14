package model.entities;

import java.io.Serializable;
import java.util.Date;

public class Evento implements Serializable {

	private static final long serialVersionUID = 1L;

	private String chave;
	private Integer tpevento;
	private Date dhevento;
	private Integer seqevento;

	
	public Evento() {
	}

	public Evento(String chave, Integer tpevento, Date dhevento, Integer seqevento) {
		this.chave = chave;
		this.tpevento = tpevento;
		this.dhevento = dhevento;
		this.seqevento = seqevento;

	}
	public String getChave() {
		return chave;
	}
	public void setChave(String chave) {
		this.chave = chave;
	}
	public Integer getTpevento() {
		return tpevento;
	}
	public void setTpevento(Integer tpevento) {
		this.tpevento = tpevento;
	}
	public Date getDhevento() { return dhevento; }
	public void setDhevento(Date dhevento) { this.dhevento = dhevento; }
	public Integer getSeqevento() {
		return seqevento;
	}
	public void setSeqevento(Integer seqevento) {
		this.seqevento = seqevento;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chave == null) ? 0 : chave.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Evento other = (Evento) obj;
		if (chave == null) {
			if (other.chave != null)
				return false;
		} else if (!chave.equals(other.chave))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Evento [chave=" + chave + ", TpEvento=" + tpevento + ", DhEvento=" + dhevento + ", SeqEvento =" + seqevento + "]";
	}
}