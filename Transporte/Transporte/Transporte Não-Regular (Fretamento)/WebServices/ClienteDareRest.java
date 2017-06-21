
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Cliente para os serviços REST de emissão e consulta de DARE, e consulta de pagamento.
 * @author Lindiomar-VP
 * @version 1.0.15
 * @date 2016.02.04
 */
public class ClienteDareRest {
    public static final String requestMethodPOST = "POST";
    public static final String requestMethodGET = "GET";
    public static final String contentTypeXML = "application/xml";
    public static final String contentTypeJSON = "application/json";
    public static final String acceptURL = "text/url";
    public static final String acceptPLAIN = "text/plain";
    
    public static void main(String args[]) {
        // Monta xml para envio dos parâmetros, os parâmetros vazios geralmente são utilizados exclusivamente pela SEFAZ-GO, na geração do DARE para os
        //demais órgãos podem ser ignorados.
        String siglaOrgaoEmissor = "AGR";
        String numeroControleOrgaoEmissor = "QG4ENU4QDC";
        String siglaSistemaEmissor = "";
        String numeroControleSistemaEmissor = "";
        Integer codgTipoDocumentoArrec = null;
        Integer codgReceita = 370;
        Integer codgDetalheReceita = 14;
        Integer codgCondPagamento = null;
        Integer codgApuracao = null;
        Integer codgDetalheApuracao = null;
        Integer refeMesApuracao = null;
        Integer refeAnoApuracao = null; 
        Integer numrParcela = null;
        Integer codgTipoDocumentoOrigem = 16;
        String numrDocumentoOrigem = "2016000001"; 
        String dataVencimentoTributo = "";
        String dataCalcPagamento = "30/04/2016";
        String valorOriginal = "9.999,99";
        String valorMulta = "";
        String valorMultaAcaoFiscal = "";
        String valorJuros = "";
        String valorJurosFinanceiros = "";
        String valorAtualizacaoMonetaria = "";
        Integer codgLei = null;
        Integer numeroFaseLeiCalculo = null;
        Integer numrInscricaoContrib = null;
        String numrPlacaVeiculo = ""; 
        String numrCPFContrib = "02150163861";
        String numrCNPJContrib = "";
        String nomeRazaoSocialContrib = "NOME CONTRIBUINTE TESTE";
        String enderecoEmitente = "RUA TESTE, 999, SETOR TESTE - CEP 74.000-000";
        Integer codgDddTelefoneContrib = 62;
        Integer numrTelefoneContrib = 99999999;
        Integer codgMunicipioContrib = null;
        String nomeMunicipioContrib = "GOIÂNIA";
        String informacoesComplementares = "DARE DE TESTE";
        
        String xml = gerarXML(siglaOrgaoEmissor, numeroControleOrgaoEmissor, siglaSistemaEmissor, numeroControleSistemaEmissor, 
                            codgReceita, codgDetalheReceita, codgCondPagamento, codgApuracao, refeMesApuracao, refeAnoApuracao, 
                            numrParcela, numrInscricaoContrib, informacoesComplementares, codgTipoDocumentoOrigem, numrDocumentoOrigem, 
                            codgTipoDocumentoArrec, dataVencimentoTributo, dataCalcPagamento, valorOriginal, valorMulta, valorMultaAcaoFiscal, 
                            valorJuros, valorJurosFinanceiros, valorAtualizacaoMonetaria, codgLei, numeroFaseLeiCalculo, codgDetalheApuracao, numrPlacaVeiculo, 
                            numrCPFContrib, numrCNPJContrib, nomeRazaoSocialContrib, enderecoEmitente, codgDddTelefoneContrib, numrTelefoneContrib, 
                            codgMunicipioContrib, nomeMunicipioContrib);

        String json = gerarJSON(siglaOrgaoEmissor, numeroControleOrgaoEmissor, siglaSistemaEmissor, numeroControleSistemaEmissor, 
                            codgReceita, codgDetalheReceita, codgCondPagamento, codgApuracao, refeMesApuracao, refeAnoApuracao, 
                            numrParcela, numrInscricaoContrib, informacoesComplementares, codgTipoDocumentoOrigem, numrDocumentoOrigem, 
                            codgTipoDocumentoArrec, dataVencimentoTributo, dataCalcPagamento, valorOriginal, valorMulta, valorMultaAcaoFiscal, 
                            valorJuros, valorJurosFinanceiros, valorAtualizacaoMonetaria, codgLei, numeroFaseLeiCalculo, codgDetalheApuracao, numrPlacaVeiculo, 
                            numrCPFContrib, numrCNPJContrib, nomeRazaoSocialContrib, enderecoEmitente, codgDddTelefoneContrib, numrTelefoneContrib, 
                            codgMunicipioContrib, nomeMunicipioContrib);
        
        System.out.println("<<<<<< Dados no formato XML >>>>> : "+xml);
        System.out.println("<<<<<< Dados no formato JSON >>>>> : "+json);
        
        //Serviço de emissão do DARE 5.1 com retorno da URL de exibição do DARE. A entrada de dados no formato XML (poderia ser JSON)
        System.out.println("<<<<<< DARE Emitido URL >>>>> : "+emitirDareRetornaURL(xml, contentTypeXML));
        
        //Serviço de emissão do DARE 5.1 com retorno do número de processamento. A entrada de dados no formato JSON (poderia ser XML)
        String numeroProcessamento = emitirDareRetornaNumeroProcessamento(json, contentTypeJSON);
        System.out.println("<<<<<< DARE Emitido Número de Processamento >>>>> : "+numeroProcessamento);

        //Serviço de consulta dos dados do DARE pelo número do processamento do DARE
        System.out.println("<<<<<< DARE Consultado >>>>> : "+consultarDare(numeroProcessamento));
        
        //Serviço de consulta dos dados de pagamento pelo número do processamento do DARE (Fixado o número do processamento de um DARE TESTE pago)
        System.out.println("<<<<<< Pagamento do DARE Número 12000000431600113 >>>>> : "+consultarPagamentoDare("12000000431600113"));
    }
    
