package Validacoes;

public enum TipoDoc {
    BPe("diario"),
    BPeCanc("diario"),
    BPeEvento("diario"),
    BPeInut("diario"),
    CTe("hora"),
    CTeCanc("diario"),
    CTeEvento("hora"),
    CTeInut("diario"),
    CTeRFB("hora"),
    CTeRFBCanc("diario"),
    CTeRFBEvento("hora"),
    MDFe("hora"),
    MDFeCanc("diario"),
    MDFeEvento("hora"),
    MDFEInut("diario"),
    MDFeRFB("diario"),
    MDFeRFBCanc("diario"),
    MDFeRFBEvento("diario"),
    NFe("hora"),
    NFeCanc("diario"),
    NFeEvento("hora"),
    NFeInut("diario"),
    NF3e("diario"),
    NF3eCanc("diario"),
    NF3eInut("diario"),
    NF3eEvento("diario"),
    NFeRFB("diario"),
    NFeRFBCanc("diario"),
    NFeRFBEvento("diario"),
    NFCe("hora"),
    NFCeCanc("diario"),
    NFCeEvento("diario"),
    NFCeInut("diario");

private final String frequencia;

TipoDoc(String frequencia) {
    this.frequencia = frequencia;
}

public String getFrequencia() {
    return frequencia;
	}
}