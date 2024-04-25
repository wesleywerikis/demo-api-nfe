package br.com.weswerikis.demoapinfe;

import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;
import br.com.swconsultoria.nfe.Nfe;
import br.com.swconsultoria.nfe.dom.ConfiguracoesNfe;
import br.com.swconsultoria.nfe.dom.enuns.AmbienteEnum;
import br.com.swconsultoria.nfe.dom.enuns.EstadosEnum;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TEnviNFe;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe;
import br.com.swconsultoria.nfe.util.ChaveUtil;
import br.com.swconsultoria.nfe.util.ConstantesUtil;
import br.com.swconsultoria.nfe.util.XmlNfeUtil;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
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
        infNFe.setEmit();
        infNFe.setDest();
        infNFe.setTotal();
        infNFe.getDet().add();
        infNFe.setTransp();
        infNFe.setPag();
        infNFe.setInfAdic();
        infNFe.setInfRespTec();

        return infNFe;
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
        ide.set
        ide.set
        ide.set
        ide.set

        return null;
    }

    private static void criaConfiguracoes() throws CertificadoException, FileNotFoundException {
        Certificado certificado = CertificadoService.certificadoPfx("C:\\certA1\\certificado.pfx", "123456");
        configuracoesNfe = ConfiguracoesNfe.criarConfiguracoes(EstadosEnum.PR, AmbienteEnum.HOMOLOGACAO, certificado, "C:\\certA1\\schemas");
    }
}
