import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.DynadotConfig;
import balbucio.dynadot4j.client.DomainClient;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class BulkSearchExample {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String apiKey = System.getenv("DYNADOT_APIKEY");
        String apiSecret = System.getenv("DYNADOT_APISECRET");

        // defina qualquer um a não ser que esteja utilizando a Key de produção (loucura inclusive)
        String domainName = JOptionPane.showInputDialog(null,
                "Qual será o domínio utilizado para testes?",
                "Dynadot4j Test Unit",
                JOptionPane.QUESTION_MESSAGE);

        if (domainName == null || domainName.isEmpty()) {
            domainName = System.getenv("DYNADOT_DOMAINNAME");
        }

        DynadotConfig config = Dynadot.createDefault()
                .endpointUrl("https://api-sandbox.dynadot.com")
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .debug(true)
                .build();

        Dynadot dynadot = new Dynadot(config);
        DomainClient domainClient = dynadot.getDomainClient();
        System.out.println(domainClient.searchBulk(domainName, "USD")
                .get());
    }
}
