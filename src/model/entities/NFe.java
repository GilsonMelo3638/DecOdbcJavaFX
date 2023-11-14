package model.entities;

import java.io.Serializable;
import java.util.Date;

public class NFe implements Serializable {
    private static int lineNumber = 1;
    private static final long serialVersionUID = 1L;

    private String chave;
    private Long nsudf;
    private Date dhemi;
    private String xmlDocumento;
    private String emitente;
	private String destinatario;
	private String uf_destinatario;

	public String getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

	public String getUf_destinatario() {
		return uf_destinatario;
	}

	public void setUf_destinatario(String uf_destinatario) {
		this.uf_destinatario = uf_destinatario;
	}

	public String getUf_emitente() {
		return uf_emitente;
	}

	public void setUf_emitente(String uf_emitente) {
		this.uf_emitente = uf_emitente;
	}

	private String uf_emitente;

    public String getEmitente() {
        return emitente;
    }

    public void setEmitente(String emitente) {
        this.emitente = emitente;
    }


    private Evento evento;

    public NFe() {
    }

    public NFe(Long nsudf, String chave, Date dhemi, String xmlDocumento, Evento evento) {
        this.nsudf = nsudf;
        this.chave = chave;
        this.dhemi = dhemi;
        this.xmlDocumento = xmlDocumento;
        this.evento = evento;
    }

    public Long getNsudf() {
        return nsudf;
    }

    public void setNsudf(Long nsudf) {
        this.nsudf = nsudf;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public Date getDhemi() {
        return dhemi;
    }

    public void setDhemi(Date dhemi) {
        this.dhemi = dhemi;
    }

    public String getXmlDocumento() {
        return xmlDocumento;
    }

    public void setXmlDocumento(String xmlDocumento) {
        this.xmlDocumento = xmlDocumento;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
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
        NFe other = (NFe) obj;
        if (chave == null) {
            if (other.chave != null)
                return false;
        } else if (!chave.equals(other.chave))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Linha ").append(lineNumber++).append(": "); // Auto incremento

        sb.append("NFE [chave=").append(chave).append(", nsudf=").append(nsudf)
                .append(", Emitente=").append(emitente).append(", UF Emitente=").append(uf_emitente)
                .append(", Destinatário=").append(destinatario).append(", UF Destinatário=").append(uf_destinatario)
                .append(", dhemi=").append(dhemi).append(", xmlDocumento=").append(xmlDocumento)
                .append(", Evento=").append(evento).append("]");

        return sb.toString();
    }

}
