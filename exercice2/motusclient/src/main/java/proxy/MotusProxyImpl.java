package proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.*;
import proxy.dto.EtatPartie;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;

public class MotusProxyImpl implements MotusProxy{

    private HttpClient httpClient = HttpClient.newHttpClient();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String creerUnCompte(String pseudo) throws PseudoDejaPrisException {
        HttpRequest request = HttpRequest.newBuilder().build()
                .uri(URI.create("http://localhost:8080/motus/joueur?pseudo"+pseudo))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

    }

    @Override
    public String creerUnePartie(String tokenAuthentification) {
        return null;
    }

    @Override
    public EtatPartie proposerMot(String tokenPartie, String proposition) throws MotInexistantException, MaxNbCoupsException, TicketInvalideException {
        return null;
    }

    @Override
    public List<String> getPropositions(String tokenPartie) throws TicketInvalideException, PartieInexistanteException {
        return null;
    }

}
