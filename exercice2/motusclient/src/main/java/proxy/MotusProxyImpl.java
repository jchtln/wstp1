package proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.*;
import proxy.dto.EtatPartie;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class MotusProxyImpl implements MotusProxy{

    private HttpClient httpClient = HttpClient.newHttpClient();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String creerUnCompte(String pseudo) throws PseudoDejaPrisException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/motus/joueur"))
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("pseudo="+pseudo))
                .build();


        try {
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(httpResponse.statusCode()==201){
                String resultat = httpResponse.headers().firstValue("token").get();
                return resultat;
            }
            else {
                throw new PseudoDejaPrisException();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Soucis");
        }

    }


    @Override
    public String creerUnePartie(String tokenAuthentification) {

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/motus/partie"))
                .header("token","tokenAuthentification")
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (httpResponse.statusCode()){

                case 201 : {
                    String resultat = httpResponse.headers().firstValue("tokenPartie").get();
                    return resultat;
                }

                case 400 | 401 : {
                    throw new TicketInvalideException();
                }

                default: {
                    throw new RuntimeException("Code retour inattendu");
                }
            }

        } catch (IOException | InterruptedException | TicketInvalideException e) {
            throw new RuntimeException("SOUCIS");
        }

    }

    @Override
    public EtatPartie proposerMot(String tokenPartie, String proposition) throws MotInexistantException, MaxNbCoupsException, TicketInvalideException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/motus/partie"))
                .header("tokenPartie", tokenPartie)
                .header("Accept","application/json")
                .header("Content-Type","application/x-www-form-urlencoded")
                .PUT(HttpRequest.BodyPublishers.ofString("proposition="+proposition))
                .build();


        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


            switch (response.statusCode()) {

                case 200: {
                    String body = response.body();
                    EtatPartie etatPartie = this.objectMapper.readValue(body,EtatPartie.class);
                    return etatPartie;
                }

                case 404: {
                    throw new MotInexistantException();
                }

                case 406: {
                    throw new MaxNbCoupsException();
                }

                case 400: {
                    throw new TicketInvalideException();
                }

                default: {
                    throw new RuntimeException("Code inconnu");
                }
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("ploufff");
        }

    }

    @Override
    public List<String> getPropositions(String tokenPartie) throws TicketInvalideException, PartieInexistanteException {
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8080/motus/partie?tokenPartie"))
//                .GET(HttpRequest.BodyPublishers)
        return null;
   }

}