    public static HttpURLConnection conectar(URL url, String requestMethod, String contentType, String accept){
        HttpURLConnection connection = null;
        try{
            // Abre e configura parâmetros da conexão com o servidor
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod(requestMethod);
            // Configura tipos de conteúdo de envio e retorno
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Accept", accept);
            return connection;
        }catch(Exception e){
            return null;
        } finally {
            if(connection != null) {
                    connection.disconnect();
            }
        }
    }

    private static String retorno(HttpURLConnection con){
        StringBuilder sb = new StringBuilder();
        try {
            // Monta retorno do método
            InputStream errorIs = con.getErrorStream();
            if(errorIs != null) {
                // Caso tenha ocorrido algum erro, devolve mensagem detalhada
                readInputStream(sb, errorIs);
            } else {
                // Caso a geração tenha sido bem sucedida, concatena retorno enviado pelo sistema ARR
                String resposta = con.getHeaderField("Location");
                if(resposta == null || resposta.isEmpty()){
                    readInputStream(sb, con.getInputStream());
                }else{
                    sb.append(resposta);
                }
            }
        } catch(IOException e) {
            sb.append("999 - Erro ao realizar chamada ao servico de geração de DARE - " + e.getMessage());
        }
        return sb.toString();
    }
    
    private static void readInputStream(StringBuilder sb, InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 1);
        String inputLine;
        while ((inputLine = bufferedReader.readLine()) != null) {
            sb.append(inputLine);
        }
        bufferedReader.close();
    }
    
    
    public static String consultarDare(String numrReservadoProcessamento) {
        HttpURLConnection con = null;
        try {
            // Escreve conteúdo de envio na requisição
            URL url = new URL("http://10.6.0.36/arr-rs/api/dare/"+numrReservadoProcessamento);
            con = conectar(url, requestMethodGET, contentTypeJSON, "");//request GET e contentType obrigatório, mas tanto faz JSON ou XML
        } catch (IOException e) {
            return "Falha ao conectar.";
        }
        return retorno(con);
    }

    public static String consultarPagamentoDare(String numrReservadoProcessamento) {
        HttpURLConnection con = null;
        try {
            // Escreve conteúdo de envio na requisição
            URL url = new URL("http://10.6.0.36/arr-rs/api/pagamento/"+numrReservadoProcessamento);
            con = conectar(url, requestMethodGET, contentTypeXML, "");//request GET e contentType obrigatório, mas tanto faz JSON ou XML
        } catch (IOException e) {
            return "Falha ao conectar.";
        }
        return retorno(con);
    }

    public static URL emitirDareRetornaURL(String dadosDare, String contentType) {
        try {
            //Na emissão o contentType define o formato dos dados (XML ou JSON)
            //O accept define o tipo de retorno (URL de exibição do DARE ou Numero do Processamento)
            return new URL(emitir(dadosDare, contentType, acceptURL));
        } catch (Exception e) {
            System.out.println("Falha ao emitir.");
            return null;
        }
    }

    public static String emitirDareRetornaNumeroProcessamento(String dadosDare, String contentType) {
        try {
            //Na emissão o contentType define o formato dos dados (XML ou JSON)
            //O accept define o tipo de retorno (URL de exibição do DARE ou Numero do Processamento)
            return emitir(dadosDare, contentType, acceptPLAIN);
        } catch (Exception e) {
            System.out.println("Falha ao emitir.");
            return null;
        }
    }
    
    private static String emitir(String dadosDare, String contentType, String accept) {
        HttpURLConnection con = null;
        try {
            // Escreve conteúdo de envio na requisição
            URL url = new URL("http://10.6.0.36/arr-rs/api/dare");
            con = conectar(url, requestMethodPOST, contentType, accept);
            OutputStream os = con.getOutputStream();
            os.write(dadosDare.getBytes("UTF-8"));
            os.flush();
        } catch (IOException e) {
            return "Falha ao conectar.";
        }
        return retorno(con);
    }

    private static String gerarXML(
                    String siglaOrgaoEmissor,
                    String numeroControleOrgaoEmissor,
                    String siglaSistemaEmissor,
                    String numeroControleSistemaEmissor,
                    Integer codgReceita,
                    Integer codgDetalheReceita,
                    Integer codgCondPagamento,
                    Integer codgApuracao,
                    Integer refeMesApuracao,
                    Integer refeAnoApuracao,
                    Integer numrParcela,
                    Integer numrInscricaoContrib,
                    String informacoesComplementares,
                    Integer codgTipoDocumentoOrigem,
                    String numrDocumentoOrigem,
                    Integer codgTipoDocumentoArrec,
                    String dataVencimentoTributo,
                    String dataCalcPagamento,
                    String valorOriginal,
                    String valorMulta,
                    String valorMultaAcaoFiscal,
                    String valorJuros,
                    String valorJurosFinanceiros,
                    String valorAtualizacaoMonetaria,
                    Integer codgLei,
                    Integer numeroFaseLeiCalculo,
                    Integer codgDetalheApuracao,
                    String numrPlacaVeiculo,
                    String numrCPFContrib,
                    String numrCNPJContrib,
                    String nomeRazaoSocialContrib,
                    String enderecoEmitente,
                    Integer codgDddTelefoneContrib,
                    Integer numrTelefoneContrib,
                    Integer codgMunicipioContrib,
                    String nomeMunicipioContrib
                    ) {
        
                StringBuilder gerarDareXmlRetornoSb = new StringBuilder();
		gerarDareXmlRetornoSb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		gerarDareXmlRetornoSb.append("<dare>");
		gerarDareXmlRetornoSb.append("<siglaOrgaoEmissor>").append(siglaOrgaoEmissor).append("</siglaOrgaoEmissor>");
		gerarDareXmlRetornoSb.append("<numeroControleOrgaoEmissor>").append(numeroControleOrgaoEmissor).append("</numeroControleOrgaoEmissor>");
		gerarDareXmlRetornoSb.append("<siglaSistemaEmissor>").append(siglaSistemaEmissor).append("</siglaSistemaEmissor>");
		gerarDareXmlRetornoSb.append("<numeroControleSistemaEmissor>").append(numeroControleSistemaEmissor).append("</numeroControleSistemaEmissor>");
		gerarDareXmlRetornoSb.append("<codgReceita>").append(codgReceita).append("</codgReceita>");
		gerarDareXmlRetornoSb.append("<codgDetalheReceita>").append(codgDetalheReceita).append("</codgDetalheReceita>");
		gerarDareXmlRetornoSb.append("<codgCondPagamento>").append(codgCondPagamento).append("</codgCondPagamento>");
		gerarDareXmlRetornoSb.append("<codgApuracao>").append(codgApuracao).append("</codgApuracao>");
		gerarDareXmlRetornoSb.append("<codgDetalheApuracao>").append(codgDetalheApuracao).append("</codgDetalheApuracao>");
		gerarDareXmlRetornoSb.append("<refeMesApuracao>").append(refeMesApuracao).append("</refeMesApuracao>");
		gerarDareXmlRetornoSb.append("<refeAnoApuracao>").append(refeAnoApuracao).append("</refeAnoApuracao>");
		gerarDareXmlRetornoSb.append("<numrParcela>").append(numrParcela).append("</numrParcela>");
		gerarDareXmlRetornoSb.append("<numrInscricaoContrib>").append(numrInscricaoContrib).append("</numrInscricaoContrib>");
		gerarDareXmlRetornoSb.append("<informacoesComplementares>").append(informacoesComplementares).append("</informacoesComplementares>");
		gerarDareXmlRetornoSb.append("<codgTipoDocumentoOrigem>").append(codgTipoDocumentoOrigem).append("</codgTipoDocumentoOrigem>");
		gerarDareXmlRetornoSb.append("<numrDocumentoOrigem>").append(numrDocumentoOrigem).append("</numrDocumentoOrigem>");
		gerarDareXmlRetornoSb.append("<codgTipoDocumentoArrec>").append(codgTipoDocumentoArrec).append("</codgTipoDocumentoArrec>");
		gerarDareXmlRetornoSb.append("<dataVencimentoTributo>").append(dataVencimentoTributo).append("</dataVencimentoTributo>");
		gerarDareXmlRetornoSb.append("<dataCalcPagamento>").append(dataCalcPagamento).append("</dataCalcPagamento>");
		gerarDareXmlRetornoSb.append("<valorOriginal>").append(valorOriginal).append("</valorOriginal>");
		gerarDareXmlRetornoSb.append("<valorMulta>").append(valorMulta).append("</valorMulta>");
		gerarDareXmlRetornoSb.append("<valorMultaAcaoFiscal>").append(valorMultaAcaoFiscal).append("</valorMultaAcaoFiscal>");
		gerarDareXmlRetornoSb.append("<valorJuros>").append(valorJuros).append("</valorJuros>");
		gerarDareXmlRetornoSb.append("<valorJurosFinanceiros>").append(valorJurosFinanceiros).append("</valorJurosFinanceiros>");
		gerarDareXmlRetornoSb.append("<valorAtualizacaoMonetaria>").append(valorAtualizacaoMonetaria).append("</valorAtualizacaoMonetaria>");
		gerarDareXmlRetornoSb.append("<codgLei>").append(codgLei).append("</codgLei>");
		gerarDareXmlRetornoSb.append("<numeroFaseLeiCalculo>").append(numeroFaseLeiCalculo).append("</numeroFaseLeiCalculo>");
		gerarDareXmlRetornoSb.append("<numrPlacaVeiculo>").append(numrPlacaVeiculo).append("</numrPlacaVeiculo>");
		gerarDareXmlRetornoSb.append("<numrCPFContrib>").append(numrCPFContrib).append("</numrCPFContrib>");
		gerarDareXmlRetornoSb.append("<numrCNPJContrib>").append(numrCNPJContrib).append("</numrCNPJContrib>");
		gerarDareXmlRetornoSb.append("<nomeRazaoSocialContrib>").append(nomeRazaoSocialContrib).append("</nomeRazaoSocialContrib>");
		gerarDareXmlRetornoSb.append("<enderecoEmitente>").append(enderecoEmitente).append("</enderecoEmitente>");
		gerarDareXmlRetornoSb.append("<codgDddTelefoneContrib>").append(codgDddTelefoneContrib).append("</codgDddTelefoneContrib>");
		gerarDareXmlRetornoSb.append("<numrTelefoneContrib>").append(numrTelefoneContrib).append("</numrTelefoneContrib>");
		gerarDareXmlRetornoSb.append("<codgMunicipioContrib>").append(codgMunicipioContrib).append("</codgMunicipioContrib>");
		gerarDareXmlRetornoSb.append("<nomeMunicipioContrib>").append(nomeMunicipioContrib).append("</nomeMunicipioContrib>");
		gerarDareXmlRetornoSb.append("</dare>");
                return gerarDareXmlRetornoSb.toString();
    }

    private static String gerarJSON(
                    String siglaOrgaoEmissor,
                    String numeroControleOrgaoEmissor,
                    String siglaSistemaEmissor,
                    String numeroControleSistemaEmissor,
                    Integer codgReceita,
                    Integer codgDetalheReceita,
                    Integer codgCondPagamento,
                    Integer codgApuracao,
                    Integer refeMesApuracao,
                    Integer refeAnoApuracao,
                    Integer numrParcela,
                    Integer numrInscricaoContrib,
                    String informacoesComplementares,
                    Integer codgTipoDocumentoOrigem,
                    String numrDocumentoOrigem,
                    Integer codgTipoDocumentoArrec,
                    String dataVencimentoTributo,
                    String dataCalcPagamento,
                    String valorOriginal,
                    String valorMulta,
                    String valorMultaAcaoFiscal,
                    String valorJuros,
                    String valorJurosFinanceiros,
                    String valorAtualizacaoMonetaria,
                    Integer codgLei,
                    Integer numeroFaseLeiCalculo,
                    Integer codgDetalheApuracao,
                    String numrPlacaVeiculo,
                    String numrCPFContrib,
                    String numrCNPJContrib,
                    String nomeRazaoSocialContrib,
                    String enderecoEmitente,
                    Integer codgDddTelefoneContrib,
                    Integer numrTelefoneContrib,
                    Integer codgMunicipioContrib,
                    String nomeMunicipioContrib
                    ) {

                siglaOrgaoEmissor = siglaOrgaoEmissor.equals("")?null:"\""+siglaOrgaoEmissor+"\"";
                numeroControleOrgaoEmissor = numeroControleOrgaoEmissor.equals("")?null:"\""+numeroControleOrgaoEmissor+"\"";
                siglaSistemaEmissor = siglaSistemaEmissor.equals("")?null:"\""+siglaSistemaEmissor+"\"";
                numeroControleSistemaEmissor = numeroControleSistemaEmissor.equals("")?null:"\""+numeroControleSistemaEmissor+"\"";
                informacoesComplementares = informacoesComplementares.equals("")?null:"\""+informacoesComplementares+"\"";
                numrDocumentoOrigem = numrDocumentoOrigem.equals("")?null:"\""+numrDocumentoOrigem+"\"";
                dataVencimentoTributo = dataVencimentoTributo.equals("")?null:"\""+dataVencimentoTributo+"\"";
                dataCalcPagamento = dataCalcPagamento.equals("")?null:"\""+dataCalcPagamento+"\"";
                valorOriginal = valorOriginal.equals("")?null:"\""+valorOriginal+"\"";
                valorMulta = valorMulta.equals("")?null:"\""+valorMulta+"\"";
                valorMultaAcaoFiscal = valorMultaAcaoFiscal.equals("")?null:"\""+valorMultaAcaoFiscal+"\"";
                valorJuros = valorJuros.equals("")?null:"\""+valorJuros+"\"";
                valorJurosFinanceiros = valorJurosFinanceiros.equals("")?null:"\""+valorJurosFinanceiros+"\"";
                valorAtualizacaoMonetaria = valorAtualizacaoMonetaria.equals("")?null:"\""+valorAtualizacaoMonetaria+"\"";
                numrPlacaVeiculo = numrPlacaVeiculo.equals("")?null:"\""+numrPlacaVeiculo+"\"";
                numrCPFContrib = numrCPFContrib.equals("")?null:"\""+numrCPFContrib+"\"";
                numrCNPJContrib = numrCNPJContrib.equals("")?null:"\""+numrCNPJContrib+"\"";
                nomeRazaoSocialContrib = nomeRazaoSocialContrib.equals("")?null:"\""+nomeRazaoSocialContrib+"\"";
                enderecoEmitente = enderecoEmitente.equals("")?null:"\""+enderecoEmitente+"\"";
                nomeMunicipioContrib = nomeMunicipioContrib.equals("")?null:"\""+nomeMunicipioContrib+"\"";
        
        
            StringBuilder gerarDareXmlRetornoSb = new StringBuilder();
            gerarDareXmlRetornoSb.append("{");
            gerarDareXmlRetornoSb.append("\"siglaOrgaoEmissor\":").append(siglaOrgaoEmissor).append(",");
            gerarDareXmlRetornoSb.append("\"numeroControleOrgaoEmissor\":").append(numeroControleOrgaoEmissor).append(",");
            gerarDareXmlRetornoSb.append("\"siglaSistemaEmissor\":").append(siglaSistemaEmissor).append(",");
            gerarDareXmlRetornoSb.append("\"numeroControleSistemaEmissor\":").append(numeroControleSistemaEmissor).append(",");
            gerarDareXmlRetornoSb.append("\"codgReceita\":").append(codgReceita).append(",");
            gerarDareXmlRetornoSb.append("\"codgDetalheReceita\":").append(codgDetalheReceita).append(",");
            gerarDareXmlRetornoSb.append("\"codgCondPagamento\":").append(codgCondPagamento).append(",");
            gerarDareXmlRetornoSb.append("\"codgApuracao\":").append(codgApuracao).append(",");
            gerarDareXmlRetornoSb.append("\"codgDetalheApuracao\":").append(codgDetalheApuracao).append(",");
            gerarDareXmlRetornoSb.append("\"refeMesApuracao\":").append(refeMesApuracao).append(",");
            gerarDareXmlRetornoSb.append("\"refeAnoApuracao\":").append(refeAnoApuracao).append(",");
            gerarDareXmlRetornoSb.append("\"numrParcela\":").append(numrParcela).append(",");
            gerarDareXmlRetornoSb.append("\"numrInscricaoContrib\":").append(numrInscricaoContrib).append(",");
            gerarDareXmlRetornoSb.append("\"informacoesComplementares\":").append(informacoesComplementares).append(",");
            gerarDareXmlRetornoSb.append("\"codgTipoDocumentoOrigem\":").append(codgTipoDocumentoOrigem).append(",");
            gerarDareXmlRetornoSb.append("\"numrDocumentoOrigem\":").append(numrDocumentoOrigem).append(",");
            gerarDareXmlRetornoSb.append("\"codgTipoDocumentoArrec\":").append(codgTipoDocumentoArrec).append(",");
            gerarDareXmlRetornoSb.append("\"dataVencimentoTributo\":").append(dataVencimentoTributo).append(",");
            gerarDareXmlRetornoSb.append("\"dataCalcPagamento\":").append(dataCalcPagamento).append(",");
            gerarDareXmlRetornoSb.append("\"valorOriginal\":").append(valorOriginal).append(",");
            gerarDareXmlRetornoSb.append("\"valorMulta\":").append(valorMulta).append(",");
            gerarDareXmlRetornoSb.append("\"valorMultaAcaoFiscal\":").append(valorMultaAcaoFiscal).append(",");
            gerarDareXmlRetornoSb.append("\"valorJuros\":").append(valorJuros).append(",");
            gerarDareXmlRetornoSb.append("\"valorJurosFinanceiros\":").append(valorJurosFinanceiros).append(",");
            gerarDareXmlRetornoSb.append("\"valorAtualizacaoMonetaria\":").append(valorAtualizacaoMonetaria).append(",");
            gerarDareXmlRetornoSb.append("\"codgLei\":").append(codgLei).append(",");
            gerarDareXmlRetornoSb.append("\"numeroFaseLeiCalculo\":").append(numeroFaseLeiCalculo).append(",");
            gerarDareXmlRetornoSb.append("\"numrPlacaVeiculo\":").append(numrPlacaVeiculo).append(",");
            gerarDareXmlRetornoSb.append("\"numrCPFContrib\":").append(numrCPFContrib).append(",");
            gerarDareXmlRetornoSb.append("\"numrCNPJContrib\":").append(numrCNPJContrib).append(",");
            gerarDareXmlRetornoSb.append("\"nomeRazaoSocialContrib\":").append(nomeRazaoSocialContrib).append(",");
            gerarDareXmlRetornoSb.append("\"enderecoEmitente\":").append(enderecoEmitente).append(",");
            gerarDareXmlRetornoSb.append("\"codgDddTelefoneContrib\":").append(codgDddTelefoneContrib).append(",");
            gerarDareXmlRetornoSb.append("\"numrTelefoneContrib\":").append(numrTelefoneContrib).append(",");
            gerarDareXmlRetornoSb.append("\"codgMunicipioContrib\":").append(codgMunicipioContrib).append(",");
            gerarDareXmlRetornoSb.append("\"nomeMunicipioContrib\":").append(nomeMunicipioContrib);
            gerarDareXmlRetornoSb.append("}");
            return gerarDareXmlRetornoSb.toString();
    }
}
