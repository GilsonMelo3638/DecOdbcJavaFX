package model.entities;

import java.math.BigDecimal;
import java.util.Objects;

import Validacoes.SituacaoProcessamento;
import Validacoes.TipoDoc;

public class Agenda {

    private Long cod_agenda_extracao;
    private TipoDoc tipo_doc;
    private SituacaoProcessamento ind_situacao;
    private String par_inicio;
    private String par_fim;
    private String nome_arquivo;
    private BigDecimal quantidade;

    public Agenda() {}

    public Agenda(Long cod_agenda_extracao, TipoDoc tipo_doc, SituacaoProcessamento ind_situacao, String par_inicio, String par_fim) {
        this.cod_agenda_extracao = cod_agenda_extracao;
        this.tipo_doc = tipo_doc;
        this.ind_situacao = ind_situacao;
        this.par_inicio = par_inicio;
        this.par_fim = par_fim;
    }

  //  @Id
    public Long getCod_agenda_extracao() {
        return cod_agenda_extracao;
    }

    public void setCod_agenda_extracao(Long cod_agenda_extracao) {
        this.cod_agenda_extracao = cod_agenda_extracao;
    }

    public TipoDoc getTipo_doc() {
        return tipo_doc;
    }

    public void setTipo_doc(TipoDoc tipo_doc) {
        this.tipo_doc = tipo_doc;
    }

    public SituacaoProcessamento getInd_situacao() {
        return ind_situacao;
    }

    public void setInd_situacao(SituacaoProcessamento ind_situacao) {
        this.ind_situacao = ind_situacao;
    }

    public String getPar_inicio() {
        return par_inicio;
    }

    public void setPar_inicio(String par_inicio) {
        this.par_inicio = par_inicio;
    }

    public String getPar_fim() {
        return par_fim;
    }

    public void setPar_fim(String par_fim) {
        this.par_fim = par_fim;
    }

    public String getNome_arquivo() {
        return nome_arquivo;
    }

    public void setNome_arquivo(String nome_arquivo) {
        this.nome_arquivo = nome_arquivo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agenda agenda = (Agenda) o;
        return Objects.equals(cod_agenda_extracao, agenda.cod_agenda_extracao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cod_agenda_extracao);
    }

    @Override
    public String toString() {
        return "Agenda{" +
                "cod_agenda_extracao=" + cod_agenda_extracao +
                ", Nome do arquivo= " + nome_arquivo +
                ", Quantidade = " + quantidade +
                ", par_inicio=" + par_inicio +
                ", par_fim=" + par_fim +
                ", tipo_doc=" + tipo_doc +
                ", ind_situacao=" + ind_situacao +
                '}';
    }
}
