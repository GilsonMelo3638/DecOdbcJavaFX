package model.entities;

import java.util.HashMap;
import java.util.Map;

public class Arquivo {

    public String dataHora;
    public String documento;

    public Arquivo() {} ;

    public Arquivo(String dataHora, String documento) {
        this.dataHora = dataHora;
        this.documento = documento;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String horaQuereyInicial() {
        int tamanho = dataHora.length();
        String horaInicial = (tamanho == 10) ? dataHora.substring(8, 10) : "00";
        return horaInicial;
    }

    public String horaQuereyFinal() {
        int tamanho = dataHora.length();
        String horaFinal = (tamanho == 10) ? dataHora.substring(8, 10) : "23";
        return horaFinal;
    }

    public Map<String, String> ConverterArquivoDigitado() {
        Map<String, String> resultado = new HashMap<>();

        String parametroApiRestIni = dataHora.substring(0, 4) + "-" + dataHora.substring(4, 6) + "-" +
                dataHora.substring(6, 8) + " " + horaQuereyInicial();
        String parametroApiRestFim = dataHora.substring(0, 4) + "-" + dataHora.substring(4, 6) + "-" +
                dataHora.substring(6, 8) + " " + horaQuereyFinal();
        String tipoDoc = documento;
        String parInicio = parametroApiRestIni + ":00:00";
        String parFim = parametroApiRestFim + ":59:59";

        resultado.put("parametroApiRestIni", parametroApiRestIni);
        resultado.put("parametroApiRestFim", parametroApiRestFim);
        resultado.put("tipoDoc", tipoDoc);
        resultado.put("parInicio", parInicio);
        resultado.put("parFim", parFim);

        // O restante do código permanece o mesmo

        return resultado;
    }

}


