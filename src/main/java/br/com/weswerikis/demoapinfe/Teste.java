package br.com.weswerikis.demoapinfe;

import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;
import br.com.swconsultoria.nfe.Nfe;
import br.com.swconsultoria.nfe.dom.ConfiguracoesNfe;
import br.com.swconsultoria.nfe.dom.enuns.AmbienteEnum;
import br.com.swconsultoria.nfe.dom.enuns.EstadosEnum;
import br.com.swconsultoria.nfe.schema_4.enviNFe.*;
import br.com.swconsultoria.nfe.util.ChaveUtil;
import br.com.swconsultoria.nfe.util.ConstantesUtil;
import br.com.swconsultoria.nfe.util.XmlNfeUtil;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Teste {
    public static void main(String[] args) {
        try {
            emiteNfe();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ChaveUtil chaveUtil;
    private static ConfiguracoesNfe configuracoesNfe;
    private static String cnpj;
    private static String modelo;
    private static String tipoEmissao;
    private static String cnf;
    private static int serie;
    private static int numero;
    private static LocalDateTime dataEmissao;

    private static void emiteNfe() throws Exception {

        cnpj = "02107526000109";
        modelo = "55";
        serie = 1;
        numero = 92723;
        tipoEmissao = "1";
        cnf = String.format("%08d", new Random().nextInt(99999999));
        dataEmissao = LocalDateTime.now();

        criaConfiguracoes();

        MontaChaveNFe(configuracoesNfe);

        TEnviNFe enviNFe = criaEnviNFe();
        enviNFe = Nfe.montaNfe(configuracoesNfe, enviNFe, true);
    }

    private static void MontaChaveNFe(ConfiguracoesNfe configuracoesNfe) {
        chaveUtil = new ChaveUtil(
                configuracoesNfe.getEstado(),
                cnpj,
                modelo,
                serie,
                numero,
                tipoEmissao,
                cnf,
                LocalDateTime.now()
        );
    }

    private static TEnviNFe criaEnviNFe() {
        TEnviNFe enviNFe = new TEnviNFe();
        enviNFe.setVersao(ConstantesUtil.VERSAO.NFE);
        enviNFe.setIdLote("1");
        enviNFe.setIndSinc("1");
        TNFe nfe = new TNFe();
        TNFe.InfNFe infNFe = getInfNFe();
        nfe.setInfNFe(infNFe);
        enviNFe.getNFe().add(nfe);
        return enviNFe;
    }

    private static TNFe.InfNFe getInfNFe() {
        TNFe.InfNFe infNFe = new TNFe.InfNFe();
        infNFe.setId(chaveUtil.getChaveNF());
        infNFe.setVersao(ConstantesUtil.VERSAO.NFE);

        infNFe.setIde(montaIde());
        infNFe.setEmit(montaEmitente());
        infNFe.setDest(montaDestinatario());
        infNFe.getDet().addAll(montaDet());
        infNFe.setTransp(montaTransportadora());
        infNFe.setPag(pagamento());
//      infNFe.setInfAdic();
        infNFe.setInfRespTec(montaRespTecnico());
        infNFe.setTotal(montaTotal());

        return infNFe;
    }

    private static TNFe.InfNFe.Total montaTotal() {
        TNFe.InfNFe.Total total = new TNFe.InfNFe.Total();
        TNFe.InfNFe.Total.ICMSTot icmsTot = new TNFe.InfNFe.Total.ICMSTot();
        icmsTot.setVBC("10.00");
        icmsTot.setVICMS("1.00");
        icmsTot.setVICMSDeson("0.00");
        icmsTot.setVFCP("0.00");
        icmsTot.setVBCST("0.00");
        icmsTot.setVST("0.00");
        icmsTot.setVFCPST("0.00");
        icmsTot.setVFCPSTRet("0.00");
        icmsTot.setVProd("10.00");
        icmsTot.setVFrete("0.00");
        icmsTot.setVSeg("0.00");
        icmsTot.setVDesc("0.00");
        icmsTot.setVII("0.00");
        icmsTot.setVIPI("0.00");
        icmsTot.setVIPIDevol("0.00");
        icmsTot.setVPIS("0.17");
        icmsTot.setVCOFINS("0.76");
        icmsTot.setVOutro("0.00");
        icmsTot.setVNF("10.00");
        total.setICMSTot(icmsTot);
        return total;
    }

    private static TInfRespTec montaRespTecnico() {
        TInfRespTec respTec = new TInfRespTec();
        respTec.setCNPJ("32330160000195"); // CNPJ - SW CONSULT - TESTE HOMOLOG
        respTec.setXContato("Wesley Werikis");
        respTec.setFone("wesleywerikis@email.com.br");
        respTec.setEmail("43999998888");
        return respTec;
    }

    private static TNFe.InfNFe.Pag pagamento() {
        TNFe.InfNFe.Pag pag = new TNFe.InfNFe.Pag();
        TNFe.InfNFe.Pag.DetPag detPag = new TNFe.InfNFe.Pag.DetPag();
        detPag.setTPag("01");
        detPag.setVPag("10.00");
        pag.getDetPag().add(detPag);
        return pag;
    }

    private static TNFe.InfNFe.Transp montaTransportadora() {
        TNFe.InfNFe.Transp transp = new TNFe.InfNFe.Transp();
        transp.setModFrete("9");
        return transp;
    }

    private static List<TNFe.InfNFe.Det> montaDet() {
        TNFe.InfNFe.Det det = new TNFe.InfNFe.Det();
        det.setNItem("1");
        det.setProd(montaProduto());
        det.setImposto(montaImposto());
        return Collections.singletonList(det);
    }

    private static TNFe.InfNFe.Det.Imposto montaImposto() {
        TNFe.InfNFe.Det.Imposto imposto = new TNFe.InfNFe.Det.Imposto();
        criaImpostoIcms(imposto);
        criaImpostoPis(imposto);
        criaImpostoCofins(imposto);
        return imposto;
    }

    private static void criaImpostoPis(TNFe.InfNFe.Det.Imposto imposto) {
        TNFe.InfNFe.Det.Imposto.PIS pis = new TNFe.InfNFe.Det.Imposto.PIS();
        TNFe.InfNFe.Det.Imposto.PIS.PISAliq pisAliq = new TNFe.InfNFe.Det.Imposto.PIS.PISAliq();
        pisAliq.setCST("01");
        pisAliq.setVBC("10.00");
        pisAliq.setPPIS("1.65");
        pisAliq.setVPIS("0.17");
        pis.setPISAliq(pisAliq);
        imposto.getContent().add(new ObjectFactory().createTNFeInfNFeDetImpostoPIS(pis));

    }
    private static void criaImpostoCofins(TNFe.InfNFe.Det.Imposto imposto) {
        TNFe.InfNFe.Det.Imposto.COFINS cofins = new TNFe.InfNFe.Det.Imposto.COFINS();
        TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq cofinsAliq = new TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq();
        cofinsAliq.setCST("01");
        cofinsAliq.setVBC("10.00");
        cofinsAliq.setPCOFINS("7.60");
        cofinsAliq.setVCOFINS("0.76");
        cofins.setCOFINSAliq(cofinsAliq);
        imposto.getContent().add(new ObjectFactory().createTNFeInfNFeDetImpostoCOFINS(cofins));

    }

    private static void criaImpostoIcms(TNFe.InfNFe.Det.Imposto imposto) {
        TNFe.InfNFe.Det.Imposto.ICMS icms = new TNFe.InfNFe.Det.Imposto.ICMS();
        TNFe.InfNFe.Det.Imposto.ICMS.ICMS00 icms00 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS00();
        icms00.setOrig("0");
        icms00.setModBC("0");
        icms00.setCST("00");
        icms00.setVBC("10.00");
        icms00.setPICMS("10");
        icms00.setVICMS("1.00");
        icms.setICMS00(icms00);
        imposto.getContent().add(new ObjectFactory().createTNFeInfNFeDetImpostoICMS(icms));
    }

    private static TNFe.InfNFe.Det.Prod montaProduto() {
        TNFe.InfNFe.Det.Prod produto = new TNFe.InfNFe.Det.Prod();
        produto.setCProd("123");
        produto.setCEAN("SEM GTIN");
        produto.setXProd("Produto ABC"); //"NOTA FISCAL EMITIDA EM AMBIENTE DE HOMOLOGAÇÃO - SEM VALOR FISCAL"
        produto.setNCM("27101932");
        produto.setCEST("0600500");
        produto.setIndEscala("S");
        produto.setCFOP("5405");
        produto.setUCom("UN");
        produto.setQCom("1");
        produto.setVUnCom("10");
        produto.setVProd("10.00");

        produto.setCEANTrib("SEM GTIN");
        produto.setUTrib("UN");
        produto.setQTrib("1");
        produto.setVUnTrib("1");
        produto.setIndTot("1");
        return produto;
    }

    private static TNFe.InfNFe.Dest montaDestinatario() {
        TNFe.InfNFe.Dest dest = new TNFe.InfNFe.Dest();
        dest.setXNome("Nome Empresa");
        dest.setCNPJ("75733147000190");
        dest.setIE("6010972300");
        dest.setIndIEDest("1");

        TEndereco enderecoEmitente = new TEndereco();
        enderecoEmitente.setXLgr("Rua Teste");
        enderecoEmitente.setNro("0");
        enderecoEmitente.setXCpl("QD 1 Lote 1");
        enderecoEmitente.setXBairro("Centro");
        enderecoEmitente.setCMun("4113700");
        enderecoEmitente.setXMun("LONDRINA");
        enderecoEmitente.setUF(TUf.PR);
        enderecoEmitente.setCEP("03671010");
        dest.setEnderDest(enderecoEmitente);
        return dest;
    }

    private static TNFe.InfNFe.Emit montaEmitente() {
        TNFe.InfNFe.Emit emit = new TNFe.InfNFe.Emit();
        emit.setXNome("Nome Empresa");
        emit.setCNPJ(cnpj);
        emit.setIE("9014158623");
        emit.setCRT("3");
        TEnderEmi enderecoEmitente = new TEnderEmi();
        enderecoEmitente.setXLgr("Rua Teste");
        enderecoEmitente.setNro("0");
        enderecoEmitente.setXCpl("QD 1 LOTE 1");
        enderecoEmitente.setXBairro("Centro");
        enderecoEmitente.setCMun("4113700");
        enderecoEmitente.setXMun("LONDRINA");
        enderecoEmitente.setUF(TUfEmi.valueOf(configuracoesNfe.getEstado().toString()));
        enderecoEmitente.setCEP("86000000");
        emit.setEnderEmit(enderecoEmitente);
        return emit;
    }

    private static TNFe.InfNFe.Ide montaIde() {
        TNFe.InfNFe.Ide ide = new TNFe.InfNFe.Ide();
        ide.setCUF(configuracoesNfe.getEstado().getCodigoUF());
        ide.setCNF(cnf);
        ide.setNatOp("Venda NFe");
        ide.setMod(modelo);
        ide.setSerie(String.valueOf(serie));
        ide.setNNF(String.valueOf(numero));
        ide.setDhEmi(XmlNfeUtil.dataNfe(dataEmissao));
        ide.setTpNF("1");
        ide.setIdDest("2");
        ide.setCMunFG("4113700");
        ide.setTpImp("1");
        ide.setTpEmis(tipoEmissao);
        ide.setCDV(chaveUtil.getDigitoVerificador());
        ide.setTpAmb(configuracoesNfe.getAmbiente().getCodigo());
        ide.setFinNFe("1");
        ide.setIndFinal("1");
        ide.setIndPres("1");
        ide.setProcEmi("0");
        ide.setVerProc("1.0.0");
        return ide;
    }

    private static void criaConfiguracoes() throws CertificadoException, FileNotFoundException {
        Certificado certificado = CertificadoService.certificadoPfx("C:\\certA1\\certificado.pfx", "123456");
        configuracoesNfe = ConfiguracoesNfe.criarConfiguracoes(EstadosEnum.PR, AmbienteEnum.HOMOLOGACAO, certificado, "C:\\certA1\\schemas");
    }
}
